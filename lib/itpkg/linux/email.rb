require 'securerandom'

module Linux
  module Email
    module_function

    def drop!(host)
      ["DROP USER 'email'@'#{host}'", 'FLUSH PRIVILEGES'].each { |sql| ActiveRecord::Base.connection.execute(sql) }

    end

    def grant!(host)

      db=Rails.configuration.database_configuration[Rails.env]['database']

      password=SecureRandom.hex 8

      ["GRANT INSERT ON `#{db}`.`email_domains` TO 'email'@'#{host}' IDENTIFIED BY '#{password}'",
       "GRANT SELECT ON `#{db}`.`email_users` TO 'email'@'#{host}' IDENTIFIED BY '#{password}'",
       "GRANT INSERT ON `#{db}`.`email_aliases` TO 'email'@'#{host}' IDENTIFIED BY '#{password}'",
       'FLUSH PRIVILEGES'].each { |sql| ActiveRecord::Base.connection.execute(sql) }

      password
    end

    def password(password)
      result = ActiveRecord::Base.connection.execute "SELECT ENCRYPT('#{password}', CONCAT('$6$', SUBSTRING(SHA(RAND()), -16)))"
      result.first[0]
    end

    def config_files(host, password)
      {'mysql' => "#{host} #{password}"}
    end
  end
end