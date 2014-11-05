class Vpn::UsersController < ApplicationController
  before_action :must_admin!

  def index
    @buttons = [
        {label: t('links.vpn_user.create'), url: new_vpn_user_path, style: 'primary'},
        {label: t('links.vpn_log.list'), url: vpn_logs_path, style: 'warning'}
    ]
    @users = Vpn::User.select(:id, :email, :enable, :start_date, :end_date).map { |u| {cols:[u.email, u.enable, u.start_date, u.end_date], url:edit_vpn_user_path(u.id)} }
  end

  def new
    @user = Vpn::User.new
  end

  def create
    @user = Vpn::User.new params.require(:vpn_user).permit( :email, :passwd,:passwd_confirmation, :start_date, :end_date)
    @user.enable = true
    if @user.save
      redirect_to vpn_users_path
    else
      render 'new'
    end

  end

  def edit
    @user1 = Vpn::User.find params[:id]
    @user2 = Vpn::User.find params[:id]
  end

  def update
    @user1 = Vpn::User.find params[:id]
    @user2 = Vpn::User.find params[:id]


    case params['mode']
      when 'password'
        result = @user1.update params.require(:vpn_user).permit(:passwd, :passwd_confirmation)
      when 'enable'
        result = @user2.update params.require(:vpn_user).permit(:enable)
      else
        return
    end

    if result
      redirect_to vpn_users_path
    else
      render 'edit'
    end

  end

  def destroy

  end


end
