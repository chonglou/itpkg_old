class CreateFinanceTags < ActiveRecord::Migration
  def change
    create_table :finance_tags do |t|
      t.integer :project_id, null: false
      t.string :name, null: false, limit: 32
      t.timestamps
    end
  end
end
