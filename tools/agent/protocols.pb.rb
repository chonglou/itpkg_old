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
      class Logging < ::Protobuf::Message; end
      class Monitor < ::Protobuf::Message
        class Memory < ::Protobuf::Message; end
        class Network < ::Protobuf::Message; end
        class Disk < ::Protobuf::Message; end

      end


    end

    class Response < ::Protobuf::Message
      class Heart < ::Protobuf::Message
        class Monitor < ::Protobuf::Message; end
        class Logging < ::Protobuf::Message; end

      end


    end

    class Message < ::Protobuf::Message
      class Type < ::Protobuf::Enum
        define :HEART, 0
        define :DOCKER, 1
        define :MONITOR, 2
        define :LOGGING, 3
      end

    end



    ##
    # Message Fields
    #
    class Request
      class Logging
        required :string, :name, 1
        repeated :string, :lines, 2
      end

      class Monitor
        class Memory
          required :int32, :total, 1
          required :int32, :usage, 2
        end

        class Network
          required :int32, :in, 1
          required :int32, :out, 2
        end

        class Disk
          required :int32, :read, 1
          required :int32, :write, 2
        end

        optional :int32, :cpu, 1
        optional ::Itpkg::Protocols::Request::Monitor::Memory, :memory, 2
        optional ::Itpkg::Protocols::Request::Monitor::Network, :network, 3
        optional ::Itpkg::Protocols::Request::Monitor::Disk, :disk, 4
        optional :float, :load, 5
        required :int64, :created, 6
        required :float, :value, 7
      end

      optional ::Itpkg::Protocols::Request::Monitor, :monitor, 1
      repeated ::Itpkg::Protocols::Request::Logging, :loggings, 2
    end

    class Response
      class Heart
        class Monitor
          required :bool, :cpu, 1
          required :bool, :memory, 2
          required :bool, :network, 3
          required :bool, :disk, 4
        end

        class Logging
          repeated :string, :names, 1
          required :int32, :space, 2
        end

        optional ::Itpkg::Protocols::Response::Heart::Monitor, :monitor, 2
        optional ::Itpkg::Protocols::Response::Heart::Logging, :logging, 3
      end

      required :bool, :ok, 1
      optional ::Itpkg::Protocols::Response::Heart, :heart, 2
      repeated :string, :lines, 3
    end

    class Message
      required :string, :nid, 1
      required ::Itpkg::Protocols::Message::Type, :type, 2
      required :bool, :plain, 3, :default => false
      required :string, :payload, 4
      required :int64, :created, 5
      required :string, :version, 6, :default => "v20141127"
    end

  end

end

