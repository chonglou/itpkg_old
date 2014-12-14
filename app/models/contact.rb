class Contact < ActiveRecord::Base
  belongs_to :user

  validates :user_id, presence: true
  validates :user_id, uniqueness: true

  def to_s
    s = JSON.pretty_generate(self.attributes.delete_if{|k,_| %w(created_at updated_at user_id).include? k})
    s.gsub("\n", '<br>')
  end

end
