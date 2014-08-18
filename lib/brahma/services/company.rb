require 'brahma/services/rbac'

module Brahma
  module CompanyService
    module_function

    def by_user(user_id)
      c = BrahmaBodhi::Rbac.where('role = :role AND resource LIKE :prefix', role:"user://#{user_id}", prefix:'company%').first
      c.nil? ? nil : Company.find_by(id:c.resource[10..-1].to_i)
    end

    def owner?(user_id)
      c = BrahmaBodhi::Rbac.where('role = :role AND resource LIKE :prefix', role:"user://#{user_id}", prefix:'company%').first
      c && c.operation == 'manager'
    end


    def add(uid, operation, cid)
      Brahma::RbacService.set! "user://#{uid}", operation, "company://#{cid}"
    end

  end
end