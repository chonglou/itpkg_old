class Document < ActiveRecord::Base
  #attr_accessible :avatar, :avatar_cache

  belongs_to :project
  belongs_to :creator, class_name: 'User'
  enum status: {project: 0, personal: 1, publish: 2}

  mount_uploader :avatar, AvatarUploader

  validates :creator_id, :project_id,:title,:name,:ext,:status,:size, presence: true

end
