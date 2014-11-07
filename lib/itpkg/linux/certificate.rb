require 'openssl'

module Linux
  module Certificate

    module_function

    def _r_cert(pem)
      OpenSSL::X509::Certificate.new pem
    end

    def _r_key(pem)
      OpenSSL::PKey::RSA.new pem
    end

    def _r_csr(pem)
      OpenSSL::X509::Request.new pem
    end

    def _g_name
      OpenSSL::X509::Name.parse 'CN=itpkg'
    end

    def _g_cipher
      OpenSSL::Cipher::Cipher.new 'AES-256-CBC'
    end

    def _g_key
      OpenSSL::PKey::RSA.new 2048
    end

    def _g_csr(key)
      csr = OpenSSL::X509::Request.new
      csr.version = 0
      csr.subject = _g_name
      csr.public_key = key.public_key
      csr.sign key, _g_sign
    end

    def _g_cert(years)
      cert = OpenSSL::X509::Certificate.new
      cert.serial = 0
      cert.version = 2
      cert.not_before = Time.now
      cert.not_after = Time.now + years*60*60*24

      cert
    end

    def _g_ext(cert)
      ext = OpenSSL::X509::ExtensionFactory.new
      ext.subject_certificate = cert
      ext.issuer_certificate = cert
      cert.add_extension ext.create_extension('subjectKeyIdentifier', 'hash')
      ext
    end

    def _g_sign
      OpenSSL::Digest::SHA512.new
    end

    def root(years)
      key = _g_key
      name = _g_name

      cert = _g_cert years
      cert.public_key = key.public_key
      cert.subject = name
      cert.issuer = name

      ext = _g_ext cert
      cert.add_extension ext.create_extension('basicConstraints', 'CA:TRUE', true)
      cert.add_extension ext.create_extension('keyUsage', 'cRLSign,keyCertSign', true)

      cert.sign key, _g_sign

      {key: key.to_pem, cert: cert.to_pem}
    end


    def leaf(root_key, years)
      key = _g_key
      csr = _g_csr key

      cert = _g_cert years
      cert.subject = csr.subject
      cert.public_key = csr.public_key
      cert.issuer = cert.subject

      ext = _g_ext cert
      cert.add_extension ext.create_extension('basicConstraints', 'CA:FALSE')
      cert.add_extension ext.create_extension('keyUsage', 'keyEncipherment,dataEncipherment,digitalSignature')

      cert.sign _r_key(root_key), _g_sign

      {key: key.to_pem, csr: csr.to_pem, cert: cert.to_pem}

    end

    def verify(key, cert)
      _r_cert(cert).verify _r_key(key)
    end
  end
end