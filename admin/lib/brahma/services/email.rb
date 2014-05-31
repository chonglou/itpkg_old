require_relative 'client'

module Brahma
  module EmailService
    module_function

    def get_host(client_id)
      Email::Host.find_by client_id: client_id
    end
  end
end