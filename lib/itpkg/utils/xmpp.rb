require 'xmpp4r'
require 'xmpp4r/muc/helper/simplemucclient'
require_relative 'mongodb_logger'

# require 'itpkg/utils/xmpp'
# c2=Itpkg::Xmpp::Client.new 'user22@localhost'
# c2.login '123456'
#
# c2.send! 'user21@localhost', 'test'

module Itpkg

  module Xmpp
    class Client
      attr_reader :email

      def initialize(email)
        @client = Jabber::Client.new Jabber::JID.new(email)
        @client.connect
        @email = email
        @logger = Rails.logger #Itpkg::MongodbLogger.new('xmpp')
      end

      def login(password)
        @client.auth password
        @client.send(Jabber::Presence.new)

        # mc = Jabber::MUC::SimpleMUCClient.new @client
        # mc.on_join { |_, nick| @logger.info "#{nick} has joined!" }
        # mc.on_leave { |_, nick| @logger.info "#{nick} has left!" }
        # mc.on_message do |time, nick, text|
        #   begin
        #     ChatMessage.create from: nick, to: @email, body: text, flag: ChatMessage.flags[:chat], created: time
        #   rescue => e
        #     puts e
        #   end
        # end
        @client.add_message_callback do |m|
          begin
            if m.body
              from = m.from
              ChatMessage.create domain: from.domain, node:from.node, resource:from.resource, to: @email, body: m.body, flag: ChatMessage.flags[m.type], created: Time.now
            else
              @logger.error m
            end
          rescue => e
            @logger.error e
          end
        end
      end

      def send!(to, body, type=:chat)

        msg = Jabber::Message.new(to, body)
        msg.type=type
        @client.send msg
        jid = @client.jid
        ChatMessage.create domain: jid.domain, node:jid.node, resource:jid.resource, to: to, body: body, flag: ChatMessage.flags[type], created: Time.now
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