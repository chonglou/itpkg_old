class NtVolume < ActiveRecord::Base
  validates :node_type_id, :d_path, :s_path, presence: true
  validates :d_path, uniqueness: {scope: :node_type_id}

  belongs_to :node_type

end
