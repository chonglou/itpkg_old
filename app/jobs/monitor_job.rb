module Itpkg
  class MonitorJob < ActiveJob::Base
    queue_as :default

  end
end