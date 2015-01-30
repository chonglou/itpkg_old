require 'json'
require 'digest'
require 'openssl'
require 'securerandom'

module Itpkg
  module Encryptor
    module_function

    def hmac(str)
      OpenSSL::HMAC.hexdigest OpenSSL::Digest.new('sha256'), ENV['ITPKG_HMAC_KEY'], str
    end

    def encode(obj)
      salt = SecureRandom.random_bytes 64
      key = ActiveSupport::KeyGenerator.new(ENV['ITPKG_PASSWORD']).generate_key(salt)
      crypt = ActiveSupport::MessageEncryptor.new(key)
      "#{salt.unpack('H*').first}#{crypt.encrypt_and_sign(Marshal.dump(obj))}"
    end

    def decode(str)
      salt = str[0..127]
      key = ActiveSupport::KeyGenerator.new(ENV['ITPKG_PASSWORD']).generate_key([salt].pack('H*'))
      crypt = ActiveSupport::MessageEncryptor.new(key)
      Marshal.load(crypt.decrypt_and_verify(str[128..-1]))
    end

    def password(obj)
      salt = SecureRandom.hex 32
      str=Digest::SHA512.hexdigest(Marshal.dump({salt: salt, payload: obj}))
      "#{salt}#{str}"
    end

    def password?(obj, str)
      salt = str[0..63]
      str[64..-1] == Digest::SHA512.hexdigest(Marshal.dump({salt: salt, payload: obj}))
    end
  end
end