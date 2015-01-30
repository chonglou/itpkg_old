require 'securerandom'
require 'itpkg/utils/xmpp'

class UserValidator < ActiveModel::Validator
  def validate(record)
    if %w(deploy git nobody mail mysql).include?(record.label)
      record.errors[:label] << I18n.t('labels.not_valid')
    end
  end
end

class User < ActiveRecord::Base
  rolify
  attr_accessor :login
  attr_encrypted :chat_password, key: ENV['ITPKG_PASSWORD'], mode: :per_attribute_iv_and_salt

  after_create :_register_im
  before_create :_generate_random

  include RailsSettings::Extend
  # Include default devise modules. Others available are:
  # :confirmable, :lockable, :timeoutable and :omniauthable
  devise :database_authenticatable, :registerable,
         :recoverable, :rememberable, :trackable, :validatable,
         :confirmable, :lockable, :timeoutable,
         :async, authentication_keys: [:login]

  has_one :contact
  validates :label, uniqueness: true
  validates :label, :first_name, :last_name, presence: true
  validates_format_of :label, with: /\A[a-zA-Z][a-zA-Z0-9_]{2,12}\z/, on: :create

  has_one :ssh_key

  validates_with UserValidator


  def to_s
    "#{self.full_name}<#{self.email}>"
  end

  # def after_database_authentication
  #   Log.create user_id:self.id, message:'Sign in.'
  # end

  def full_name
    self.first_name.to_s + ' ' + self.last_name.to_s
  end

  def self.find_for_database_authentication(warden_conditions)
    conditions = warden_conditions.dup
    if (login = conditions.delete(:login))
      where(conditions).where(['label = :value OR email = :value', {:value => login}]).first
    else
      where(conditions).first
    end
  end

  private
  def _generate_random
    self.chat_password = SecureRandom.hex 4
    self.uid = SecureRandom.uuid
  end

  def _register_im
    begin
      c = Itpkg::Xmpp::Client.new self.email
      c.register self.chat_password
    rescue => e
      logger.error e
    end
  end
end
