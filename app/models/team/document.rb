class Team::Document < ActiveRecord::Base
  belongs_to :project
end
