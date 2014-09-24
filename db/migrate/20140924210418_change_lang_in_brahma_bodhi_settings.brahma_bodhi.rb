# This migration comes from brahma_bodhi (originally 20140830011718)
class ChangeLangInBrahmaBodhiSettings < ActiveRecord::Migration
  def change
    remove_column :brahma_bodhi_settings, :lang
    add_column :brahma_bodhi_settings, :lang, :string
  end
end
