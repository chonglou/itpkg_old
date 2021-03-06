require 'ffi-rzmq'
require 'itpkg/protocols'

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
          req = Itpkg::Protocols.Request.decode request.bytes
          @logger.debug "Received Data: #{request.inspect}"
          resp = Itpkg::Protocols::Response.new created:Time.now.to_i, type:req.type
          #todo
          resp.ok = true
          socket.send_string(resp.encode.pack('c*'))
        end
      end

    end
  end
end