namespace :db do
  desc 'Install database server'
  task :install do
    ask :password, nil

    on roles(:db) do

      [
          "sudo debconf-set-selections <<< 'mysql-server mysql-server/root_password password #{fetch :password}'",
          "sudo debconf-set-selections <<< 'mysql-server mysql-server/root_password_again password #{fetch :password}'",
          'sudo apt-get -y install mysql-server'
      ].each { |cmd| execute cmd }

    end
  end

  desc 'Load the seed data from db/seeds.rb'
  task :seed do
    on roles(:db) do
      within "#{current_path}" do
        with rails_env: fetch(:rails_env) do
          execute :rake, 'db:seed'
        end
      end
    end
  end

  desc 'Backup database'
  task :backup do
    on roles(:db) do
      within "#{current_path}" do
        with rails_env: fetch(:rails_env) do
          execute :rake, 'db:backup'
        end
      end
    end
  end
end