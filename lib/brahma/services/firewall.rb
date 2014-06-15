require_relative 'client'

module Brahma
  module FirewallService
    module_function

    def get_host(client_id)
      Firewall::Host.find_by client_id: client_id
    end
  end
end