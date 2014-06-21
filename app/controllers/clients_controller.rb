require 'json'
require 'brahma/web/table'
require 'brahma/web/form'
require 'brahma/web/dialog'
require 'brahma/web/fall'
require 'brahma/web/validator'
require 'brahma/web/response'
require 'brahma/services/site'
require 'brahma/services/client'

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
      c = Brahma::ClientService.get id, current_user.fetch(:id)
      if c
        list = Brahma::Web::List.new "终端[#{c.id}]"
        list.add "名称：#{c.name}"
        list.add "类型：#{c.flag}"
        list.add "状态：#{c.state}"
        list.add "配置文件: <a href='/clients/#{c.id}/demo'>点击下载</a>"
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
    c = Brahma::ClientService.get params[:id], current_user.fetch(:id)
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

    c = Brahma::ClientService.get params[:id], current_user.fetch(:id)
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
      serial, secret=Brahma::ClientService.generate
      Client.create user_id: current_user.fetch(:id), name: params[:name], details: params[:details],
                    secret: secret, serial: serial,
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
      fm.radio 'flag', '类型', 'firewall', [
          %w(firewall 防火墙),
          %w(cdn CDN),
          %w(vpn VPN),
          %w(dns DNS),
          %w(email 邮件),
          %w(monitor 监控)
      ]
      fm.html 'details', '内容'
      fm.ok = true
      render json: fm.to_h
    else
      not_found
    end
  end

  def state
    user_id = current_user.fetch(:id)
    c = Brahma::ClientService.get params[:id], user_id
    if c
      case request.method
        when 'GET'
          fm = Brahma::Web::Form.new '状态管理', "/clients/#{c.id}/state"
          fm.radio 'state', '状态', c.state, [%w(enable 启用), %w(disable 禁用)]
          fm.ok = true
          render json: fm.to_h and return
        when 'POST'
          dlg = Brahma::Web::Dialog.new
          c.update state: params[:state]
          Brahma::LogService.add "变更终端[#{c.id}]状态为[#{params[:state]}]", user_id
          dlg.ok = true
          render json: dlg.to_h and return
        else
      end
    end
    not_found
  end

  def reset
    user_id = current_user.fetch(:id)
    c = Brahma::ClientService.get params[:id], user_id
    if c
      case request.method
        when 'GET'
          fm = Brahma::Web::Form.new '重新生成KEY', "/clients/#{c.id}/reset"
          fm.ok = true
          render json: fm.to_h and return
        when 'POST'
          dlg = Brahma::Web::Dialog.new
          serial, secret=Brahma::ClientService.generate
          c.update secret: secret, serial: serial
          Brahma::LogService.add "重新生成[#{c.id}]的KEY信息", user_id
          dlg.add '请妥善保管验证信息'
          dlg.add "ID: #{serial}"
          dlg.add "KEY: #{secret}"
          dlg.ok = true
          render json: dlg.to_h and return
        else
      end
    end
    not_found
  end

  def demo
    user_id = current_user.fetch(:id)
    c = Brahma::ClientService.get params[:id], user_id
    if c
      send_data(JSON.pretty_generate({
                    server: 'CHANGE_ME',
                    mysql: {user: 'CHANGE_ME', password: 'CHANGE_ME'},
                    agent: {serial: c.serial, key: c.secret}
                }), filename:'agent.json')
    else
      not_found
    end
  end

end
