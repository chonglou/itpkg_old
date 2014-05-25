# This migration comes from brahma_bodhi (originally 20140427025522)
class CreateBrahmaBodhiLogs < ActiveRecord::Migration
  def change
    create_table :brahma_bodhi_logs do |t|
      t.integer :user_id, null: false, default: 0
      t.string :message, null: false
      t.integer :flag, null: false, default: 0, limit: 1
      t.datetime :created, null: false
    end
  end
end
