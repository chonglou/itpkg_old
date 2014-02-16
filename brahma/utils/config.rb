require 'yaml'
require 'singleton'

module Brahma
  class Config
    include Singleton
    attr_reader :debug, :mysql, :redis, :store

    def initialize
      begin
        cfg = File.open "#{File.dirname(__FILE__)}/../config/web.cfg"
        cfg = YAML.load cfg
        @debug = cfg['app']['debug']
        @store = cfg['app']['store']

        @redis = {
            host: cfg['redis']['host'],
            port: cfg['redis']['port'],
            db: cfg['redis']['db'],
            pool: cfg['redis']['pool'],
        }
        @mysql = {
            host: cfg['mysql']['host'],
            port: cfg['mysql']['port'],
            username: cfg['mysql']['username'],
            password: cfg['mysql']['password'],
            database: cfg['mysql']['database'],
            pool: cfg['mysql']['pool'],
        }

      rescue ArgumentError => e
        fail Brahma::Error.new('读取配置文件失败')
      end
    end

    def setup(devel=false)
      if devel
        @mysql[:database] += '_d'
        @redis[:db] += '_d'
      end
      require_relative '../utils/mysql'
      require_relative '../utils/redis'
      Brahma::Mysql::Pool.instance.connect @mysql[:host], @mysql[:port], @mysql[:username], @mysql[:password], @mysql[:database], @mysql[:pool]
      Brahma::Redis::Pool.instance.connect @redis[:redis], @redis[:port], @redis[:db], @redis[:pool]
    end

  end
end