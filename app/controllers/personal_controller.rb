require 'brahma/web/dialog'
require 'brahma/web/form'
require 'brahma/web/validator'
require 'brahma/services/company'

class PersonalController < ApplicationController
  before_action :require_login

  def index
    @ctl_links = {
        '/personal/company' => '公司信息',
        '/projects' => '项目列表'
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
    goto_admin
  end

  def company
    uid = current_user.id
    c = Brahma::CompanyService.by_user(uid)

    case request.method
      when 'GET'
        if c
          if Brahma::CompanyService.owner?(uid)
            rv = Brahma::Web::Form.new '公司信息', '/personal/company'
            rv.text 'name', '名称', c.name, 480
            rv.html 'details', '详细信息', c.details
          else
            rv = Brahma::Web::List.new '公司信息'
            rv.add "名称：#{c.name}"
            rv.add "详细信息：#{c.details}"
          end
        else
          rv = Brahma::Web::Form.new '公司信息', '/personal/company'
          rv.text 'name', '名称'
          rv.html 'details', '详细信息'

        end
        rv.ok = true
        render json: rv.to_h
      when 'POST'
        vat = Brahma::Web::Validator.new params
        vat.empty? 'name', '名称'
        dlg = Brahma::Web::Dialog.new
        if vat.ok?
          if c
            if Brahma::CompanyService.owner?(uid)
              c.update name: params[:name], details: params[:details]
              dlg.ok = true
            else
              dlg.add '没有权限'
            end
          else
            c = Company.create name: params[:name], details: params[:details], created: Time.now
            Brahma::CompanyService.add uid, 'manager', c.id

            dlg.ok = true
          end
        else
          dlg.data += vat.messages
        end
        render json: dlg.to_h
      else
        not_found
    end
  end

end
