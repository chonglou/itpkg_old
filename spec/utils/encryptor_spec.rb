require 'rails_helper'

describe 'Encryptor Utils' do
  before do
    @obj = {a: 1, b: 'test', c: Time.now}
    @ie = Itpkg::Encryptor
  end

  it 'password' do
    pwd = @ie.password(@obj)
    expect(pwd).to be_an_instance_of(String)
    expect(@ie.password?(@obj, pwd)).to be true
  end

  it 'encode and decode' do
    en = @ie.encode(@obj)
    expect(en).to be_an_instance_of(String)
    expect(@ie.decode(en)).to eq @obj
  end


end