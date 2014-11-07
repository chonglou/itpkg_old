class Dns::Record < ActiveRecord::Base
  alias_attribute :flag, :type
end
