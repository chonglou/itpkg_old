module Itpkg
  module PermissionService
    module_function

    def resources(user_id, operation, resource)
      t = Date.today
      Permission.select(:resource).where("role = :role AND operation = :operation AND start_date <= :today AND end_date >= :today AND resource LIKE :resource",
                       {role: "user://#{user_id}", operation: operation, today: t, resource: "#{resource}://%"}).map {|p|p.resource.split('://').last.to_i}
    end

    def root?(user_id)
      auth? "user://#{user_id}", 'ROOT', 'SYSTEM'
    end

    def admin?(user_id)
      auth? "user://#{user_id}", 'ADMIN', 'SYSTEM'
    end

    def admin!(user_id, end_date)
      auth! "user://#{user_id}", 'ADMIN', 'SYSTEM', Date.today.strftime, end_date
    end

    def auth?(role, operation, resource)
      p = Permission.find_by role: role, operation: operation, resource: resource
      t = Date.today
      p && p.start_date <= t && p.end_date >= t
    end

    def auth!(role, operation, resource, start_date, end_date)
      p = Permission.find_by role: role, operation: operation, resource: resource
      if p
        p.update start_date: start_date, end_date: end_date
      else
        p.create role: role, operation: operation, resource: resource, start_date: start_date, end_date: end_date
      end
    end
  end
end