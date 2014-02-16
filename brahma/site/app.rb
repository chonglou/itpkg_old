require_relative '../brahma'
require_relative '../plugins/wiki/app'


module Brahma::Site
  class App < Brahma::Base
    use Brahma::Wiki::App


    get "/" do
      logger.info "Hello Index #{request.host}"
      "Index1"
    end

    get '/attachments/*' do
      f = "#{Brahma::Config.instance.store}/#{request.host}/attach/#{params[:splat][0]}"
      logger.info f
      File.exist?(f) ? send_file(f) : 404
    end
  end
end
