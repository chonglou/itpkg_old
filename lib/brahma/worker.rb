require 'brahma/daemon'

module Brahma
  class Worker < Brahma::Daemon
    def initialize(id)
      super "itpkg-worker-#{id}", "#{Rails.root}/tmp/pids"
    end

    def start
      super do
        @logger.info '无事可做'
        sleep 5
      end
    end
  end
end