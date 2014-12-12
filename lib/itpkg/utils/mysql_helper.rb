require 'securerandom'

module Itpkg
  module MysqlHelper
    module_function
    def drop!(user, host)
      ["DROP USER '#{user}'@'#{host}'", 'FLUSH PRIVILEGES'].each { |sql| ActiveRecord::Base.connection.execute(sql) }
    end

    def grant!(user, host, rules)

      db=Rails.configuration.database_configuration[Rails.env]['database']

      password=SecureRandom.hex 8

      sql = []
      rules.each{|t, p| sql << "GRANT #{p} ON `#{db}`.`#{t}` TO '#{user}'@'#{host}' IDENTIFIED BY '#{password}'"}
      sql << 'FLUSH PRIVILEGES'

      sql.each { |s| ActiveRecord::Base.connection.execute(s) }

      password
    end

    def email_password(password)
      result = ActiveRecord::Base.connection.execute "SELECT ENCRYPT('#{password}', CONCAT('$6$', SUBSTRING(SHA(RAND()), -16)))"
      result.first[0]
    end



  end
end