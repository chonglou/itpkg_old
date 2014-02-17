
module Brahma::Site
  class SiteView < Brahma::Base
    get '/' do
      logger.info "Hello Index sdf"
      slim :index
    end
  end
end
