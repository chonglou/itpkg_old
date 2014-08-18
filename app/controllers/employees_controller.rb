require 'json'
require 'brahma/web/table'
require 'brahma/web/form'
require 'brahma/web/dialog'
require 'brahma/web/fall'
require 'brahma/web/validator'
require 'brahma/web/response'
require 'brahma/services/company'
require 'brahma/services/site'

class EmployeesController < ApplicationController
  before_action :require_owner

  def index
    cs = Brahma::CompanyService
    uid = current_user.id
    r= cs.get(uid)

    tab = Brahma::Web::Table.new '/employees', '雇员列表', %w(ID 用户名 类型 生效时间 截止时间)
    BrahmaBodhi::Rbac.where(resource: r.resource).all.each do |r|
      u = cs.role2user r.role
      btns = [
          #['info', 'GET', "/employees/#{u.id}", '查看'],
          #['warning', 'GET', "/employees/#{u.id}/edit", '编辑']
      ]
      unless u.id == uid
        btns << ['danger', 'DELETE', "/employees/#{u.id}", '删除']
      end
      tab.insert [u.id, u.username, r.operation, r.startup, r.shutdown], btns
    end
    tab.toolbar = [%w(primary GET /employees/new 新增)]
    tab.ok = true
    render json: tab.to_h
  end


  def destroy
    cs = Brahma::CompanyService
    uid = current_user.id
    r = cs.get params[:id]

    dlg = Brahma::Web::Dialog.new
    if uid !=params[:id].to_i && r && r.resource == cs.get(uid).resource
      r.destroy
      Brahma::LogService.add "删除用户#{params[:id]}", uid
      dlg.ok = true
    else
      dlg.add '没有权限'
    end


    render(json: dlg.to_h)
  end

  def create
    cs = Brahma::CompanyService
    user = BrahmaBodhi::User.find_by open_id: params[:open_id]
    dlg = Brahma::Web::Dialog.new
    if user && cs.get(user.id).nil?
      uid = current_user.id
      Brahma::RbacService.set! "user://#{user.id}", 'employee', cs.get(uid).resource
      Brahma::LogService.add "添加用户#{user.id}", uid
      dlg.ok = true
    else
      dlg.add '没有权限'
    end
    render json: dlg.to_h
  end

  def new
    fm = Brahma::Web::Form.new '添加雇员', '/employees'
    fm.text 'open_id', 'OPEN ID：'
    fm.ok = true
    render json: fm.to_h
  end
end
