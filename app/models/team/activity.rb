class Team::Activity < ActiveRecord::Base
  belongs_to :issue
end
