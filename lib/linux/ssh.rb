require 'fileutils'
require 'net/ssh'

module Linux
  class Ssh
    def initialize
      @key = "#{Rails.root}/tmp/storage/keys/ssh"
    end

    def exist?
      File.exist? @key
    end

    def init
      d = "#{Rails.root}/tmp/storage/keys"
      unless Dir.exist?(d)
        FileUtils.mkpath d
      end
      `echo -e  'y\n'|ssh-keygen -q -t rsa -N "" -f #{@key}`
    end

    def execute(user, host, commands, port=22)

      Net::SSH.start(host, user, {keys: [@key], port: port, logger: Rails.logger}) do |ssh|
        channel = ssh.open_channel do |ch|
          commands.each do |cmd|
            ch.exec cmd do |session, success|
              #fail 'could not execute command' unless success
              session.on_data do |c, data|
                yield data
              end

              session.on_extended_data do |c, type, data|
                yield data
              end
              session.on_close {}
            end
          end

        end
        channel.wait
      end
    end
  end
end