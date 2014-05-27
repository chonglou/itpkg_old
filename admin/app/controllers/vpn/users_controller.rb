require 'brahma/web/table'
require 'brahma/web/form'
require 'brahma/web/dialog'
require 'brahma/web/fall'
require 'brahma/web/validator'
require 'brahma/web/response'
require 'brahma/services/client'
require 'brahma/services/vpn'

class Vpn::UsersController < ApplicationController
  before_action :require_login

  def index
    user_id = current_user.fetch(:id)
    c_id = params[:id]
    tab = Brahma::Web::Table.new "/vpn/users?id=#{c_id}", '用户列表', %w(ID 名称 类型 状态 创建日期)
    client = Brahma::ClientService.get c_id, user_id, :vpn
    if client && client.enable?
      host = Brahma::VpnService.get_host(client.id)
      host.users.each do |u|
        tab.insert [u.id, u.username, u.state, u.created], [
            ['info', 'GET', "/vpn/users/#{u.id}", '查看'],
            ['warning', 'GET', "/vpn/users/#{u.id}/edit", '编辑'],
            ['danger', 'DELETE', "/vpn/users/#{u.id}", '删除']
        ]
        tab.toolbar = [%w(primary GET /vpn/users/new 新增)]
      end
      tab.ok = true
    else
      tab.add '没有权限'
    end
    render json: tab.to_h
  end
end
