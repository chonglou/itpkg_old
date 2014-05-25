require 'brahma/daemon'

module Itpkg
  class Agent < Brahma::Daemon
    def initialize
      super 'itpkg-agent', 'tmp/pids'
    end
    def start
      super do

      end
    end
    def stop
      super {}
    end
  end
end