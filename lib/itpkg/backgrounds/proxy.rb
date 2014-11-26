require 'ffi-rzmq'
module Itpkg
  module Background
    class ProxyServer
      attr_reader :url
      def initialize(port)
        @url = "tcp://*:#{port}"
        @logger = Rails.logger
      end

      def start
        content = ZMQ::Context.new
        socket = content.socket(ZMQ::REP)
        socket.bind(@url)
        @logger.info "Listen on #{@url}"
        while true do
          request = ''
          socket.recv_string(request)
          @logger.debug "Received Data: #{request.inspect}"
          socket.send_string(request)
        end
      end

    end
  end
end