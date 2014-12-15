require 'socket'
require 'eventmachine'

module Itpkg
  module Background
    module Journal
      module Server
        def post_init
          @port, @ip = Socket.unpack_sockaddr_in(get_peername)
          if LoggingNode.count(vip: @ip) == 0
            LoggingNode.create name: 'UNKNOWN', vip: @ip
          end

          Rails.logger.info "Connect From #{@host}:#{@ip}"
        end

        def receive_data(line)
          Itpkg::ESClient.index index: 'logs', type: @ip, body: {message: line.chomp}
        end

        def unbind
          Rails.logger.info "Close #{@host}:#{@ip}"
        end
      end

      module_function

      def start(host, port)
        EventMachine.run do
          Signal.trap('INT') { EventMachine.stop }
          Signal.trap('TERM') { EventMachine.stop }
          EventMachine.start_server host, port, Server
        end
      end
    end
  end
end