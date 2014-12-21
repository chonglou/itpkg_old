class Story < ActiveRecord::Base
  belongs_to :project
  has_many :story_comments, dependent: :destroy
  has_many :tasks,          dependent: :destroy
  has_and_belongs_to_many :story_types
  has_and_belongs_to_many :story_tags

  validates_presence_of :title, :point, :status

  enum status: {submit: 0, processing: 1, finish: 2, reject: 3, done: 9}

  def to_html
    Itpkg::StringHelper.md2html self.description
  end
end
