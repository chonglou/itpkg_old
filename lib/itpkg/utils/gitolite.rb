

module Itpkg
  class Gitolite
    def self.key_pairs(label)
      key = OpenSSL::PKey::RSA.new 2048
      {private_key: key.to_pem, public_key: "#{key.ssh_type} #{[key.public_key.to_blob].pack('m0')} #{label}@#{ENV['ITPKG_DOMAIN']}"}
    end






  end
end