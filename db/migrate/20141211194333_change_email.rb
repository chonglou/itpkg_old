class ChangeEmail < ActiveRecord::Migration
  def change
    change_column :email_users, :email, :string
  end
end
