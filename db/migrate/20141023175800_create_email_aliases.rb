class CreateEmailAliases < ActiveRecord::Migration
  def change
    create_table :email_aliases do |t|
      t.integer :domain_id, null:false
      t.string :source, null:false, limit:128
      t.string :destination,null:false,limit:128
      t.timestamps
    end
  end
end
