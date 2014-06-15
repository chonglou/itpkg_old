require 'brahma/web/dialog'
require 'brahma/web/form'
require 'brahma/web/response'
require 'brahma/services/client'
require 'brahma/services/site'
require 'brahma/services/email'

class EmailController < ApplicationController
  before_action :require_login

  def index
    @ctl_links ={}
    Brahma::ClientService.list(current_user.fetch(:id), :email).each { |c| @ctl_links["/email/show/#{c.id}"]=c.name }
    @ctl_links['/email/help']='帮助文档'
    @index='/email'
    goto_admin
  end

  def info
    user_id = current_user.fetch(:id)
    c = Brahma::ClientService.get params[:client_id], user_id, :email
    if c
      case request.method
        when 'GET'
          fm = Brahma::Web::Form.new '参数设置', "/email/info/#{c.id}"
          fm.radio 'state', '状态', c.state, [%w(enable 启用), %w(disable 禁用)]
          fm.ok = true
          render json: fm.to_h and return
        when 'POST'
          dlg = Brahma::Web::Dialog.new
          host = Brahma::EmailService.get_host c.id
          unless host
            Email::Host.create client_id: c.id, created: Time.now
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
    bg.add "/email/info/#{c_id}", '参数设置', 'warning'
    bg.add "/clients/#{c_id}/reset", '重设KEY', 'danger'
    bg.add "/email/domains?client_id=#{c_id}", '域管理', 'primary'
    bg.add "/email/users?client_id=#{c_id}", '用户管理', 'info'
    render(json: bg.to_h)
  end

end
