# This migration comes from brahma_bodhi (originally 20140428045550)
class CreateBrahmaBodhiNotices < ActiveRecord::Migration
  def change
    create_table :brahma_bodhi_notices do |t|
      t.text :content, null: false
      t.datetime :last_edit, null: false
      t.datetime :created, null: false
    end
  end
end
