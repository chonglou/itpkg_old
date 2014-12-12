require 'net/imap'
require 'net/smtp'

module Linux
  class Mail
    attr_reader :user
    def initialize(user, pass, domain=ENV['ITPKG_DOMAIN'])
      @domain = domain
      @smtp_host= "smtp.#{@domain}"
      @smtp_port= 25
      @imap_host= "imap.#{@domain}"
      @imap_port= 993
      @user=user
      @pass=pass
    end

    def pull(folder='INBOX')
      imap = Net::IMAP.new @imap_host, @imap_port, usessl = true, certs = nil, verify = false
      imap.authenticate 'LOGIN', @user, @pass
      imap.select folder
      imap.search (['RECENT']).each do |message_id|
        envelope = imap.fetch(message_id, 'ENVELOPE')[0].attr['ENVELOPE']
        yield envelope.from[0].name, envelope.subject
      end
      imap.logout
      imap.disconnect
    end

    def push(to, msg)
      smtp = Net::SMTP.new @smtp_host, @smtp_port
      smtp.enable_starttls
      smtp.start(@domain, @user, @pass, :plain) do |s|
      #Net::SMTP.start(@smtp_host, @smtp_port, @domain, @user, @pass, :login) do |smtp|
        s.send_message msg, @user, to
      end

    end
  end
end