class Company < ActiveRecord::Base
  belongs_to :user_id, null:false
end
