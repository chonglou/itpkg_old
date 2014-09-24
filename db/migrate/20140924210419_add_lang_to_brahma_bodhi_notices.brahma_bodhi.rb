# This migration comes from brahma_bodhi (originally 20140902222340)
class AddLangToBrahmaBodhiNotices < ActiveRecord::Migration
  def change
    add_column :brahma_bodhi_notices, :lang, :string, null: false, limit: 5
    add_index :brahma_bodhi_notices, :lang
  end
end
