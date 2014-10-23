require 'net/ssh'

module Linux
  module Ssh
    module_function

    def execute(host, user, key, commands)

      Net::SSH.start(host, user, {keys: [key], logger: Rails.logger}) do |ssh|
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