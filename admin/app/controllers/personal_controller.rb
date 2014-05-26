require 'brahma/web/dialog'
require 'brahma/web/form'
require 'brahma/web/validator'

class PersonalController < ApplicationController
  before_action :require_login

  def index
    user = current_user

    @ctl_links = {
        '/clients' => '终端管理',
        '/personal/company' => '公司信息'
    }
    if admin?
      @ctl_links['/core/admin/users'] = '用户列表'
      @ctl_links['/core/admin/site'] = '站点参数'
      @ctl_links['/core/admin/advert'] = '广告设置'
      @ctl_links['/core/admin/seo'] = 'SEO设置'
      @ctl_links['/core/admin/notices'] = '消息通知'
    end
    @ctl_links['/core/attachments']='附件管理'
    @ctl_links['/core/user/logs']='日志列表'
    @index='/personal'
    goto_admin
  end

  def company
    case request.method
      when 'GET'
        c = Company.find_by user_id: current_user.fetch(:id)
        fm = Brahma::Web::Form.new '公司信息', '/personal/company'
        fm.text 'name', '名称', c ? c.name : '', 480
        fm.html 'details', '详细信息', c ? c.details : ''
        fm.ok = true
        render json: fm.to_h
      when 'POST'
        vat = Brahma::Web::Validator.new params
        vat.empty? 'name', '名称'
        dlg = Brahma::Web::Dialog.new
        if vat.ok?
          c = Company.find_by user_id: current_user.fetch(:id)
          if c
            c.update name: params[:name], details: params[:details]
          else
            Company.create user_id: current_user.fetch(:id), name: params[:name], details: params[:details], created: Time.now
          end
          dlg.ok = true
        else
          dlg.data += vat.messages
        end
        render json: dlg.to_h
      else
        not_found
    end
  end

  private
  def require_login
    unless current_user
      not_found
    end
  end
end
