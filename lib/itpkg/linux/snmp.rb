# net-snmp-config --create-snmpv3-user -ro -A "#{password}" -a MD5 itpkg
# snmpwalk -v 3 -u itpkg -a MD5 -A "#{password}" -l authNoPriv #{host} sysDescr

require 'net/snmp'

module Linux
  class Snmp
    def initialize(host, username, password)
      @host = host
      @username = username
      @password = password
    end

    def get(oids)
      session = Net::SNMP::Session.open(peername: @host,
                                        port: 161,
                                        username: @username,
                                        auth_password: @password,
                                        security_level: Net::SNMP::Constants::SNMP_SEC_LEVEL_AUTHNOPRIV,
                                        version: 3,
                                        auth_protocol: :md5) do |sess|
        sess.get(oids) do |status, pdu|
          yield(pdu.varbinds) if status == :success
        end
      end
      Net::SNMP::Dispatcher.select(false)
      session.close
    end

  end
end