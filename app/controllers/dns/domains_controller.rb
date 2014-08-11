require 'brahma/web/table'
require 'brahma/web/form'
require 'brahma/web/dialog'
require 'brahma/web/validator'
require 'brahma/web/response'
require 'brahma/services/client'
require 'brahma/services/dns'
require 'brahma/services/site'
require 'brahma/factory'

class Dns::DomainsController < ApplicationController
  before_action :require_login
  def index
    user_id = current_user.id
    c_id = params[:client_id]
    tab = Brahma::Web::Table.new "/dns/domains?client_id=#{c_id}", '域列表', %w(ID 名称 TTL(秒) 创建日期)
    client = Brahma::ClientService.get c_id, user_id, :dns
    if client && client.enable?
      host = Brahma::DnsService.get_host(client.id)
      host.domains.each do |d|
        tab.insert [d.id, d.name, d.ttl, d.created], [
            ['info', 'GET', "/dns/domains/#{d.id}", '查看'],
            ['warning', 'GET', "/dns/domains/#{d.id}/edit", '编辑'],
            ['danger', 'DELETE', "/dns/domains/#{d.id}", '删除']
        ]
      end
      tab.toolbar = [['primary', 'GET', "/dns/domains/new?client_id=#{c_id}", '新增']]
      tab.ok = true
    else
      tab.add '没有权限'
    end
    render json: tab.to_h
  end

  def destroy
    domain = Dns::Domain.find_by params[:id]
    dlg = Brahma::Web::Dialog.new
    if can_edit?(domain) && domain.records.empty?
      Brahma::LogService.add "删除DNS域#{domain.name}", current_user.id
      domain.destroy
      dlg.ok = true
    else
      dlg.add '没有权限'
    end
    render(json: dlg.to_h)
  end

  def show
    domain = Dns::Domain.find_by id: params[:id]
    if can_edit?(domain)
      list = Brahma::Web::List.new "域[#{domain.id}]"
      list.add "域：#{domain.name}"
      list.add "TTL: #{domain.ttl}"
      list.add "创建时间： #{domain.created}"
      render json: list.to_h
    else
      not_found
    end
  end

  def update
    vat = Brahma::Web::Validator.new params
    vat.empty? :name, '名称'
    domain = Dns::Domain.find_by id: params[:id]
    d1 = Dns::Domain.find_by name:params[:name], host_id:domain.host_id
    if !can_edit?(domain) || !domain.records.empty? || (d1 && d1.id!=domain.id)
      vat.add '没有权限'
    end
    dlg = Brahma::Web::Dialog.new
    if vat.ok?
      domain.update name:params[:name], ttl:params[:ttl]
      dlg.ok = true
    else
      dlg.data += vat.messages
    end
    render json: dlg.to_h
  end

  def edit
    domain = Dns::Domain.find_by id: params[:id]
    fm = Brahma::Web::Form.new '', "/dns/domains/#{params[:id]}"
    if can_edit?(domain)
      fm.label = "编辑域[#{domain.id}]"
      fm.text 'name', '名称', domain.name
      fm.select 'ttl', 'TTL', domain.ttl, ttl_options
      fm.method = 'PUT'
      fm.ok = true
    else
      fm.add '没有权限'
    end
    render json: fm.to_h
  end

  def create
    user_id = current_user.id
    vat = Brahma::Web::Validator.new params
    vat.empty? :name, '名称'

    client = Brahma::ClientService.get(params[:client_id], user_id, :dns)
    if client && client.enable?
      host = Brahma::DnsService.get_host client.id
      if Dns::Domain.find_by name: params[:name], host_id: host.id
        vat.add '域名已存在'
      end
    else
      vat.add '没有权限'
    end

    dlg = Brahma::Web::Dialog.new
    if vat.ok?
      Dns::Domain.create name:params[:name], host_id:host.id, ttl:params[:ttl], created: Time.now
      dlg.ok = true
    else
      dlg.data += vat.messages
    end
    render json: dlg.to_h
  end

  def new
    if current_user
      fm = Brahma::Web::Form.new '增加DNS域', '/dns/domains'
      fm.hidden 'client_id', params[:client_id]
      fm.text 'name', '名称'
      fm.select 'ttl', 'TTL', 300, ttl_options
      fm.ok = true
      render json: fm.to_h
    else
      not_found
    end
  end

  private
  def can_edit?(domain)
    domain && domain.host.client.user_id == current_user.id
  end

  def ttl_options
    [[60,'1分钟'],[60*5,'5分钟'],[60*60, '1小时'], [60*60*24, '1天']]
  end
end
