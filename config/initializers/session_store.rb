# Be sure to restart your server when you modify this file.
require 'brahma/config/site'
Rails.application.config.session_store :cookie_store,
                                       expire_after: 20.minutes,
                                       key: Brahma::Config::Site.new.load(Rails.env, :client).fetch(:session_key)
