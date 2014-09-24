# This migration comes from brahma_bodhi (originally 20140829200747)
class AddLocaleToBrahmaBodhiSettings < ActiveRecord::Migration
  def change
    remove_index :brahma_bodhi_settings, :key
    add_index :brahma_bodhi_settings, :key
    add_column :brahma_bodhi_settings, :lang, :integer
  end
end
