require 'securerandom'
require 'base64'
require 'digest'
require 'redcarpet'

module Itpkg
  module StringHelper
    MARKDOWN = Redcarpet::Markdown.new(Redcarpet::Render::HTML.new(link_attributes:{target:'_blank'}), tables: true)

    module_function

    def rand_s(n)
      SecureRandom.hex n
    end

    def uuid
      SecureRandom.uuid
    end

    def obj2hex(obj)
      Marshal.dump(obj).unpack('H*').first
    end

    def hex2obj(hex)
      Marshal.load([hex].pack('H*'))
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