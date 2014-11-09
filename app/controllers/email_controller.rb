class EmailController < ApplicationController
  before_action :must_admin!

  def index
    @items=[
        {
            url: email_hosts_path,
            logo: 'flat/256/hosting.png',
            label: t('links.email_host.list')
        },
        {
            url: email_domains_path,
            logo: 'flat/256/log2.png',
            label: t('links.email_domain.list')
        },
        {
            url: email_users_path,
            logo: 'flat/256/users6.png',
            label: t('links.email_user.list')
        }
    ]
  end
end
