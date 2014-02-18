require 'openssl'
require 'base64'
require 'securerandom'
require 'digest'

module Brahma
  class Encryptor
    include Singleton

    def initialize
      @chars = ('a'..'z').to_a + ('0'..'9').to_a

      d = "#{File.dirname(__FILE__)}/../config"
      Dir.exist?(d) || FileUtils.mkdir_p(d)

      kf = "#{d}/.key"
      log = Log.instance.get 'act'
      if File.exist?(kf)
        log.info '加载KEY文件'
        f = File.new(kf, 'r')
        @key= hex2obj f.gets
        @iv = hex2obj f.gets
        f.close
      else
        log.info '初始化KEY文件'
        c = get_cipher
        @key = c.random_key
        @iv = c.random_iv
        f = File.new(kf, 'w')
        f.puts(obj2hex @key)
        f.puts(obj2hex @iv)
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

    def obj2hex(obj, flag=false)
      str = Marshal.dump obj
      if flag
        str = encrypt str
      end
      str.unpack('H*')[0]
    end

    def hex2obj(hex, flag=false)
      str = [hex].pack('H*')
      if flag
        str = decrypt str
      end

      Marshal.load str
    end

    private
    def get_cipher
      OpenSSL::Cipher::AES256.new(:CBC)
    end
  end
end