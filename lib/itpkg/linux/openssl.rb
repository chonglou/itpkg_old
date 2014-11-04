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
        `openssl genrsa -out #{@ssl}/root/key.pem 2048`
        `openssl req -new -key #{@ssl}/root/key.pem -subj "/C=US/ST=California/L=Goleta/O=itpkg/CN=#{ENV['ITPKG_DOMAIN']}" -out #{@ssl}/root/req.csr -text`
        `openssl x509 -req -in #{@ssl}/root/req.csr -out #{@ssl}/root/cert.pem -sha512 -signkey #{@ssl}/root/key.pem -days #{days} -text -extfile /etc/ssl/openssl.cnf -extensions v3_ca`
      else
        `openssl genrsa -out #{@ssl}/#{name}/key.pem 2048`
        `openssl req -new -key #{@ssl}/#{name}/key.pem -subj "/C=US/ST=California/L=Goleta/O=itpkg/CN=#{ENV['ITPKG_DOMAIN']}" -out #{@ssl}/#{name}/req.csr -text`
        `openssl x509 -req -in #{@ssl}/#{name}/req.csr -CA #{@ssl}/root/cert.pem -CAkey #{@ssl}/root/key.pem -CAcreateserial -days #{days} -out #{@ssl}/#{name}/cert.pem -text`
      end


    end

    def verify(name)
      `openssl verify -CAfile #{@ssl}/root/cert.pem #{@ssl}/#{name}/cert.pem`
    end

    def files(name)
      ["#{@ssl}/#{name}/cert.pem", "#{@ssl}/#{name}/key.pem"]
    end
  end
end