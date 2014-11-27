require 'rails_helper'
require 'itpkg/utils/string_helper'

describe 'String Helper Utils' do
  before do
    @obj = {a: 1, b: 'test', c: Time.now}
    @sh = Itpkg::StringHelper
  end
  it 'obj <==> hex' do
    hex = @sh.obj2hex(@obj)
    expect(hex).to be_an_instance_of(String)
    expect(@sh.hex2obj(hex)).to eq @obj
  end
end