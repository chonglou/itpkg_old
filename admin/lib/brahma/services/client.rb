module Brahma

  module ClientService
    module_function
    def list(user_id, flag)
      Client.select(:id, :name).where(user_id:user_id, flag:Client.flags[flag])
    end

    def get(client_id, user_id)
      Client.find_by id: client_id, user_id: user_id
    end
  end
end