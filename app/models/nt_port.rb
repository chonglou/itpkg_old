class NtPort < ActiveRecord::Base
  validates :node_type_id, :s_port, :t_port, presence: true
  validates :t_port, uniqueness: {scope: [:node_type_id, :tcp]}
  validates :t_port, :s_port, numericality:{greater_than:0,less_than:65536}

  belongs_to :node_type

end
