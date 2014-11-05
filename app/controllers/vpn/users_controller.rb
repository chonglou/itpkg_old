class Vpn::UsersController < ApplicationController
  before_action :must_admin!

  def index
    @buttons = [
        {label: t('links.vpn_user.create'), url: new_vpn_user_path, style: 'primary'},
        {label: t('links.vpn_log.list'), url: vpn_logs_path, style: 'warning'}
    ]
    @users = Vpn::User.select(:id, :name, :email, :enable, :start_date, :end_date).map { |u| [u.id, u.name, u.email, u.enable, u.start_date, u.end_date] }
  end

  def new
    @user = Vpn::User.new
  end

  def create

  end

  def show

  end

  def edit

  end

  def update

  end

  def destroy

  end
end
