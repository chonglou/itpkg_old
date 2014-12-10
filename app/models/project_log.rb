class ProjectLog
  include Mongoid::Document
  include Mongoid::Attributes::Dynamic
  store_in collection: 'project.logs'
end
