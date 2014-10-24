module Linux
  class Openssl
    def initialize
      @ssl="#{Rails.root}/tmp/storage/ssl"
    end

    def exist?(name='root')
      Dir.exist? "#{@ssl}/#{name}"
    end

    def init(name='root', days=3650)
      return if exist?(name)

      `mkdir -pv #{@ssl}/#{name}`

      if name == 'root'
        `openssl genrsa -out #{@ssl}/root/root-key.pem 2048`
        `openssl req -new -key #{@ssl}/root/root-key.pem -subj "/C=US/ST=California/L=Goleta/O=itpkg/CN=#{ENV['ITPKG_DOMAIN']}" -out #{@ssl}/root/root-req.csr -text`
        `openssl x509 -req -in #{@ssl}/root/root-req.csr -out #{@ssl}/root/root-cert.pem -sha512 -signkey #{@ssl}/root/root-key.pem -days #{days} -text -extfile /etc/ssl/openssl.cnf -extensions v3_ca`
      else
        `openssl genrsa -out #{@ssl}/#{name}/server-key.pem 2048`
        `openssl req -new -key #{@ssl}/#{name}/server-key.pem -subj "/C=US/ST=California/L=Goleta/O=itpkg/CN=#{ENV['ITPKG_DOMAIN']}" -out #{@ssl}/#{name}/server-req.csr -text`
        `openssl x509 -req -in #{@ssl}/#{name}/server-req.csr -CA #{@ssl}/root/root-cert.pem -CAkey #{@ssl}/root/root-key.pem -CAcreateserial -days #{days} -out #{@ssl}/#{name}/server-cert.pem -text`
      end


    end

    def verify(name)
      `openssl verify -CAfile #{@ssl}/root/root-cert.pem #{@ssl}/#{name}/server-cert.pem`
    end
  end
end