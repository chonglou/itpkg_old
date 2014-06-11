# This migration comes from brahma_bodhi (originally 20140427025508)
class CreateBrahmaBodhiFriendLinks < ActiveRecord::Migration
  def change
    create_table :brahma_bodhi_friend_links do |t|
      t.string :logo
      t.string :domain, null: false
      t.string :name, null: false
      t.datetime :created, null: false

    end
  end
end
