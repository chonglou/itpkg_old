# This migration comes from brahma_bodhi (originally 20140829200742)
class AddLocaleToBrahmaBodhiNotices < ActiveRecord::Migration
  def change
    add_column :brahma_bodhi_notices, :tid, :integer, null:false
  end
end
