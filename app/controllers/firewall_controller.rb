require 'brahma/web/dialog'
require 'brahma/web/form'
require 'brahma/web/response'
require 'brahma/services/client'
require 'brahma/services/site'
require 'brahma/services/firewall'

class FirewallController < ApplicationController
  before_action :require_login

  def index
    @ctl_links = {}
    Brahma::ClientService.list(current_user.id, :firewall).each { |c| @ctl_links["/firewall/show/#{c.id}"]=c.name }
    @ctl_links['/firewall/help']='帮助文档'
    @index='/firewall'
    goto_admin
  end


  def info
    user_id = current_user.id
    c = Brahma::ClientService.get params[:client_id], user_id, :firewall
    if c
      case request.method
        when 'GET'
          fm = Brahma::Web::Form.new '参数设置', "/firewall/info/#{c.id}"
          fm.radio 'state', '状态', c.state, [%w(enable 启用), %w(disable 禁用)]
          host = Brahma::FirewallService.get_host c.id

          fm.text 'wan', 'WAN',host ? host.wan : ''
          fm.text 'lan', 'LAN', host ? host.lan : ''
          fm.ok = true
          render json: fm.to_h and return
        when 'POST'
          dlg = Brahma::Web::Dialog.new
          host = Brahma::FirewallService.get_host c.id
          if host
            host.update wan:params[:wan], lan:params[:lan]
          else
            Firewall::Host.create client_id: c.id, wan:params[:wan], lan:params[:lan], created: Time.now
          end
          c.update state: params[:state]
          Brahma::LogService.add "变更终端[#{c.id}]状态为[#{params[:state]}]", user_id
          dlg.ok = true
          render json: dlg.to_h and return
        else
      end
    end
    not_found
  end


  def show
    bg = Brahma::Web::ButtonGroup.new
    c_id = params[:client_id]
    bg.add "/clients/#{c_id}", '基本信息', 'info'
    bg.add "/firewall/info/#{c_id}", '参数设置', 'warning'
    bg.add "/clients/#{c_id}/reset", '重设KEY', 'danger'
    bg.add "/firewall/rules?client_id=#{c_id}", '规则管理', 'primary'
    bg.add "/firewall/limits?client_id=#{c_id}", '限速管理', 'info'
    bg.add "/firewall/devices?client_id=#{c_id}", '设备管理', 'warning'
    render(json: bg.to_h)
  end

end
