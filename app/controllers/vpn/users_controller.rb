class Vpn::UsersController < ApplicationController
  before_action :must_admin!

  def index
    @buttons = [
        {label: t('links.vpn_user.create'), url: new_vpn_user_path, style: 'primary'},
        {label: t('links.vpn'), url: vpn_path, style: 'warning'},

    ]
    @users = Vpn::User.all.map do |u|
      {
          cols: [u.name, u.email, u.phone, u.online, u.enable, u.start_date, u.end_date],
          url: edit_vpn_user_path(u.id)
      }
    end
  end

  def new
    @user = Vpn::User.new
  end

  def create
    @user = Vpn::User.new params.require(:vpn_user).permit(:name, :phone, :email, :password, :start_date, :end_date)
    @user.enable = true
    if @user.valid?
      @user.password = _password @user.password
      @user.save
      Vpn::Log.create user: @user.name, message: t('logs.vpn_user.created'), start_time: Time.now

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
      rv = params.require(:vpn_user).permit(:email, :phone, :enable, :start_date, :end_date)
    else
      rv = params.require(:vpn_user).permit(:email, :phone, :enable, :start_date, :end_date, :password)
      rv['password'] = _password rv['password']
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
      Vpn::Log.create user: user.name, message: t('logs.vpn_user.remove'), start_time: Time.now
      user.destroy
    end
    redirect_to vpn_users_path
  end

  private
  def _password(password)
    # result = ActiveRecord::Base.connection.execute "SELECT PASSWORD('#{password}')"
    # result.first[0]
    "*#{Digest::SHA1.hexdigest(Digest::SHA1.digest(password)).upcase}"
  end

end
