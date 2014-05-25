module Itpkg
  class Response
    attr_reader :data
    attr_accessor :ok

    def initialize(ok=false)
      @ok = ok
      @data = []
      @created = Time.now
    end

    def add(message)
      @data << message
    end

    def to_h
      {
          ok: @ok,
          data: @data,
          created: @created
      }
    end
  end
end