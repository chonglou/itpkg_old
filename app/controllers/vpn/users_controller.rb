require 'itpkg/linux/openvpn'

class Vpn::UsersController < ApplicationController
  before_action :must_admin!

  def index
    @buttons = [
        {label: t('links.vpn_user.create'), url: new_vpn_user_path, style: 'primary'},
        {label: t('links.vpn'), url: vpn_path, style: 'warning'},

    ]
    @users = Vpn::User.select(:id, :email, :enable, :start_date, :end_date).map { |u| {cols: [u.email, u.enable, u.start_date, u.end_date], url: edit_vpn_user_path(u.id)} }
  end

  def new
    @user = Vpn::User.new
  end

  def create
    @user = Vpn::User.new params.require(:vpn_user).permit(:email, :password, :start_date, :end_date)
    @user.enable = true
    if @user.valid?
      @user.password = Linux::OpenVpn.password @user.password
      @user.save
      Vpn::Log.create email: @user.email, message: t('log.vpn_user.created'), created: Time.now
      redirect_to vpn_users_path
    else
      @user.password = ''
      render 'new'
    end

  end

  def edit
    @user = Vpn::User.find params[:id]
    @user.password=nil
  end

  def update
    if params['vpn_user']['password'] == ''
      rv = params.require(:vpn_user).permit(:enable, :start_date, :end_date)
    else
      rv = params.require(:vpn_user).permit(:password, :enable, :start_date, :end_date)
      rv['password'] = Linux::OpenVpn.password rv['password']
    end

    @user = Vpn::User.find params[:id]

    if @user.update(rv)
      redirect_to vpn_users_path
    else
      @user.password = ''
      render 'edit'
    end

  end

  def destroy
    user = Vpn::User.find params[:id]
    if user
      #Vpn::Log.destroy_all email: user.email
      Vpn::Log.create email: user.email, message: t('log.vpn_user.remove'), created: Time.now
      user.destroy
    end
    redirect_to vpn_users_path
  end


end
