class RegistrationsController < Devise::RegistrationsController
  before_filter :_setup_email, only: :create
  private
  def _setup_email
    params[:user][:email] = "#{params.fetch(:user).fetch(:label)}@#{ENV['ITPKG_DOMAIN']}"
  end
end