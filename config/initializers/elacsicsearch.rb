#require 'elacsicsearch'

cfg = YAML.load_file "#{Rails.root}/config/elacsicsearch.yml"
Itpkg::ESClient = Elasticsearch::Client.new url: cfg[Rails.env].fetch('url'), log: cfg[Rails.env].fetch('log')