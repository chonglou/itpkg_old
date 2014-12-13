require 'rails_helper'
require 'itpkg/linux/snmp'


describe 'Linux Smtp' do
  before do
    @snmp = Linux::Snmp.new 'localhost', 'itpkg', '12345678'
  end

  it 'get' do

    @snmp.get(%w(sysDescr.0 sysContact.0)) do |vbs|
      vbs.each {|v| puts v.value}
    end
    sleep 5
  end
end
