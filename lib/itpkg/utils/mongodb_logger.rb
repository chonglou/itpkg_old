module Itpkg
  class MongodbLogger < Logger
    def initialize(type)
      @type = type
      @level = DEBUG
      @default_formatter = Formatter.new
    end

    def add(severity, message = nil, progname = nil, &block)
      return if @level > severity
      _write_message (message || (block && block.call) || progname).to_s
      true
    end

    def <<(message)
      _write_message(message)
    end

    private
    def _write_message(message)
      message ||= message.strip
      BgLog.create( message:message, created:Time.now, type:@type)  unless message.empty?
    end
  end
end