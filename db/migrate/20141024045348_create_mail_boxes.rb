class CreateMailBoxes < ActiveRecord::Migration
  def change
    create_table :mail_boxes do |t|
      t.string :from, null: false, limit: 64
      t.string :to, null: false, limit: 64
      t.string :bcc, limit: 1024
      t.string :cc, limit: 1024
      t.string :subject
      t.text :content
      t.integer :owner_id, null: false
      t.integer :flag, null: false, default: 0, limit: 2
      t.timestamps
    end

    add_index :mail_boxes, :from
    add_index :mail_boxes, :to
    add_index :mail_boxes, :subject
  end

end
