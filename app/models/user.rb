class User < ActiveRecord::Base
  # Include default devise modules. Others available are:
  # :confirmable, :lockable, :timeoutable and :omniauthable
  devise :database_authenticatable, :registerable,
         :recoverable, :rememberable, :trackable, :validatable,
         :confirmable, :lockable, :timeoutable

  has_one :contact
  validates :label, uniqueness:true
  validates :label, presence: true
  validates_format_of :label, with: /\A[a-zA-Z][a-zA-Z0-9_]{2,12}\z/, on: :create
end
