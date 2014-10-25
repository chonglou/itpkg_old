class CreateTranslations < ActiveRecord::Migration
  def change
    create_table :translations do |t|
      t.integer 'zh-CN'
      t.integer :en
      t.string :flag, null:false, limit:8
      t.timestamps
    end
    add_index :translations, :flag
  end
end
