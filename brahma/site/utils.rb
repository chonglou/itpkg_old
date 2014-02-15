require 'rubygems'
require 'securerandom'
require 'digest'
require 'yaml'
require 'singleton'
require 'openssl'
require 'net/ssh'
require 'logger'
require 'parseconfig'
require File.dirname(__FILE__)+'/../brahma'

module Brahma::Site
  class Setting
    include Singleton
    attr_reader :debug, :mysql, :redis

    def initialize
      f = File.dirname(__FILE__)+'/../config/web.cfg'
      if File.exist?(f)
        cfg = ParseConfig.new f
        @debug = cfg['debug'] != 'false'
        @redis = cfg['redid']
        @mysql = cfg['mysql']
      else
        fail Brahma::Error.new '配置文件不存在'
      end
    end
  end

  class Log
    include Singleton

    def initialize
      d = File.dirname(__FILE__)+'/../tmp/logs'
      Dir.exist?(d) || FileUtils.mkdir_p(d)
      @loggers = {}
    end

    def get(name)
      def create(n)
        logger = Logger.new(File.dirname(__FILE__)+"/../tmp/logs/#{n}.log", 'daily')
        logger.level = Setting.instance.debug ? Logger::DEBUG : Logger::INFO
        logger.formatter = proc do |severity, datetime, progname, msg|
          "#{severity}\t#{datetime}: #{msg}\n"
        end
        @loggers[n] = logger
        logger
      end

      @loggers[name]|| create(name)
    end

  end

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

  class Helper
    include Singleton

    def initialize
      @chars = ('a'..'z').to_a + ('0'..'9').to_a

      d = File.dirname(__FILE__)+'/../config'
      Dir.exist?(d) || FileUtils.mkdir_p(d)

      kf = "#{d}/.key"
      log = Log.instance.get 'act'
      if File.exist?(kf)
        log.info '加载KEY文件'
        f = File.new(kf, 'r')
        @key= f.gets
        @iv = f.gets
        f.close
      else
        log.info '初始化KEY文件'
        c = get_cipher
        @key = c.random_key
        @iv = c.random_iv
        f = File.new(kf, 'w')
        f.puts @key
        f.puts @iv
        f.chmod 0400
        f.close
      end

    end

    def md5(str)
      Digest::MD5.hexdigest str
    end

    def sha512(str)
      Digest::SHA512.hexdigest str
    end

    def uuid
      SecureRandom.uuid
    end

    def password(plain)
      sha512(plain)
    end

    def check(plain, encode)
      sha512(plain) == encode
    end

    def encrypt(plain)
      c = get_cipher
      c.encrypt
      c.key = @key
      c.iv = @iv
      c.update(plain)+c.final
    end

    def decrypt(encode)
      c = get_cipher
      c.decrypt
      c.key = @key
      c.iv = @iv
      c.update(encode) + c.final
    end

    def rand_str(len)
      ss = ''
      1.upto(len) { |i| ss<<@chars[rand(@chars.size-1)] }
      ss
    end

    def obj2str(obj, encrypt=false)
      Marshal.dump obj
    end

    def str2obj(str, encrypt=false)
      Marshal.load str
    end

    private
    def get_cipher
      OpenSSL::Cipher::AES256.new(:CBC)
    end
  end

end