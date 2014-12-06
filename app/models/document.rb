class Document < ActiveRecord::Base
  #attr_accessible :avatar, :avatar_cache

  belongs_to :project
  belongs_to :creator, class_name: 'User'
  enum status: {project: 0, personal: 1, publish: 2}

  mount_uploader :avatar, AvatarUploader

  validates :creator_id, :project_id,:name,:status,:size, presence: true

  def size_s
    s = self.size
    if s < 1024
      s
    elsif s<1024*1024
      "#{s/1024}K"
    elsif s<1024*1024*1024
      "#{s/1024/1024}M"
    end

  end
end
