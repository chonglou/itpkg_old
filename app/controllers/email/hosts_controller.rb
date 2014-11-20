require 'itpkg/utils/mysql_helper'

class Email::HostsController < ApplicationController
  before_action :must_admin!

  def index
    @buttons = [
        {label: t('links.email_host.create'), url: new_email_host_path, style: 'primary'},
        {label: t('links.email'), url: email_path, style: 'warning'},

    ]
    @hosts = Email::Host.select(:id, :name, :ip).map do |h|
      {
          cols: [h.name, h.ip],
          url: email_host_path(h.id)
      }
    end
  end

  def new
    @host = Email::Host.new
  end

  def create
    @host = Email::Host.new params.require(:email_host).permit(:name, :ip)
    if @host.valid?
      @host.password = Itpkg::MysqlHelper.grant!(
          'email',
          @host.ip,
          {
              'email_domains' => 'SELECT',
              'email_users' => 'SELECT',
              'email_aliases' => 'SELECT'
          }
      )
      #todo
      @host.certificate_id = 0
      @host.weight = 0

      @host.save
      redirect_to email_hosts_path
    else
      render 'new'
    end
  end

  def edit
    @host = Email::Host.find params[:id]
  end

  def update
    @host = Email::Host.find params[:id]
    if @host.update(params.require(:email_host).permit(:name))
      redirect_to email_host_path(@host.id)
    else
      render 'edit'
    end
  end

  def destroy
    host = Email::Host.find params[:id]
    if host
      Itpkg::MysqlHelper.drop! 'email', host.ip
      host.destroy
    end
    redirect_to email_hosts_path
  end

  def show
    @host = Email::Host.find params[:id]
    @buttons = [
        {label: t('buttons.edit'), url: edit_email_host_path(@host.id), style: 'primary'},
        {label: t('links.email_host.install_sh'), url: email_host_install_sh_path(@host.id), style: 'warning'},

    ]

  end

  def install_sh
    host = Email::Host.find params[:host_id]
    cfg = {id: host.id, password: host.password}

    tmp = Template.find_by flag: 'ops.email', name: 'install.sh'
    send_data ERB.new(tmp.to_sh).result(binding), filename: 'install.sh'
  end
end