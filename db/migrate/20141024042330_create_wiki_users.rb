class CreateWikiUsers < ActiveRecord::Migration
  def change
    create_table :wiki_users do |t|
      t.integer :wiki_id, null: false
      t.integer :user_id, null: false
      t.timestamps
    end
  end
end
