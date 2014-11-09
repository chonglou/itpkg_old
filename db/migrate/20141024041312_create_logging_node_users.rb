class CreateLoggingNodeUsers < ActiveRecord::Migration
  def change
    create_table :logging_node_users do |t|

      t.integer :logging_node_id, null: false
      t.integer :user_id, null: false
      t.timestamps
    end
  end
end
