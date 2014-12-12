require 'mail'

module Itpkg
  class Mailer
    attr_reader :user

    def initialize(user, pass, domain=ENV['ITPKG_DOMAIN'])
      @domain = domain
      @user=user
      @pass=pass
    end

    def folders
      _imap.connection do |c|
        c.list('*', '*').map{|l|l.name}
      end
    end

    def pull
      _imap.all.each { |m| yield m }
    end


    def test
        _imap.connection do |c|
          Rails.logger.debug "TEST IMAP #{c.capability}"
        end
    end

    def push(to, subject, body)

      mail = Mail.new do
        to to
        subject subject
        body body
      end
      mail.from = @user
      _smtp.deliver! mail
    end

    private
    def _imap
      Mail::IMAP.new address: "imap.#{@domain}",
                     port: 993,
                     user_name: @user,
                     password: @pass,
                     enable_ssl: true,
                     openssl_verify_mode: OpenSSL::SSL::VERIFY_NONE
    end

    def _smtp
      Mail::SMTP.new address: "smtp.#{@domain}",
                     port: 25,
                     user_name: @user,
                     password: @pass,
                     enable_starttls_auto: true,
                     openssl_verify_mode: OpenSSL::SSL::VERIFY_NONE
    end
  end
end