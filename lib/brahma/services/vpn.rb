require_relative 'client'

module Brahma
  module VpnService
    module_function

    def get_host(client_id)
      Vpn::Host.find_by client_id: client_id
    end
  end
end