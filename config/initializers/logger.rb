require 'itpkg/utils/mongodb_logger'
Rails.logger.extend(ActiveSupport::Logger.broadcast(Itpkg::MongodbLogger.new))

Mongoid.logger = nil
Moped.logger = nil

Itpkg::BOOTED_AT = Time.now