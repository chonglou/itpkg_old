class Email::User < ActiveRecord::Base
  belongs_to :domain, class_name: 'Email::Domain'

  validates :name, presence: true

  validates :name, uniqueness:{scope: :domain_id, message:I18n.t('email_username_per_domain')}
end
