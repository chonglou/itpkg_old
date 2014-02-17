
module Brahma::Site
  class SeoView < Brahma::Base
    get '/robots.txt' do
      content_type :'text/plain;charset=utf-8'
      "User-agent: *\nDisallow: /admin/\nDisallow: /personal/\nSitemap: http://#{request.host}/sitemap.xml.gz\n"
    end
  end
end
