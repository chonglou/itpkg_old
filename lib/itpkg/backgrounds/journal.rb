require 'socket'
require 'eventmachine'
require 'itpkg/utils/search_client'

module Itpkg
  module Background
    module Journal
      module Server
        LOG_R=/(^[A-Z][a-z]{2}[0-9: ]{12}) ([-\w]+) ([-\w]+)\[([0-9]+)\]: (.*)/

        def post_init
          @port, @ip = Socket.unpack_sockaddr_in(get_peername)
          if LoggingNode.count(vip: @ip) == 0
            LoggingNode.create name: 'UNKNOWN', vip: @ip
          end
          @client = Itpkg::SearchClient.new

          Rails.logger.info "Connect From #{@ip}:#{@port}"
        end

        def receive_data(line)
          line = line.chomp
          ss = LOG_R.match line
          body = {vip:@ip, vport:@port}
          if ss
            #todo 时间上跨年可能会有bug
            body.merge! created:Time.parse(ss[1]), hostname:ss[2], tag:ss[3], pid:ss[4], message:ss[5]
          else
            body.merge! message:line, created:Time.now
          end
          @client.insert :logs, body
        end

        def unbind
          Rails.logger.info "Close #{@ip}:#{@port}"
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