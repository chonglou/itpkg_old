require 'rails_helper'
require 'itpkg/utils/xmpp'

# need to set registration_timeout: infinity
# http://localhost:5280/admin
describe 'Xmpp Client' do
  before(:all) do
    @password = '123456'
    @c1 = Itpkg::Xmpp::Client.new 'user21@localhost'
    @c2 = Itpkg::Xmpp::Client.new 'user22@localhost'
  end

  # after(:all) do
  #   @c1.kill
  #   @c2.kill
  # end
  #
  # it 'register user' do
  #   @c1.register @password
  #   @c2.register @password
  # end

  it 'login' do
    @c1.login @password
    @c2.login @password
  end

  it 'send message' do

    @c1.send! @c2.email, "Hello, #{Time.now}"
    sleep 2
    @c2.send! @c1.email, "Hello, #{Time.now}"
    sleep 2
  end

  it 'receive message' do

  end

  # it 'change password' do
  #   password = '654321'
  #   @c1.password! password
  #   @c2.password! password
  # end

end