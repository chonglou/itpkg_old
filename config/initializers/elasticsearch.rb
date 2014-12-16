Elasticsearch::Persistence.client = Elasticsearch::Client.new url:ENV['ITPKG_ELASTICSEARCH_URL'], log: !Rails.env.production?
