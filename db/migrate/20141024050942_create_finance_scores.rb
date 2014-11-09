class CreateFinanceScores < ActiveRecord::Migration
  def change
    create_table :finance_scores do |t|
      t.integer :project_id, null: false
      t.integer :user_id, null: false
      t.decimal :val, null: false, default: 0.0
      t.integer :tag_id, null: false
      t.string :title, null: false
      t.decimal :balance, null: false, default: 0.0
      t.timestamps
    end
  end
end
