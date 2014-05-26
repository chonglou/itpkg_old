require 'brahma/web/table'
require 'brahma/web/form'
require 'brahma/web/dialog'
require 'brahma/web/fall'
require 'brahma/web/validator'
require 'brahma/web/response'
require 'brahma/services/site'
require 'brahma/utils/string_helper'

class ClientsController < ApplicationController
  before_action :require_login

  def index
    user = current_user
    clients = Client.select('id, name, flag, state, created').where(user_id: user.fetch(:id)).order(created: :desc)
    tab = Brahma::Web::Table.new '/clients', '终端列表', %w(ID 名称 类型 状态 创建日期)
    clients.each do |c|
      tab.insert [c.id, c.name, c.flag, c.state, c.created], [
          ['info', 'GET', "/clients/#{c.id}", '查看'],
          ['warning', 'GET', "/clients/#{c.id}/edit", '编辑'],
      #['danger', 'DELETE', "/clients/#{c.id}", '删除']
      ]
    end
    tab.toolbar = [%w(primary GET /clients/new 新增)]
    tab.ok = true
    render json: tab.to_h
  end

  def destroy
    dlg = Brahma::Web::Dialog.new
    dlg.add '暂不提供删除功能'
    render(json: dlg.to_h)
  end

  def show
    id = params[:id]
    if id
      c = Client.find_by id: id, user_id: current_user.fetch(:id)
      if c
        list = Brahma::Web::List.new "终端[#{c.id}]"
        list.add "名称：#{c.name}"
        list.add "类型：#{c.flag}"
        list.add "状态：#{c.state}"
        list.add "创建时间： #{c.created}"
        list.add "详细信息： #{c.details}"
        render(json: list.to_h) and return
      end
    end
    not_found
  end

  def update
    vat = Brahma::Web::Validator.new params
    vat.empty? :name, '名称'
    c = Client.find_by id: params[:id], user_id: current_user.fetch(:id)
    unless c
      vat.add '没有权限'
    end
    dlg = Brahma::Web::Dialog.new
    if vat.ok?
      c.update details: params[:details], name: params[:name]
      dlg.ok = true
    else
      dlg.data += vat.messages
    end
    render json: dlg.to_h
  end

  def edit

    c = Client.find_by id: params[:id], user_id: current_user.fetch(:id)
    fm = Brahma::Web::Form.new '编辑终端', "/clients/#{params[:id]}"
    if c
      fm.text 'name', '名称', c.name
      fm.html 'details', '详情', c.details
      fm.method = 'PUT'
      fm.ok = true
    else
      fm.add '没有权限'
    end
    render json: fm.to_h

  end

  def create
    vat = Brahma::Web::Validator.new params
    vat.empty? :name, '名称'
    dlg = Brahma::Web::Dialog.new
    if vat.ok?
      sh = Brahma::Utils::StringHelper
      Client.create user_id: current_user.fetch(:id), name: params[:name], details: params[:details],
                    secret: sh.rand_s!(64), serial: sh.uuid,
                    flag: params[:flag], state: :submit, created: Time.now
      dlg.ok = true
    else
      dlg.data += vat.messages
    end
    render json: dlg.to_h
  end

  def new
    if current_user
      fm = Brahma::Web::Form.new '增加终端', '/clients'
      fm.text 'name', '名称'
      fm.radio 'flag', '类型', 'firewall', [['firewall', '防火墙'], ['cdn', 'CDN'], ['vpn', 'VPN'], ['dns', 'DNS'], ['email', '邮件'], ['monitor', '监控']]
      fm.html 'details', '内容'
      fm.ok = true
      render json: fm.to_h
    else
      not_found
    end
  end

end
