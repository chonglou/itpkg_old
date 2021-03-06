
class RepositoryValidator < ActiveModel::Validator
  def validate(record)
    if record.name == GitAdminJob::TESTING_NAME || record.name == GitAdminJob::ADMIN_NAME
      record.errors[:name] << I18n.t('labels.not_valid')
    end
  end
end

class Repository < ActiveRecord::Base
  resourcify

  has_many :repository_users

  validates :name, :title, presence: true
  validates :name, uniqueness: true
  validates_format_of :name, with: /\A[a-zA-Z][a-zA-Z0-9_]{2,12}\z/, on: :create

  validates_with RepositoryValidator
end

