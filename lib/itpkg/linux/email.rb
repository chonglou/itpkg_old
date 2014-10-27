require 'securerandom'

module Linux
  module Email
    module_function

    def grant!(host)

      db=Rails.configuration.database_configuration[Rails.env]['database']

      password=SecureRandom.hex 8
      sql = <<-EOF
GRANT INSERT ON `#{db}`.`email_domains` TO 'email'@'#{host}' IDENTIFIED BY '#{password}';
GRANT SELECT ON `#{db}`.`email_users` TO 'email'@'#{host}' IDENTIFIED BY '#{password}';
GRANT INSERT ON `#{db}`.`email_aliases` TO 'email'@'#{host}' IDENTIFIED BY '#{password}';
FLUSH PRIVILEGES;
      EOF
      ActiveRecord::Base.connection.execute(sql)

      password
    end

    def password(password)
      result = ActiveRecord::Base.connection.execute "SELECT ENCRYPT('#{password}', CONCAT('$6$', SUBSTRING(SHA(RAND()), -16)))"
      result.first[0]
    end
  end
end