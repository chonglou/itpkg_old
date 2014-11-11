namespace :proxy do
  desc 'Start a proxy server.'
  task start: :environment do
    require 'backgrounds/proxy'
    run_server 'localhost', 9999
  end

end