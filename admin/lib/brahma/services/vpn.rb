require_relative 'client'

module Brahma
  module VpnService
    module_function
    def hosts(user_id)
      hosts = {}
      clients = ClientService::list(user_id, :vpn).each{|c|hosts[Vpn::Host.find_by(client_id:c.id)]=c.name}
      hosts
    end
  end
end