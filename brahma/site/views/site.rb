require 'multi_json'
require_relative '../../utils/web'

module Brahma::Site
  class SiteView < Brahma::Base
    get '/' do
      logger.info "Hello Index sdf"
      slim :index
    end

    get '/status' do
      r = Brahma::Web::Form.new 'test', '测试', '/test'
      r.add 'test'
      r.add Brahma::Web::TextField.new 'text', '文本', '内容'
      json r.to_h
      #json version:'v20140214', created:Time.now
    end
  end
end
