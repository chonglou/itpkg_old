require 'itpkg/linux/email'
require 'itpkg/services/site'

class Email::SetupController < ApplicationController
  before_action :must_admin!

  def files
    cfg = Itpkg::SettingService.get('email.cfg', true) || {}
    if cfg.empty?
      @files = {}
    else
      @files = Linux::Email.config_files(cfg[:mysql][:host],  cfg[:mysql][:password])
    end

  end

  def grant
    cfg = Itpkg::SettingService.get('email.cfg', true) || {}

    mysql_host = params[:mysql_host]
    vpn_host = params[:openvpn_host]
    if mysql_host == '' || vpn_host == ''
      flash[:alert] = t('labels.input_error')
    else

      unless cfg.empty?
        Linux::OpenVpn.drop! cfg[:openvpn][:host]
      end
      Itpkg::SettingService.set 'email.cfg', {mysql:{host:mysql_host, password:Linux::OpenVpn.grant!(vpn_host)}, openvpn:{host:vpn_host}}, true
    end
    redirect_to vpn_setup_files_path
  end
end
