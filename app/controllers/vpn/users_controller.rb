require 'brahma/web/table'
require 'brahma/web/form'
require 'brahma/web/dialog'
require 'brahma/web/validator'
require 'brahma/web/response'
require 'brahma/services/client'
require 'brahma/services/vpn'
require 'brahma/services/site'
require 'brahma/factory'

class Vpn::UsersController < ApplicationController
  before_action :require_login

  def index
    user_id = current_user.fetch(:id)
    c_id = params[:client_id]
    tab = Brahma::Web::Table.new "/vpn/users?client_id=#{c_id}", '用户列表', %w(ID 名称 状态 创建日期)
    client = Brahma::ClientService.get c_id, user_id, :vpn
    if client && client.enable?
      host = Brahma::VpnService.get_host(client.id)
      host.users.each do |u|
        tab.insert [u.id, u.username, u.state, u.created], [
            ['info', 'GET', "/vpn/users/#{u.id}", '查看'],
            ['primary', 'GET', "/vpn/users/#{u.id}/edit", '修改密码'],
            ['warning', 'GET', "/vpn/users/#{u.id}/state", '状态变更'],
            ['danger', 'DELETE', "/vpn/users/#{u.id}", '删除']
        ]
      end
      tab.toolbar = [['primary', 'GET', "/vpn/users/new?client_id=#{c_id}", '新增']]
      tab.ok = true
    else
      tab.add '没有权限'
    end
    render json: tab.to_h
  end

  def destroy
    user = Vpn::User.find_by params[:id]
    dlg = Brahma::Web::Dialog.new
    if can_edit?(user)
      Brahma::LogService.add "删除VPN用户#{user.username}", current_user.fetch(:id)
      user.destroy
      dlg.ok = true
    else
      dlg.add '没有权限'
    end
    render(json: dlg.to_h)
  end

  def show
    user = Vpn::User.find_by id: params[:id]
    if can_edit?(user)
      list = Brahma::Web::List.new "VPN用户[#{user.id}]"
      list.add "用户名：#{user.username}"
      list.add "状态：#{user.state}"
      list.add "创建时间： #{user.created}"
      render json: list.to_h
    else
      not_found
    end
  end

  def update
    vat = Brahma::Web::Validator.new params
    vat.password? :password, true
    user = Vpn::User.find_by id: params[:id]
    unless can_edit?(user)
      vat.add '没有权限'
    end
    dlg = Brahma::Web::Dialog.new
    if vat.ok?
      user.update password: Brahma::Factory.instance.encryptor.encrypt(params[:password])
      dlg.ok = true
    else
      dlg.data += vat.messages
    end
    render json: dlg.to_h
  end

  def edit
    user = Vpn::User.find_by id: params[:id]
    fm = Brahma::Web::Form.new '', "/vpn/users/#{params[:id]}"
    if can_edit?(user)
      fm.label = "编辑用户[#{user.username}]"
      fm.password 'password', '密码'
      fm.password 'passwordRe', '再次输入'
      fm.method = 'PUT'
      fm.ok = true
    else
      fm.add '没有权限'
    end
    render json: fm.to_h
  end

  def create
    user_id = current_user.fetch(:id)
    vat = Brahma::Web::Validator.new params
    vat.empty? :username, '用户名'
    vat.password? :password, true

    client = Brahma::ClientService.get(params[:client_id], user_id, :vpn)
    if client && client.enable?
      host = Brahma::VpnService.get_host client.id
      if Vpn::User.find_by username: params[:username], host_id: host.id
        vat.add '用户名已存在'
      end
    else
      vat.add '没有权限'
    end

    dlg = Brahma::Web::Dialog.new
    if vat.ok?
      Vpn::User.create username: params[:username], password: Brahma::Factory.instance.encryptor.encrypt(params[:password]),
                       host_id: host.id, created: Time.now
      dlg.ok = true
    else
      dlg.data += vat.messages
    end
    render json: dlg.to_h
  end

  def new
    if current_user
      fm = Brahma::Web::Form.new '增加VPN用户', '/vpn/users'
      fm.hidden 'client_id', params[:client_id]
      fm.text 'username', '用户名'
      fm.password 'password', '登录密码'
      fm.password 'passwordRe', '再次输入'
      fm.ok = true
      render json: fm.to_h
    else
      not_found
    end
  end

  def state
    case request.method
      when 'GET'
        fm = Brahma::Web::Form.new '', "/vpn/users/#{params[:id]}/state"
        user = Vpn::User.find_by id: params[:id]
        if can_edit?(user)
          fm.label = "修改状态[#{user.username}]"
          fm.radio 'state', '状态', user.state, [%w(enable 启用), %w(disable 禁用)]
          fm.ok = true
        else
          fm.add '没有权限'
        end
        render json: fm.to_h
      when 'POST'
        dlg = Brahma::Web::Dialog.new
        user = Vpn::User.find_by id: params[:id]
        if can_edit?(user)
          user.update state: params[:state]
          Brahma::LogService.add "修改VPN用户#{user.username}状态为#{params[:state]}"
          dlg.ok = true
        else
          dlg.add '没有权限'
        end
        render json: dlg.to_h
      else
        not_found
    end

  end

  private
  def can_edit?(vpn_user)
    vpn_user && vpn_user.host.client.user_id == current_user.fetch(:id)
  end
end
