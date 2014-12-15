class RssItem
  include Mongoid::Document
  include Mongoid::Attributes::Dynamic
  store_in collection: 'rss.items'
end
