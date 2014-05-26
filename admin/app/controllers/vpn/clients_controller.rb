require 'brahma/web/response'

class Vpn::ClientsController < ApplicationController
  before_action :require_login

  def show
    bg = Brahma::Web::ButtonGroup.new
    bg.add "/clients/#{params[:id]}", '基本信息', 'info'
    bg.add "/clients/#{params[:id]}/state", '状态管理', 'warning'
    bg.add "/clients/#{params[:id]}/reset", '重设KEY', 'danger'
    bg.add '/vpn/users', '用户管理', 'primary'
    render(json: bg.to_h)
  end
end
