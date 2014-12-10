class Document < ActiveRecord::Base
  resourcify
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

  def image?
    %w(jpg jpeg gif png).include? self.ext
  end

  def doc?
    %w(pdf doc).include? self.ext
  end

  def txt?
    %w(txt log rb py php java c cpp cxx h sh).include? self.ext
  end
end
