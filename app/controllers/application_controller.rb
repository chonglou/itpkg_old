require 'brahma/web/response'

class ApplicationController < ActionController::Base
  # Prevent CSRF attacks by raising an exception.
  # For APIs, you may want to use :null_session instead.
  protect_from_forgery with: :exception
  layout 'bodhi/main'
  include ShareHelper


  def require_login
    unless current_user
      r = Brahma::Web::Response.new
      r.add '需要登录'
      goto_message r
    end
  end
end
