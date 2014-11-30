class NodeType < ActiveRecord::Base
  has_many :nodes
  has_many :templates
  belongs_to :creator, class_name: 'User'
  enum flag: {integer: 0, string: 1, dockfile: 2, text:3}

  validates :name, :dockerfile, :creator_id, presence: true
  validates :name, uniqueness: true
end
