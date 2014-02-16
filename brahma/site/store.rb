require 'singleton'

module Brahma::Site
  class SettingDao
    include Singleton

    def initialize

      Brahma::Mysql::Pool.instance.query [
                                             Brahma::Mysql::Sql.create_table('settings', true, true, true, {
                                                 key: 'VARCHAR(32) NOT NULL',
                                                 val: 'TEXT NOT NULL'}),
                                             Brahma::Mysql::Sql.create_table('logs', false, true, false, {
                                                 message: 'VARCHAR(255) NOT NULL',
                                                 type: "CHAR(1) NOT NULL DEFAULT 'I'",
                                                 user: 'INTEGER NOT NULL DEFAULT 0'})
                                         ]
    end

    def set(domain, key, val, encrypt=false)
      val=Brahma::Helper.instance.obj2str val, encrypt

      Brahma::Mysql::Pool.instance.execute { |conn|
        rs = conn.query Brahma::Mysql::Sql.select 'settings', ['id'], {key: key, domain: domain}, 1
        conn.query rs.empty? ?
                       Brahma::Mysql::Sql.insert('settings', {domain: domain, key: key, val: val})
                   :
                       Brahma::Mysql::Sql.update('settings', {val: val}, {key: key, domain: domain}, true)
      }
    end

    def get(domain, key, encrypt=false)
      Brahma::Mysql::Pool.instance.execute { |conn|
        rs = conn.query Brahma::Mysql::Sql.select 'settings', %w'val', {key: key, domain: domain}, 1
        val=nil
        rs.each do |row|
          val = Brahma::Helper.instance.str2obj row['val_'], encrypt
        end
        val
      }
    end
  end
end