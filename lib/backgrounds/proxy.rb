$logger = Rails.logger
module ProxyServer

  def post_init
    $logger.info 'connected'
  end

  def receive_data(line)
    line = line.chomp
    send_data ">>> SENT:#{line}\n"
    close_connection if line == 'bye'
  end

  def unbind
    $logger.info 'disconnected'
  end
end

def run_server(host, port)
  require 'eventmachine'
  EventMachine.run { EventMachine.start_server host, port, ProxyServer }
end