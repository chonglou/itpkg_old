class ChangeContact < ActiveRecord::Migration
  def change
    remove_column :contacts, :content

    change_table :contacts do |t|
      t.string :qq
      t.string :wechat
      t.string :phone
      t.string :fax
      t.string :address
      t.string :weibo
      t.string :linkedin
      t.string :facebook
      t.string :skype
      t.text :others
    end
  end
end
