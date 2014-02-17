
module Brahma::Site
  class AttachView < Brahma::Base
    get '/attachments/*' do
      f = "#{Brahma::Config.instance.store}/#{request.host}/attach/#{params[:splat][0]}"
      logger.info f
      File.exist?(f) ? send_file(f) : 404
    end
  end
end
