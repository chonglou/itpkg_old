class CreateCompanies < ActiveRecord::Migration
  def change
    create_table :companies do |t|
      t.string :name, null: false
      t.text :details
      t.datetime :created, null: false
    end
  end
end
