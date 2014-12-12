require 'rails_helper'
require 'itpkg/utils/mail_helper'

describe 'Linux Email' do
  before do

    @u1 = Itpkg::Mailer.new 'u1@itpkg.com', '123456', 'itpkg.com'
    @u2 = Itpkg::Mailer.new 'u2@itpkg.com', '123456', 'itpkg.com'
  end

  it 'smtp' do
    @u1.push @u2.user, "test-#{Time.now}", 'bbb'
  end

  it 'imap' do
    @u2.pull do |e|
      puts '#'*80
      puts e.to_s
    end
  end



end