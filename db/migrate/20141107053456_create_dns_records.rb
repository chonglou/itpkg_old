class CreateDnsRecords < ActiveRecord::Migration
  def change
    create_table :dns_records do |t|
      t.string :zone, null: false
      t.string :host, null: false, default: '@'
      t.string :type, null: false, limit: 8
      t.text :data
      t.integer :ttl, null: false, default: 86400
      t.integer :mx_priority
      t.integer :refresh
      t.integer :retry
      t.integer :expire
      t.integer :minimum
      t.integer :serial, limit: 8
      t.string :resp_person
      t.string :primary_ns
      t.integer :code, default: 0
      t.timestamps
    end
    add_index :dns_records, :type
    add_index :dns_records, :host
    add_index :dns_records, :zone
    add_index :dns_records, [:host, :zone]
  end
end
