class NtVar < ActiveRecord::Base
  validates :node_type_id, :name, presence: true
  validates :name, uniqueness: {scope: :node_type_id}
  belongs_to :node_type
end
