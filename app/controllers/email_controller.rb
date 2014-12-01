class EmailController < ApplicationController
  before_action :must_admin!

  def index
    @items=[
        {
            url: email_domains_path,
            logo: 'flat/256/domain.png',
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
