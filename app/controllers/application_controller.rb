require 'itpkg/services/permission'

class ApplicationController < ActionController::Base
  # Prevent CSRF attacks by raising an exception.
  # For APIs, you may want to use :null_session instead.
  protect_from_forgery with: :exception

  before_action :set_locale

  before_action :configure_permitted_parameters, if: :devise_controller?


  def set_locale
    I18n.locale = params[:locale] || I18n.default_locale
  end

  def default_url_options(options={})
    {locale: I18n.locale}
  end


  def admin?
    u = current_user
    u && Itpkg::PermissionService.admin?(u.id)
  end

  def root?
    u = current_user
    u && Itpkg::PermissionService.root?(u.id)
  end

  def must_admin!
    unless admin?
      flash[:alert] = t('labels.must_admin')
      redirect_to root_path
    end
  end


  protected

  def configure_permitted_parameters
    devise_parameter_sanitizer.for(:sign_up) << :label
  end

end
