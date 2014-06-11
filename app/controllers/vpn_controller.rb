require 'brahma/web/dialog'
require 'brahma/web/form'
require 'brahma/web/response'
require 'brahma/services/client'
require 'brahma/services/site'
require 'brahma/services/vpn'

class VpnController < ApplicationController
  before_action :require_login

  def index
    @ctl_links ={}
    Brahma::ClientService.list(current_user.fetch(:id), :vpn).each { |c| @ctl_links["/vpn/show/#{c.id}"]=c.name }
    @ctl_links['/vpn/help']='帮助文档'
    @index='/vpn'
    goto_admin
  end

  def info
    user_id = current_user.fetch(:id)
    c = Brahma::ClientService.get params[:client_id], user_id, :vpn
    if c
      case request.method
        when 'GET'
          fm = Brahma::Web::Form.new '参数设置', "/vpn/info/#{c.id}"
          fm.radio 'state', '状态', c.state, [%w(enable 启用), %w(disable 禁用)]
          fm.ok = true
          render json: fm.to_h and return
        when 'POST'
          dlg = Brahma::Web::Dialog.new
          host = Brahma::VpnService.get_host c.id
          unless host
            Vpn::Host.create client_id: c.id, created: Time.now
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
    bg.add "/vpn/info/#{c_id}", '参数设置', 'warning'
    bg.add "/clients/#{c_id}/reset", '重设KEY', 'danger'
    bg.add "/vpn/user/#{c_id}", '用户管理', 'primary'
    render(json: bg.to_h)
  end

end
