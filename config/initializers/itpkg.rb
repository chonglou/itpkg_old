Itpkg::BOOTED_AT = Time.now
Itpkg::ELACSICSEARCH_CFG = YAML.load_file("#{Rails.root}/config/elacsicsearch.yml")[Rails.env]