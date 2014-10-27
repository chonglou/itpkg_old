require 'json'
require 'digest'
require 'openssl'
require 'securerandom'
require_relative 'string_helper'

module Itpkg
  module Encryptor
    module_function

    def _cipher
      OpenSSL::Cipher::AES256.new(:CBC)
    end
    def _config
      StringHelper.hex2obj ENV['ITPKG_CIPHER']
    end


    def generate
      c = _cipher
      StringHelper.obj2hex({key: c.random_key, iv: c.random_iv})
    end

    def encode(obj)
      c = _cipher
      c.encrypt
      h = _config
      c.key = h.fetch :key
      c.iv = h.fetch :iv

      str = c.update(Marshal.dump({salt: StringHelper.rand_s(8), payload: obj}))+c.final
      str.unpack('H*')[0]
    end

    def decode(str)
      str = [str].pack('H*')
      c = _cipher
      c.decrypt
      h = StringHelper.hex2obj ENV['ITPKG_CIPHER']
      c.key = h.fetch :key
      c.iv = h.fetch :iv

      Marshal.load(c.update(str) + c.final).fetch :payload
    end

    def password(obj)
      salt = StringHelper.rand_s 8
      str=StringHelper.sha512(Marshal.dump({salt: salt, payload: obj}))
      "#{salt}#{str}"
    end

    def password?(obj, str)
      salt = str[0..15]
      str[16..-1] == StringHelper.sha512(Marshal.dump({salt: salt, data: obj}))
    end
  end
end