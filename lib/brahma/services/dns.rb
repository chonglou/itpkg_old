require_relative 'client'

module Brahma
  module DnsService
    module_function

    def get_host(client_id)
      Dns::Host.find_by client_id: client_id
    end
  end
end