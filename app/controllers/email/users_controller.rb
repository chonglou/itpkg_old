require 'itpkg/utils/mysql_helper'

class Email::UsersController < ApplicationController
  before_action :must_admin!

  def index
    @buttons = [
        {label: t('links.email_user.create'), url: new_email_user_path, style: 'primary'},
        {label: t('links.email'), url: email_path, style: 'warning'},

    ]
    @users = Email::User.select(:id, :email, :created_at).map do |u|
      {
          cols: [u.email, u.created_at],
          url: edit_email_user_path(u.id)
      }
    end

  end

  def new
    @domains = Email::Domain.all.map { |d| [d.name, d.id] }
    if @domains.empty?
      redirect_to new_email_domain_path, alert: t('labels.need_setup')
    else
      @user = Email::User.new
    end
  end

  def create
    @domains = Email::Domain.all.map { |d| [d.name, d.id] }
    @user = Email::User.new user_params
    if @user.valid?
      email = "#{@user.email}@#{Email::Domain.find(@user.domain_id).name}"
      if Email::User.find_by(email: email)
        @user.errors[:base] << t('labels.email_already_exist', email: email)
        render 'new'
      else
        @user.email = email
        @user.password = Itpkg::MysqlHelper.email_password @user.password
        @user.save
        redirect_to email_users_path
      end
    else
      @user.password = ''
      @user.email = ''
      render 'new'
    end
  end

  def edit
    @user = Email::User.find params[:id]
    @user.password = nil
  end

  def update

    rv = params.require(:email_user).permit(:password)
    rv['password'] = Itpkg::MysqlHelper.email_password rv['password']
    @user = Email::User.find params[:id]
    if @user.update(rv)
      redirect_to email_users_path
    else
      @user.password = ''
      render 'edit'
    end
  end

  def destroy
    user= Email::User.find params[:id]
    if user
      Email::Alias.destroy_all destination: user.email
      user.destroy
    end
    redirect_to email_users_path
  end

  private
  def user_params
    params.require(:email_user).permit(:email, :password, :domain_id)
  end


end
