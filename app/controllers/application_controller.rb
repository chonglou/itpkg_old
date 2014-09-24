require 'brahma_bodhi/concerns/auth_concern'
require 'brahma_bodhi/concerns/locale_concern'
require 'brahma_bodhi/concerns/bodhi_concern'


class ApplicationController < ActionController::Base
  # Prevent CSRF attacks by raising an exception.
  # For APIs, you may want to use :null_session instead.
  protect_from_forgery with: :exception

  layout 'bodhi/main'
    include BrahmaBodhi::LocaleConcern
    include BrahmaBodhi::BodhiConcern
    include BrahmaBodhi::AuthConcern



end
