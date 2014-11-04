namespace :db do
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