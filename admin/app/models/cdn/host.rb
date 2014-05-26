class Cdn::Host < ActiveRecord::Base
  has_one :client
end
