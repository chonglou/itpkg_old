class Vpn::UsersController < ApplicationController
  before_action :must_admin!

  def index
    @buttons = [
        {label: t('links.vpn_user.create'), url: new_vpn_user_path, style: 'primary'},
        {label: t('links.vpn_log.list'), url: vpn_logs_path, style: 'warning'}
    ]
    @users = Vpn::User.select(:id, :name, :email, :enable, :start_date, :end_date).map { |u| {cols:[u.name, u.email, u.enable, u.start_date, u.end_date], url:edit_vpn_user_path(u.id)} }
  end

  def new
    @user = Vpn::User.new
  end

  def create
    @user = Vpn::User.new user_params
    @user.enable = true
    @user.passwd='123456'
    if @user.save
      flash[:notice] = t('labels.success')
      redirect_to vpn_users_path
    else
      render :action => 'new'
    end

  end

  def edit

  end

  def update

  end

  def destroy

  end

  private
  def user_params
    params.require(:vpn_user).permit(:name, :email, :start_date, :end_date)
  end
end
