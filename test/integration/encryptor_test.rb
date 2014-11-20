require 'test_helper'
require 'itpkg/utils/encryptor'

class EncryptorTest < ActionDispatch::IntegrationTest
   test 'encode and decode' do
     assert @obj == @ie.decode(@ie.encode(@obj))
   end

  test 'password' do
    assert @ie.password?(@obj, @ie.password(@obj))
  end

  def setup
    @ie = Itpkg::Encryptor
    @obj = {a:1,b:2,c:'string'}
  end
end
