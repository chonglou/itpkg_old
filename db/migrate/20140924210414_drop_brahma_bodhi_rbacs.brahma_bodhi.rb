# This migration comes from brahma_bodhi (originally 20140829200621)
class DropBrahmaBodhiRbacs < ActiveRecord::Migration
  def change
    drop_table :brahma_bodhi_rbacs
  end
end
