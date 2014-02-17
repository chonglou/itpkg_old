require 'slim'
require 'sinatra/base'
require 'sinatra/json'
require 'sinatra/namespace'
require_relative 'utils/config'

module Brahma
  class Base < Sinatra::Base
    set :sessions, true
    set :public_folder, "#{File.dirname(__FILE__)}/statics"
    set :views, "#{File.dirname(__FILE__)}/templates"

    register Sinatra::Namespace

    configure :production do
      Brahma::Config.instance.setup
    end

    configure :development do
      enable :logging
      Brahma::Config.instance.setup true
    end

    before do
      #@_domain =request.host
    end

    after do

    end

    not_found do
      '资源不存在'
    end

    error do
      "服务器出错 #{env['sinatra.error'].name}"
    end

  end

  Error = Class.new(StandardError)

  class Main < Base
    require_relative 'site/views/seo'
    require_relative 'site/views/attach'
    require_relative 'site/views/site'
    use Brahma::Site::SeoView
    use Brahma::Site::AttachView
    use Brahma::Site::SiteView

    plugins = Brahma::Config.instance.plugins
    if plugins.include?('wiki')
      require_relative 'plugins/wiki/views'
      use Brahma::Wiki::WikiView
    end


  end
end

