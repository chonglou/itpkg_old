class NtPort < ActiveRecord::Base
  validates :node_type_id, :s_port, :d_port, :tcp, presence: true
  validates :d_port, uniqueness: {scope: [:node_type_id, :tcp]}

 belongs_to :node_type

end
