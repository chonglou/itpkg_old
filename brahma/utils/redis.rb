require 'rubygems'
require 'redis'
require 'connection_pool'
require 'singleton'
require_relative 'log'

module Brahma::Redis
  class Pool
    include Singleton

    def connect(host, port, db, size)
      @redis = ConnectionPool::Wrapper.new(:size => size, :timeout => 3) {
        Redis.new(:host => host, :port => port, :db => db)
      }
      nil
    end

    def set(key, val)
      @redis.with do |conn|
        conn.set(key, val)
      end
    end

    def get(key)
      @redis.with do |conn|
        conn.get(key)
      end
    end

  end
end