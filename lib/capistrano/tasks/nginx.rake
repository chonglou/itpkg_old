namespace :nginx do
  desc 'Reload nginx config files.'
  task :reload do
    on roles(:front) do
      execute 'sudo nginx -s reload'
    end
  end


  desc 'Generate and upload nginx configuration.'
  task :setup => 'puma:nginx_config'
end