namespace :proxy do
  desc 'Start proxy server'
  task :start, [:port] => :environment do |_, args|
    args.with_defaults(port: 9999)
    require 'itpkg/backgrounds/proxy'


  end
end
