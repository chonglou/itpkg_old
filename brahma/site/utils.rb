require 'securerandom'
require 'digest'
require 'yaml'
require 'singleton'
require 'openssl'

module Brahma::Site
  class Utils
    include Singleton

    def initialize
      @chars = ('a'..'z').to_a + ('0'..'9').to_a

      kf = File.dirname(__FILE__)+'/../config/.key'
      if File.exist?(kf)
        f = File.new(kf, 'r')
        @key= f.gets
        @iv = f.gets
        f.close
      else
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