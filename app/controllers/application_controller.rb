class ApplicationController < ActionController::Base
  # Prevent CSRF attacks by raising an exception.
  # For APIs, you may want to use :null_session instead.
  protect_from_forgery with: :exception

  before_action :set_subdomain
  before_action :set_locale

  before_action :configure_permitted_parameters, if: :devise_controller?

  def set_subdomain
    unless %w(mail www).include?(request.subdomain)
      redirect_to root_url(subdomain: 'www')
    end
  end

  def set_locale
    I18n.locale = params[:locale] || I18n.default_locale
  end

  def default_url_options(options={})
    {locale: I18n.locale}
  end


  def must_admin!
    unless current_user && current_user.has_role?(:admin)
      flash[:alert] = t('labels.must_admin')
      redirect_to root_path
    end
  end

  protected

  def configure_permitted_parameters
    devise_parameter_sanitizer.for(:sign_up) { |u| u.permit(:label, :email, :first_name, :last_name, :password, :password_confirmation, :remember_me) }
    devise_parameter_sanitizer.for(:sign_in) { |u| u.permit(:login, :password, :remember_me) }
    devise_parameter_sanitizer.for(:account_update) { |u| u.permit(:first_name, :last_name, :password, :password_confirmation, :current_password) }
  end

end
