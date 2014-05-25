require 'singleton'

module Brahma

  class Factory
    attr_reader :encryptor, :mysql, :redis, :jobber, :oauth2

    def initialize
      require 'brahma/utils/encryptor'
      require 'brahma/utils/database'
      require 'brahma/utils/redis'
      require 'brahma/config/mysql'
      require 'brahma/config/keys'
      require 'brahma/config/redis'
      require 'brahma/config/site'
      require 'brahma/job'
      require 'brahma/oauth2/client'

      keys = Brahma::Config::Keys.new("#{Rails.root}/config/keys.yml").load Rails.env
      @encryptor = Brahma::Utils::Encryptor.new keys.fetch(:key), keys.fetch(:iv)
      @mysql = Brahma::Utils::Database.connect Brahma::Config::Mysql.new("#{Rails.root}/config/database.yml").load(Rails.env)
      @redis = Brahma::Utils::Redis.pool Brahma::Config::Redis.new("#{Rails.root}/config/redis.yml").load(Rails.env)


      site = Brahma::Config::Site.new.load(Rails.env, :client)
      @oauth2 = Brahma::Oauth2::Client.new site[:app].fetch(:host),
                                           site[:app].fetch(:id), site[:app].fetch(:secret),
                                           "http://#{site.fetch :server}#{site[:app].fetch(:uri)}"

      @jobber = Brahma::JobSender.new site.fetch(:name), @redis
    end
  end

  FACTORY = Factory.new

end