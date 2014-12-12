require 'mail'
require 'net/imap'

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
        c.list('*', '*').map{|l|Net::IMAP.decode_utf7 l.name}
      end
    end

    def pull(label='INBOX', order=:desc)
      _imap.find(mailbox:label, order:order,count:50)
    end

    def remove(message_id)
      #todo
      message_id = Net::IMAP.encode_utf7 message_id
      _imap.connection do |c|
        c.select label
        c.copy message_id, 'trash'
        c.store message_id, '+FLAGS', [:Deleted]
        c.expunge
      end
    end

    def mkdir(label)
      _imap.connection do |c|
        c.create label
      end
    end

    def rmdir(label)
      _imap.connection do |c|
        c.delete label
      end
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

      _imap.connection do |c|
        mb = 'Sent'
        c.create mb unless c.list('', mb)
        c.append mb, mail.to_s
      end
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