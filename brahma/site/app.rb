require File.dirname(__FILE__)+'/../brahma'
require File.dirname(__FILE__) + "/../plugins/wiki/app"

module Brahma::Site
  class App < Brahma::Base
    use Brahma::Wiki::App

    get "/" do
      "Index"
    end
  end
end
