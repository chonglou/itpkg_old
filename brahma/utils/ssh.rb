require 'net/ssh'

module Brahma
  class Ssh
    def initialize(host, port, user, key)
      @host = host
      @port = port
      @user = user
      @key = key
    end

    def execute(commands)
      log = Log.instance.get('ssh')
      log.info("#{to_s}")
      result = []

      def run(ss, cmd, rs)
        ss.open_channel do |channel|
          channel.on_data do |c, data|
            rs << "\# #{cmd}"
            rs << data
          end
          channel.exec cmd
        end
      end

      Net::SSH.start(@host, @user, :keys_only => TRUE, :key_data => [@key], :port => @port, :compression => 'zlib') do |session|
        commands.each { |command| run session, command, result }
        session.loop
      end

      log.debug(result)
      result
    end

    def to_s
      "#{@user}@#{@host}:#{@port}"
    end
  end
end