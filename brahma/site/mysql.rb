require 'rubygems'
require 'singleton'
require 'mysql2'

class Mysql2::Result
  def empty?
    count == 0
  end
end

module Brahma::Mysql
  class Sql
    def self.version
      'SELECT VERSION()'
    end

    def self.now
      'SELECT NOW()'
    end

    def self.id
      'SELECT LAST_INSERT_ID()'
    end

    def self.insert(table, args)
      log("INSERT INTO #{table}(#{args.keys.map { |k| "#{k}_" }.join(', ')}) VALUES(#{args.values.map { |v| "'#{v}'" }.join(', ')})")
    end

    def self.delete(table, args, flag=true)
      log("DELETE FROM #{table} WHERE #{args.to_a.map { |k, v| "#{k}_='#{v}'" }.join(flag ? ' AND ' : ' OR ')}")
    end

    def self.select(table, items, args, flag=true, size=1)
      log("SELECT #{items.map { |k| "#{k}_" }.join(', ')} FROM #{table} WHERE #{args.to_a.map { |k, v| "#{k}_='#{v}'" }.join(flag ? ' AND ' : ' OR ')} LIMIT #{size}")
    end

    def self.update(table, items, args, version=false, flag=true)
      log("UPDATE #{table} SET #{items.to_a.map { |k, v| "#{k}_='#{v}'" }.join(', ')}#{version ? ', version_=version_+1' : ''} WHERE #{args.to_a.map { |k, v| "#{k}_='#{v}'" }.join(flag ? ' AND ' : ' OR ')}")
    end

    def self.count(table, args, flag=true)
      log("SELECT COUNT(*) FROM #{table}  WHERE #{args.to_a.map { |k, v| "#{k}_='#{v}'" }.join(flag ? ' AND ' : ' OR ')}")
    end

    def self.create_database(database)
      log("CREATE DATABASE IF NOT EXISTS #{database} DEFAULT CHARACTER SET 'utf8'")
    end

    def self.drop_database(database)
      log("DROP DATABASE #{database}")
    end

    def self.drop_table(table)
      log("DROP TABLE #{table}")
    end

    def self.create_table(table, domain, created, version, columns)
      log("CREATE TABLE IF NOT EXISTS #{table}(id_ INTEGER NOT NULL AUTO_INCREMENT, #{domain ? "domain_ VARCHAR(32) NOT NULL DEFAULT 'localhost'," : ''}#{columns.to_a.map { |k, v| "#{k}_ #{v}" }.join(', ')}#{created ? ', created_ TIMESTAMP NOT NULL DEFAULT NOW()' : ''}#{version ? ', version_ INTEGER NOT NULL DEFAULT 0' : ''}, PRIMARY KEY (id_)) ENGINE=InnoDB")
    end

    def self.log(sql)
      Brahma::Log.instance.get('sql').info(sql)
      sql
    end
  end

  class Pool
    include Singleton

    def connect(host, port, username, password, database, size)
      @logger = Brahma::Log.instance.get('sql')
      conn = Mysql2::Client.new(:host => host, :username => username, :password => password, :port => port)
      conn.query Sql.create_database(database)
      conn.close

      @mysql = ConnectionPool::Wrapper.new(:size => size, :timeout => 3) {
        Mysql2::Client.new(:host => host, :username => username, :password => password, :port => port, :database => database)
      }
      nil
    end

    def execute(&proc)
      result = nil
      @mysql.with do |conn|
        begin
          conn.query('BEGIN')
          result = proc.yield(conn)
          conn.query('COMMIT')
        rescue Mysql2::Error => e
          @logger.fatal e
          conn.query('ROLLBACK')
        end
      end
      result
    end

    def query(sqls)
      execute { |conn|
        sqls.each { |sql|
          @logger.info(sql)
          conn.query(sql)
        }
      }
      nil
    end


  end
end