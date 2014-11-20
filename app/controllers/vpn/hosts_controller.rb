require 'itpkg/utils/mysql_helper'
class Vpn::HostsController < ApplicationController
  before_action :must_admin!

  def index
    @buttons = [
        {label: t('links.vpn_host.create'), url: new_vpn_host_path, style: 'primary'},
        {label: t('links.vpn'), url: vpn_path, style: 'warning'},

    ]
    @hosts = Vpn::Host.select(:id, :name, :domain, :ip, :network, :routes, :dns).map do |h|
      {
          cols: [h.name, h.domain, h.ip, h.network, h.routes.gsub(/\n/, '<br/>'), h.dns.gsub(/\n/, '<br/>')],
          url: vpn_host_path(h.id)
      }

    end

  end

  def new
    @host = Vpn::Host.new
  end

  def create
    @host = Vpn::Host.new params.require(:vpn_host).permit(:name, :ip, :domain, :network, :routes, :dns)
    if @host.valid?
      @host.password = Itpkg::MysqlHelper.grant!(
          'vpn',
          @host.ip,
          {
              'vpn_users'=>'SELECT',
              'vpn_logs'=>'INSERT'
          }
      )
      # todo
      @host.certificate_id = 0
      @host.weight = 0
      @host.save
      redirect_to vpn_hosts_path
    else
      render 'new'
    end
  end

  def edit
    @host = Vpn::Host.find params[:id]
  end

  def update
    @host = Vpn::Host.find params[:id]
    if @host.update(params.require(:vpn_host).permit(:name, :domain, :network, :routes, :dns))
      redirect_to vpn_host_path(@host.id)
    else
      render 'edit'
    end
  end

  def destroy
    host = Vpn::Host.find params[:id]
    if host
      Itpkg::MysqlHelper.drop!('vpn', host.ip)
      host.destroy
    end
    redirect_to vpn_hosts_path
  end

  def show
    @host = Vpn::Host.find params[:id]
    @buttons = [
        {label: t('buttons.edit'), url: edit_vpn_host_path(@host.id), style: 'primary'},
        {label: t('links.vpn_host.install_sh'), url: vpn_host_install_sh_path(@host.id), style: 'warning'},
        {label: t('links.vpn_host.list'), url: vpn_hosts_path, style: 'info'},

    ]
  end

  def install_sh
    host = Vpn::Host.find params[:host_id]
    cfg = {
        id: host.id,
        password: host.password,
        host: host.ip,
        network: host.network,
        routes: host.routes.split("\r\n"),
        dns: host.dns.split("\r\n")
    }

    tmp = Template.find_by flag:'ops.vpn', name:'install.sh'

    send_data ERB.new(tmp.to_sh).result(binding), filename: 'install.sh'
  end

end