require 'logger'
require 'singleton'

module Brahma
  class Log
    include Singleton

    def initialize
      d = "#{File.dirname(__FILE__)}/../tmp/logs"
      Dir.exist?(d) || FileUtils.mkdir_p(d)
      @loggers = {}
    end

    def get(name)
      def create(n)
        logger = Logger.new("#{File.dirname(__FILE__)}/../tmp/logs/#{n}.log", 'daily')
        logger.level = Config.instance.debug ? Logger::DEBUG : Logger::INFO
        logger.formatter = proc do |severity, datetime, progname, msg|
          "#{severity}\t#{datetime}: #{msg}\n"
        end
        @loggers[n] = logger
        logger
      end

      @loggers[name]|| create(name)
    end

  end
end