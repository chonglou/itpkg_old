require 'rufus-scheduler'

module Itpkg
  module Background
    module Dispatcher
      module_function

      def start
        sch = Rufus::Scheduler.new
        logger = Rails.logger

        sch.every '5s' do

        end

        sch.cron '0 3 * * *' do
          RssSyncJob.perform_later
        end

        sch.join
      end
    end
  end
end