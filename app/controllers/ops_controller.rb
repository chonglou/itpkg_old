class OpsController < ApplicationController
  def index
    @ctl_links = {
        '/clients' => '终端管理',
        '/ops/services' => '服务列表',
    }
    goto_admin
  end

  def services
    @links = {
        '/firewall' => '防火墙',
        '/cdn' => 'CDN服务',
        '/vpn' => 'VPN服务',
        '/dns' => 'DNS服务',
        '/monitor' => '监控服务',
        '/email' => '邮件服务',
    }
    render 'ops/services', layout: false
  end
end
