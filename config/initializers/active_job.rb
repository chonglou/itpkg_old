Rails.application.configure do
  config.active_job.queue_adapter = :sidekiq
end

Sidekiq.configure_server do |config|
  config.redis = { url: ENV['ITPKG_REDIS_URL'] }
end

Sidekiq.configure_client do |config|
  config.redis = { url: ENV['ITPKG_REDIS_URL'] }
end

require 'itpkg/utils/mongodb_logger'
Sidekiq::Logging.logger=Itpkg::MongodbLogger.new('workers')



