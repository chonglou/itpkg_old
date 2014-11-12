namespace :cache do
  desc 'Install cache server'
  task :install do
    on roles(:cache) do
      execute 'sudo apt-get -y install memcached redis-server'
    end
  end
end