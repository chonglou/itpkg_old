module Linux
  module Snmp
    module_function

    def add_user(password)
      "net-snmp-config --create-snmpv3-user -ro -A #{password} -a MD5 itpkg"
    end

    def test(host, password)
      `snmpwalk -v 3 -u itpkg -a MD5 -A "#{password}" -l authNoPriv #{host} sysDescr`
    end

    def get(host, password, oids)
      `snmpbulkget -v 3 -u itpkg -a MD5 -A "#{password}" -l authNoPriv #{host} #{oids.join ' '}`.split("\n").each do |line|
        yield line
      end

      # require 'net/snmp'
      # Net::SNMP::Session.open(peername: host, username: username, auth_password: password,
      #                         version: 3, auth_protocol: :md5) do |session|
      #   begin
      #     pdu = session.get oid
      #     yield pdu.varbinds
      #   rescue Net::SNMP::Error => e
      #     Rails.logger.error e
      #   end
      # end
    end


  end
end