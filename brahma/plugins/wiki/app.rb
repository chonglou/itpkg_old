require File.dirname(__FILE__)+"/../../brahma"

module Brahma::Wiki
  class App < Brahma::Base
    get "/wiki" do
      "Wiki"
    end
  end
end
