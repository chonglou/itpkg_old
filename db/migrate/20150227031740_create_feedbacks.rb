class CreateFeedbacks < ActiveRecord::Migration
  def change
    create_table :feedbacks do |t|
      t.string  :name
      t.string  :email
      t.string  :phone_number
      t.text    :content
      t.integer :status
      t.boolean :active

      t.belongs_to :project, index: true
      t.belongs_to :user, index: true
      t.timestamps null: false
    end
  end
end
