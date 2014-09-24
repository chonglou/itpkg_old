# This migration comes from brahma_bodhi (originally 20140829200941)
class CreateBrahmaBodhiTranslations < ActiveRecord::Migration
  def change
    create_table :brahma_bodhi_translations do |t|
      t.integer 'zh-CN'
      t.integer :en
      t.string :flag, null:false
      t.timestamps
    end
    add_index :brahma_bodhi_translations, :flag
  end
end
