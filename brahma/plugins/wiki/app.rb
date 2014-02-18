
module Brahma::Wiki
  class App < Brahma::Base
    require_relative 'views/wiki'
    use Brahma::Wiki::WikiView
  end
end
