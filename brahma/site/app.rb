
module Brahma::Site
  class App < Brahma::Base
    require_relative 'views/seo'
    require_relative 'views/attach'
    require_relative 'views/site'
    use Brahma::Site::SeoView
    use Brahma::Site::AttachView
    use Brahma::Site::SiteView
  end
end
