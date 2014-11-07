require 'itpkg/linux/openvpn'
require 'itpkg/services/site'

class Vpn::SetupController < ApplicationController
  before_action :must_admin!

  def files
    @cfg = Itpkg::SettingService.get('vpn.cfg', true) || {mysql: {}, openvpn: {routes: [], dns: []}}
  end

  def script
    cfg = Itpkg::SettingService.get('vpn.cfg', true) || {}
    text=['#!/bin/sh']
    if cfg.empty?
      text << "echo '#{t('labels.need_setup')}'"
    else
      text << <<-EOF
mkdir -p /etc/openvpn/scripts

if [ ! -d /etc/openvpn/easy-rsa/2.0/keys ]
then
  cp -R /usr/share/doc/openvpn/examples/easy-rsa/ /etc/openvpn
  cd /etc/openvpn/easy-rsa/2.0/
  ln -s openssl-1.0.0.cnf openssl.cnf
  . /etc/openvpn/easy-rsa/2.0/vars
  . /etc/openvpn/easy-rsa/2.0/clean-all
  . /etc/openvpn/easy-rsa/2.0/build-ca
  . /etc/openvpn/easy-rsa/2.0/build-key-server server
  . /etc/openvpn/easy-rsa/2.0/build-key client

  . /etc/openvpn/easy-rsa/2.0/build-dh
fi
cd /etc/openvpn/easy-rsa/2.0/keys
cp ca.crt ca.key dh1024.pem server.crt server.key /etc/openvpn
      EOF

      Linux::OpenVpn.config_files(cfg).each do |k,v|
        text << "cat > #{k} << \"EOF\" "
        text << v
        text << 'EOF'
      end
      text << 'chmod +x /etc/openvpn/scripts/*.sh'
    end
    send_data text.join("\n"), filename:'openvpn.sh'
  end

  def grant
    cfg = Itpkg::SettingService.get('vpn.cfg', true) || {}

    mysql_host = params[:mysql_host]
    vpn_host = params[:vpn_host]

    if mysql_host == '' || vpn_host == ''
      flash[:alert] = t('labels.input_error')
    else
      unless cfg.empty?
        Linux::OpenVpn.drop! cfg[:openvpn][:host]
      end
      Itpkg::SettingService.set('vpn.cfg', {
          mysql: {
              host: mysql_host,
              password: Linux::OpenVpn.grant!(vpn_host)
          },
          openvpn: {
              host: vpn_host,
              network: params[:vpn_network],
              routes: params[:vpn_routes].split("\r\n"),
              dns: params[:vpn_dns].split("\r\n")
          }
      }, true)
    end
    redirect_to vpn_setup_files_path
  end

end