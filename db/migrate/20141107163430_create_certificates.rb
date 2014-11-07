class CreateCertificates < ActiveRecord::Migration
  def change
    create_table :certificates do |t|
      t.text :cert, null:false
      t.text :csr, null:false
      t.text :encrypted_key, null:false
      t.string :encrypted_key_salt, null:false
      t.string :encrypted_key_iv, null:false
      t.timestamps
    end
  end
end
