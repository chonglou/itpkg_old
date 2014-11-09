class VpnController < ApplicationController
  before_action :must_admin!

  def index
    @items=[
        {
            url: vpn_users_path,
            logo: 'flat/256/users6.png',
            label: t('links.vpn_user.list')
        },
        {
            url: vpn_hosts_path,
            logo: 'flat/256/hosting.png',
            label: t('links.vpn_host.list')
        },
        {
            url: vpn_logs_path,
            logo: 'flat/256/log2.png',
            label: t('links.vpn_log.list')
        }
    ]
  end
end
