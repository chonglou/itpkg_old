require 'itpkg/linux/openvpn'
require 'itpkg/services/site'

class Vpn::SetupController < ApplicationController
  before_action :must_admin!

  def files
    @files = Linux::OpenVpn.config_files('MYSQL_HOST', Itpkg::SettingService.get('vpn.db', true) || 'MYSQL_PASSWORD')
  end

  def grant
    passwd = Linux::OpenVpn.grant!
    Itpkg::SettingService.set 'vpn.db', passwd, true
    redirect_to vpn_setup_files_path
  end

end