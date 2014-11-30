class NtPort < ActiveRecord::Base
  validates :node_type_id, :s_port, :t_port, :tcp, presence: true
  validates :t_port, uniqueness: {scope: [:node_type_id, :tcp]}

 belongs_to :node_type

end
