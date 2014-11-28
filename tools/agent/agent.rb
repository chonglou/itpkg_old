require 'json'
require 'securerandom'
require 'logger'
require 'openssl'
require 'ffi-rzmq'
require_relative 'protocols.pb'

VERSION = 'v20141127'

class Agent
  def initialize
    @logger = Logger.new "#{File.dirname __FILE__}/agent.log"
    cfg = "#{File.dirname __FILE__}/agent.cfg"

    if File.exist?(cfg)
      @logger.info "Cfg file #{cfg} not exist, will generate a default!"
      File.open(cfg, 'r') { |f| @cfg = JSON.parse(f.read) }
    else
      key = OpenSSL::PKey::RSA.new 2048

      @cfg={
          server: {
              host: 'localhost',
              port: 9999,
              public_key: ''
          },
          client: {
              nid: SecureRandom.hex(16),
              public_key: key.public_key.to_pem,
              private_key: key.to_pem
          },
          logging: [
              '/var/log/nginx/access.log'
          ],
          monitor: [
              :cpu, :memory, :net_out, :net_in, :disk_w, :disk_r, :load
          ]
      }
      @logger.info "Read configuration file error: #{cfg}"
      File.open(cfg, 'w', 0600) { |f| f.write @cfg.to_json }
    end
  end

  def start
    until heart
      register
      sleep 60
    end
  end

  def stop

  end

  private
  def heart

  end

  def register

  end
end

agent = Agent.new

%w(INT TERM).each do |s|
  Signal.trap(s) {
    agent.stop
    exit
  }
end

agent.start
