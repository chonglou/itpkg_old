require 'sinatra/base'

module Brahma
  class Base < Sinatra::Base
    set :sessions, true
  end
  Error = Class.new(StandardError)
end

