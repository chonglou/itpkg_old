# run as 'puma wss.ru -p 9292'

require_relative 'config/environment'

require 'json'
require 'faye/websocket'
require 'itpkg/utils/encryptor'
require 'itpkg/utils/xmpp'

Rails.logger.info 'Starting WSS'

app = lambda do |env|
  if Faye::WebSocket.websocket?(env)
    ws = Faye::WebSocket.new(env)
    xmpp=nil

    ws.on :message do |event|
      begin
        msg = JSON.parse(event.data)
        uid = msg.fetch('ext').fetch('id')
        if Itpkg::Encryptor.hmac(uid) == msg.fetch('ext').fetch('token')
          case msg.fetch('action')
            when 'login'
              unless xmpp
                user = User.find_by uid: uid
                xmpp = Itpkg::Xmpp::Client.new user.email
                xmpp.login user.chat_password do |msg|
                  ws.send({id:uid, from:msg.from, created:msg.created, body:msg.body}.to_json)
                  #ws.send(msg.to_json)
                end
              end
            when 'send'
              xmpp.send!(msg.fetch('to'), msg.fetch('body'))
            else
              fail 'Unknown Action'
          end
        else
          fail 'Error Token'
        end
      rescue => e
        Rails.logger.error e.class, e
      end
    end

    ws.on :close do |event|
      p [:close, event.code, event.reason]
      ws = nil
      if xmpp
        xmpp.close
        xmpp = nil
      end
    end

    ws.rack_response

  else
    [200, {'Content-Type' => 'text/json'}, [{ok: true, created: Time.now.to_s}.to_json]]
  end
end

run app
