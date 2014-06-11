class CreateEmailDomains < ActiveRecord::Migration
  def change
    create_table :email_domains do |t|
      t.integer :host_id, null: false
      t.string :name, null: false
      t.datetime :created, null: false
    end
  end
end
