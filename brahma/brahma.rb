require 'sinatra/base'
require 'sinatra/json'
require 'sinatra/namespace'
require_relative 'utils/config'

module Brahma
  class Base < Sinatra::Base
    set :sessions, true
    set :public_folder, "#{File.dirname(__FILE__)}/statics"
    set :views, "#{File.dirname(__FILE__)}/templates"


    configure :production  do
      Brahma::Config.instance.setup
    end

    configure :development do
      enable :logging
      Brahma::Config.instance.setup true
    end

    before do
      puts request
    end

    not_found do
      '资源不存在'
    end

    error do
      "服务器出错 #{env['sinatra.error'].name}"
    end

  end

  Error = Class.new(StandardError)
end

