
require File.dirname(__FILE__)+'/site/app'

def check
  d = File.dirname(__FILE__)+"/config"
  Dir.exist?(d) || Dir.mkdir(d)
end

check
run Brahma::Site::App
