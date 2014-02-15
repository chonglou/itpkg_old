require 'rubygems'
require 'sequel'
require 'singleton'

module Brahma::Mysql
  class Query
    def execute
      raise NotImplementedError
    end
  end

  class Pool
    include Singleton

    def connect(host, port, user, password, database, size)
      @db = Sequel.connect(
          :adapter=>'mysql', :max_connections=>size,
      :host=>host, :port=>port, :user=>user, :password=>password, :database=>database)
      nil
    end

    #fixme 可变参数
    def execute(query)
      @db.transaction do
        query.execute(@db)
      end
    end

  end
end