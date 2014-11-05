require 'itpkg/services/permission'

class ApplicationController < ActionController::Base
  # Prevent CSRF attacks by raising an exception.
  # For APIs, you may want to use :null_session instead.
  protect_from_forgery with: :exception

  before_action :set_locale

  def set_locale
    I18n.locale = params[:locale] || I18n.default_locale
  end

  def default_url_options(options={})
    {locale: I18n.locale}
  end


  def admin?
    Itpkg::PermissionService.admin? current_user.id
  end

  def root?
    Itpkg::PermissionService.root? current_user.id
  end

end
