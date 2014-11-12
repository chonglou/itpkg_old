namespace :os do
  desc 'Upgrade system'
  task :upgrade do
    on roles(:all) do
      ['sudo apt-get update', 'sudo apt-get upgrade'].each { |cmd| execute cmd }
    end
  end
end