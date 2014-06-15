require 'brahma/daemon'

module Brahma
  class Dispatcher < Brahma::Daemon
    def initialize
      super 'itpkg-dispatcher', "#{Rails.root}/tmp/pids"
    end
    def start
      super do
        @logger.info '无事可做'
        sleep 5
      end
    end
  end
end