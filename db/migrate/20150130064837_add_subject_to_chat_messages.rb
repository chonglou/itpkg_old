class AddSubjectToChatMessages < ActiveRecord::Migration
  def change
    change_table :chat_messages do |t|
      t.string :subject, limit:500
    end
  end
end
