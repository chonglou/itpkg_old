require 'brahma/services/rbac'

module Brahma
  module CompanyService
    module_function

    def role2user(role)
      BrahmaBodhi::User.find_by id:role[7..-1].to_i
    end


    def resource2company(resource)
      Company.find_by(id:resource[10..-1].to_i)
    end

    def get(user_id)
       BrahmaBodhi::Rbac.where('role = :role AND resource LIKE :prefix', role:"user://#{user_id}", prefix:'company%').first
    end

  end
end