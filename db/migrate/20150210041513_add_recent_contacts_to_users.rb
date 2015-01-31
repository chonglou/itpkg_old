class AddRecentContactsToUsers < ActiveRecord::Migration
  def change
    add_column :users, :recent_contacts_ids, :string
  end
end
