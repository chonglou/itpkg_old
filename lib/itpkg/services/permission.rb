module Itpkg
  module PermissionService
    module_function

    def root?(user_id)
      auth? role:"user://#{user_id}", operation:'ROOT', resource:'SYSTEM'
    end

    def admin?(user_id)
      auth? role:"user://#{user_id}", operation:'ADMIN', resource:'SYSTEM'
    end

    def admin!(user_id, end_date)
      auth! role:"user://#{user_id}", operation:'ADMIN', resource:'SYSTEM', start_date:Date.today.strftime, end_date:end_date
    end

    def auth?(role, operation, resource)
      p = Permission.find_by role:role, operation:operation, resource:resource
      t = Date.today
      p && p.start_date <= t && p.end_date >= t
    end

    def auth!(role, operation, resource, start_date, end_date)
      p = Permission.find_by role:role, operation:operation, resource:resource
      if p
        p.update start_date:start_date, end_date:end_date
      else
        p.create role:role, operation:operation, resource:resource, start_date:start_date, end_date:end_date
      end
    end
  end
end