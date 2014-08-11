require 'brahma/web/table'
require 'brahma/web/form'
require 'brahma/web/dialog'
require 'brahma/web/validator'
require 'brahma/web/response'
require 'brahma/services/client'
require 'brahma/services/email'
require 'brahma/services/site'
require 'brahma/factory'

class Email::UsersController < ApplicationController
  before_action :require_login


  def index
    user_id = current_user.id
    c_id = params[:client_id]
    tab = Brahma::Web::Table.new "/email/users?client_id=#{c_id}", '用户列表', %w(ID 名称 状态 创建日期)
    client = Brahma::ClientService.get c_id, user_id, :email
    if client && client.enable?
      host = Brahma::EmailService.get_host(client.id)
      host.domains.each do |d|
        d.users.each do |u|
          tab.insert [u.id, "#{u.username}@#{u.domain.name}", u.state, u.created], [
              ['info', 'GET', "/email/users/#{u.id}", '查看'],
              ['primary', 'GET', "/email/users/#{u.id}/edit", '修改密码'],
              ['warning', 'GET', "/email/users/#{u.id}/state", '状态变更'],
              ['danger', 'DELETE', "/email/users/#{u.id}", '删除']
          ]
        end
      end
      tab.toolbar = [['primary', 'GET', "/email/users/new?client_id=#{c_id}", '新增']]
      tab.ok = true
    else
      tab.add '没有权限'
    end
    render json: tab.to_h
  end

  def destroy
    user = Email::User.find_by params[:id]
    dlg = Brahma::Web::Dialog.new
    if can_edit?(user)
      Brahma::LogService.add "删除Email用户#{user.username}", current_user.id
      user.destroy
      dlg.ok = true
    else
      dlg.add '没有权限'
    end
    render(json: dlg.to_h)
  end

  def show
    user = Email::User.find_by id: params[:id]
    if can_edit?(user)
      list = Brahma::Web::List.new "EMAIL用户[#{user.id}]"
      list.add "用户名：#{user.username}@#{user.domain.name}"
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
    user = Email::User.find_by id: params[:id]
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
    user = Email::User.find_by id: params[:id]
    fm = Brahma::Web::Form.new '', "/email/users/#{params[:id]}"
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

    vat = Brahma::Web::Validator.new params
    vat.empty? :username, '用户名'
    vat.password? :password, true

    domain = Email::Domain.find_by id: params[:domain_id]
    if domain && domain.host.client.enable?
      if Email::User.find_by username: params[:username], domain_id: params[:domain_id]
        vat.add '用户名已存在'
      end
    else
      vat.add '没有权限'
    end


    dlg = Brahma::Web::Dialog.new
    if vat.ok?
      Email::User.create username: params[:username], password: Brahma::Factory.instance.encryptor.encrypt(params[:password]),
                         domain_id: params[:domain_id], created: Time.now
      dlg.ok = true
    else
      dlg.data += vat.messages
    end
    render json: dlg.to_h
  end

  def new
    if current_user
      fm = Brahma::Web::Form.new '增加邮箱用户', '/email/users'
      fm.select 'domain_id', '域', '', Email::Host.find_by(client_id: params[:client_id]).domains.map { |d| [d.id, d.name] }
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
        fm = Brahma::Web::Form.new '', "/email/users/#{params[:id]}/state"
        user = Email::User.find_by id: params[:id]
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
        user = Email::User.find_by id: params[:id]
        if can_edit?(user)
          user.update state: params[:state]
          Brahma::LogService.add "修改邮箱用户#{user.username}状态为#{params[:state]}"
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
  def can_edit?(email_user)
    email_user && email_user.domain.host.client.user_id == current_user.id
  end
end
