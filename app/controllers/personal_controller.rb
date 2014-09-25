require 'brahma/web/response'

class PersonalController < ApplicationController
  def index
     user = current_user
    if user
      @ctl_links = {
          brahma_bodhi.personal_info_path => t('web.link.personal_info'),
      }
      if admin?
        @ctl_links[brahma_bodhi.admin_site_path] = t('web.link.site_info')
        @ctl_links[brahma_bodhi.admin_advert_path] = t('web.link.adverts')
        @ctl_links[brahma_bodhi.admin_notices_path] = t('web.link.notices')
        @ctl_links[brahma_bodhi.admin_seo_path] = t('web.link.seo')
        @ctl_links[brahma_bodhi.admin_users_path] = t('web.link.users')
      end
      @ctl_links[brahma_bodhi.attachments_path]=t('web.link.attachs')
      @ctl_links[brahma_bodhi.user_logs_path]=t('web.link.logs')
      goto_admin
    else
      not_found
    end

  end
end
