##
# This file is auto-generated. DO NOT EDIT!
#
require 'protobuf/message'

module Itpkg
  module Protocols

    ##
    # Enum Classes
    #
    class Type < ::Protobuf::Enum
      define :heart, 0
      define :docker, 1
      define :moint, 2
      define :logger, 3
    end


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

      required :string, :nid, 1
      required ::Itpkg::Protocols::Type, :type, 2
      optional ::Itpkg::Protocols::Request::Monitor, :monitor, 3
      repeated ::Itpkg::Protocols::Request::Logging, :loggings, 4
      required :int64, :created, 5
      required :string, :version, 6, :default => "v20141127"
    end

    class Response
      class Heart
        class Monitor
          required :bool, :cpu, 1
          required :bool, :memory, 2
          required :bool, :network, 3
        end

        class Logging
          repeated :string, :names, 1
          required :int32, :space, 2
        end

        required ::Itpkg::Protocols::Type, :type, 1
        optional ::Itpkg::Protocols::Response::Heart::Monitor, :monitor, 2
        optional ::Itpkg::Protocols::Response::Heart::Logging, :logging, 3
      end

      required :string, :nid, 1
      required :bool, :ok, 2
      repeated :string, :lines, 3
      optional ::Itpkg::Protocols::Response::Heart, :heart, 4
      required :int64, :created, 5
      required :string, :version, 6, :default => "v20141127"
    end

  end

end

