class NodeType < ActiveRecord::Base
  has_many :nodes
  enum flag: {integer: 0, string: 1, dockfile: 2, text:3}
end
