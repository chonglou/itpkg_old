require 'json'
require 'bunny'
require 'faye/websocket'

def msg2json(data=[])
  JSON.generate({data: data, created: Time.now})
end

def json2msg(txt)
  JSON.parse(txt).fetch 'data'
end

def mq_conn
  require 'brahma/config/jobber'
  cfg = Brahma::Config::Jobber.new('config/jobber.yml').load ENV['RACK_ENV']
  unless cfg.fetch(:type) == 'rabbit'
    fail '只支持rabbitmq'
  end
  conn = Bunny.new "amqp://#{cfg.fetch(:host)}:#{cfg.fetch(:port)}"
  conn.start
  conn
end

CONNECTION = mq_conn

APP = lambda do |env|
  if Faye::WebSocket.websocket?(env)
    ws = Faye::WebSocket.new(env)
    serial = nil
    channel = nil

    ws.on :message do |event|
      data = ['fail']
      begin
        lines = json2msg event.data
        case lines[0]
          when 'login'
            if lines[1] == 'aaa'
              data[0] = 'hello'
              serial = lines[1]
              channel = CONNECTION.create_channel
              channel.queue("agent://#{serial}", auto_delete: true).subscribe do |_, _, payload|
                ws.send msg2json(JSON.parse(payload))
              end
              data << 'auth success'
            else
              data[0] = 'bye'
              data << 'auth error'
            end
          when 'response'
            puts lines
            data[0] = 'ok'
          else
            data << 'unknown action'
        end
      rescue => e
        data[0] = 'fail'
        data << e.message
      end
      ws.send msg2json(data)
    end

    ws.on :close do |event|
      channel.close if channel
      p [:close, event.code, event.reason]
      ws = nil
    end

    ws.rack_response

  else
    [200, {'Content-Type' => 'text/plain'}, ["#{Time.now()}"]]
  end
end

run APP
