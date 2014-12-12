require 'rails_helper'
require 'itpkg/utils/mailer'

describe 'Linux Email' do
  before do

    @u1 = Itpkg::Mailer.new 'u1@itpkg.com', '123456', 'itpkg.com'
    @u2 = Itpkg::Mailer.new 'u2@itpkg.com', '123456', 'itpkg.com'
  end

  it 'smtp' do
    @u1.push @u2.user, "test-#{Time.now}", 'bbb'
  end

  it 'imap' do
    ids = @u2.pull.map do |e|
      #puts '#'*80
      #puts e.inspect
      e.message_id
    end
    @u2.remove 'INBOX', ids.first
  end

  it 'test' do
    @u2.test
  end
  it 'folder' do
    @u2.folders
  end



end