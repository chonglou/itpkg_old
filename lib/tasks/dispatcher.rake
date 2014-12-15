namespace :dispatcher do
  desc 'Start scheduler'
  task start: :environment do
    require 'itpkg/backgrounds/dispatcher'
    Itpkg::Background::Dispatcher.start
  end
end
