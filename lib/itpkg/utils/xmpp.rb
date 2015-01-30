require 'xmpp4r'

module Itpkg

  module Xmpp
    class Client
      attr_reader :email

      def initialize(email)
        @client = Jabber::Client.new email
        @client.connect
        @email = email
      end

      def login(password)
        @client.auth password
        @client.send(Jabber::Presence.new)
        @client.add_message_callback do |m|
          #yield m.from, @email, m.body, m.type
          begin
            #ChatJob.perform_later m.from.to_s, @email, m.body, m.type.to_s, Time.now.to_s
            ChatMessage.create from: m.from, to: @email, body: m.body, flag: ChatMessage.flags[m.type], created: Time.now
          rescue => e
            puts e
          end
        end
      end

      def send!(to, body, type=:chat)
        ChatMessage.create from: @email, to: to, body: body, flag: ChatMessage.flags[type], created: Time.now
        msg = Jabber::Message.new(to, body)
        msg.type=type
        @client.send msg
      end


      def register(password)
        @client.register password
      end

      def password!(password)
        @client.password = password
      end

      def kill
        @client.remove_registration
      end

      def close
        @client.close
      end

    end
  end

end