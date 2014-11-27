##
# This file is auto-generated. DO NOT EDIT!
#
require 'protobuf/message'

module Itpkg
  module Protocols

    ##
    # Message Classes
    #
    class Request < ::Protobuf::Message
      class Type < ::Protobuf::Enum
        define :docker, 0
        define :monitor, 1
        define :logging, 2
      end

    end

    class Logging < ::Protobuf::Message; end
    class Monitor < ::Protobuf::Message
      class Type < ::Protobuf::Enum
        define :cpu, 0
        define :memory, 1
      end

    end

    class Response < ::Protobuf::Message
      class Type < ::Protobuf::Enum
        define :docker, 0
        define :monitor, 1
        define :logging, 2
      end

    end



    ##
    # Message Fields
    #
    class Request
      required :string, :version, 1, :default => "v20141126"
      required :string, :nid, 2
      required ::Itpkg::Protocols::Request::Type, :type, 3
      repeated ::Itpkg::Protocols::Monitor, :monitors, 4
      repeated ::Itpkg::Protocols::Logging, :loggings, 5
      required :int64, :created, 6
    end

    class Logging
      required :string, :name, 1
      repeated :string, :lines, 2
    end

    class Monitor
      required ::Itpkg::Protocols::Monitor::Type, :type, 1
      required :int64, :created, 2
      required :float, :value, 3
    end

    class Response
      required :string, :version, 1, :default => "v20141126"
      required :bool, :ok, 2
      optional ::Itpkg::Protocols::Response::Type, :type, 3
      repeated :string, :lines, 4
      required :int64, :created, 5
    end

  end

end

