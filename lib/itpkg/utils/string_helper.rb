require 'securerandom'
require 'base64'
require 'digest'

module Itpkg
  module StringHelper
    module_function

    def rand_s(n)
      SecureRandom.hex n
    end

    def uuid
      SecureRandom.uuid
    end

    def obj2hex(obj)
      Marshal.dump(obj).unpack('H*')[0]
    end

    def hex2obj(hex)
      Marshal.load [hex].pack('H*')
    end

    def md5(str)
      Digest::MD5.hexdigest str
    end

    def sha512(str)
      Digest::SHA512.hexdigest str
    end

    def md2html(markdown)
      MARKDOWN.render markdown
    end

  end
end