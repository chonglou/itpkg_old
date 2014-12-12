require 'mail'

module Itpkg
  class Mailer
    attr_reader :user

    def initialize(user, pass, domain=ENV['ITPKG_DOMAIN'])
      @domain = domain
      @user=user
      @pass=pass
    end

    def pull
      imap = Mail::IMAP.new address: "imap.#{@domain}",
                            port: 993,
                            user_name: @user,
                            password: @pass,
                            enable_ssl: true,
                            openssl_verify_mode: OpenSSL::SSL::VERIFY_NONE

      imap.all.each {|m| yield m}
      # while (emails=imap.find(what: :first, count: 10, order: :desc)).length >=0 do
      #   emails.each { |m| yield m }
      # end
    end

    def push(to, subject, body)

      mail = Mail.new do
        to to
        subject subject
        body body
      end
      mail.from = @user

      smtp = Mail::SMTP.new address: "smtp.#{@domain}",
                            port: 25,
                            user_name: @user,
                            password: @pass,
                            enable_starttls_auto: true,
                            openssl_verify_mode: OpenSSL::SSL::VERIFY_NONE

      smtp.deliver! mail

    end
  end
end