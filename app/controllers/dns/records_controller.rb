require 'brahma/web/table'
require 'brahma/web/form'
require 'brahma/web/dialog'
require 'brahma/web/validator'
require 'brahma/web/response'
require 'brahma/services/client'
require 'brahma/services/dns'
require 'brahma/services/site'
require 'brahma/factory'

class Dns::RecordsController < ApplicationController
  before_action :require_login

  def index
    user_id = current_user.id
    c_id = params[:client_id]
    tab = Brahma::Web::Table.new "/dns/records?client_id=#{c_id}", '用户列表', %w(ID 名称 类型 值 优先级 创建日期)
    client = Brahma::ClientService.get c_id, user_id, :dns
    if client && client.enable?
      host = Brahma::DnsService.get_host(client.id)
      host.domains.each do |d|
        d.records.each do |r|
          tab.insert [r.id, "#{r.name}@#{r.domain.name}", r.flag, r.value, r.priority, r.created], [
              ['info', 'GET', "/dns/records/#{r.id}", '查看'],
              ['primary', 'GET', "/dns/records/#{r.id}/edit", '修改'],
              ['danger', 'DELETE', "/dns/records/#{r.id}", '删除']
          ]
        end
      end
      tab.toolbar = [['primary', 'GET', "/dns/records/new?client_id=#{c_id}", '新增']]
      tab.ok = true
    else
      tab.add '没有权限'
    end
    render json: tab.to_h
  end

  def destroy
    record = Dns::Record.find_by params[:id]
    dlg = Brahma::Web::Dialog.new
    if can_edit?(record)
      Brahma::LogService.add "删除DNS记录#{record.name}@#{record.domain.name}", current_user.id
      record.destroy
      dlg.ok = true
    else
      dlg.add '没有权限'
    end
    render(json: dlg.to_h)
  end

  def show
    record = Dns::Record.find_by id: params[:id]
    if can_edit?(record)
      list = Brahma::Web::List.new "DNS记录[#{record.id}]"
      list.add "用户名：#{record.name}@#{record.domain.name}"
      list.add "类型：#{record.flag}"
      list.add "值：#{record.value}"
      list.add "创建时间： #{record.created}"
      render json: list.to_h
    else
      not_found
    end
  end

  def update
    vat = Brahma::Web::Validator.new params
    record = Dns::Record.find_by id: params[:id]
    unless can_edit?(record)
      vat.add '没有权限'
    end
    dlg = Brahma::Web::Dialog.new
    if vat.ok?
      record.update flag:params[:flag], value:params[:value], priority:params[:priority]
      dlg.ok = true
    else
      dlg.data += vat.messages
    end
    render json: dlg.to_h
  end

  def edit
    record = Dns::Record.find_by id: params[:id]
    fm = Brahma::Web::Form.new '', "/dns/records/#{params[:id]}"
    if can_edit?(record)
      fm.label = "编辑DNS记录[#{record.name}]"
      fm.select 'flag', '类型', record.flag, dns_record_flag_options
      fm.text 'value', '值', record.value
      fm.select 'priority', '优先级', record.priority, dns_record_priority_options
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

    domain = Dns::Domain.find_by id: params[:domain_id]
    if domain && domain.host.client.enable?
      if Dns::Record.find_by name: params[:name], domain_id: params[:domain_id]
        vat.add '名称已存在'
      end
    else
      vat.add '没有权限'
    end


    dlg = Brahma::Web::Dialog.new
    if vat.ok?
      Dns::Record.create name: params[:name],flag:params[:flag], value:params[:value], priority:params[:priority],
                         domain_id: params[:domain_id], created: Time.now
      dlg.ok = true
    else
      dlg.data += vat.messages
    end
    render json: dlg.to_h
  end

  def new
    if current_user
      fm = Brahma::Web::Form.new '增加DNS记录', '/dns/records'
      fm.select 'domain_id', '域', '', Dns::Host.find_by(client_id: params[:client_id]).domains.map { |d| [d.id, d.name] }
      fm.text 'name', '名称'
      fm.select 'flag', '类型', '', dns_record_flag_options
      fm.text 'value', '值'
      fm.select 'priority', '优先级', 1, dns_record_priority_options
      fm.ok = true
      render json: fm.to_h
    else
      not_found
    end
  end

  private
  def can_edit?(record)
    record && record.domain.host.client.user_id == current_user.id
  end

  def dns_record_priority_options
    1.upto(20).map{|v|[v,v]}
  end
  def dns_record_flag_options
    [%w(a A记录), %w(mx MX记录), %w(ns NS记录), %w(cname NS记录), %w(TXT TXT记录)]
  end

end
