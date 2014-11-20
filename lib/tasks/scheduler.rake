namespace :scheduler do
  desc 'Start scheduler'
  task start: :environment do
    require 'rufus-scheduler'
    require 'itpkg/utils/gitolite'

    sch = Rufus::Scheduler.new

    sch.cron '*/10 * * * *' do
      puts Time.now

    end

    sch.join
  end
end
