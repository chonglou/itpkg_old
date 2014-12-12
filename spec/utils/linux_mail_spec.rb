require 'rails_helper'
require 'itpkg/linux/mail'

describe 'Linux Email' do
  before do

    @u1 = Linux::Mail.new 'u1@itpkg.com', '123456', 'itpkg.com'
    @u2 = Linux::Mail.new 'u2@itpkg.com', '123456', 'itpkg.com'
  end

  it 'smtp' do
    message = <<MESSAGE_END
From: Private Person <#{@u1.user}>
To: A Test User <#{@u2.user}>
Subject: SMTP e-mail test

This is a test e-mail message.
MESSAGE_END
    @u1.push @u2.user, message
  end

  # it 'imap' do
  #   @u2.pull do |from, subject|
  #     puts "#{from}: #{subject}"
  #   end
  # end



end