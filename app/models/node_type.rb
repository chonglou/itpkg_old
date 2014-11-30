class NodeType < ActiveRecord::Base
  has_many :nodes
  has_many :templates, class_name: 'NtTemplate'
  has_many :vars, class_name: 'NtVar'
  has_many :ports, class_name: 'NtPort'
  has_many :volumes, class_name: 'NtVolume'

  validates :name, :dockerfile, :creator_id, presence: true
  validates :name, uniqueness: true

  belongs_to :creator, class_name: 'User'
  enum flag: {integer: 0, string: 1, dockfile: 2, text:3}

end
