require 'brahma/factory'
Brahma::Factory.instance.setup! Rails.root, Rails.env
Brahma::Factory.instance.load_encryptor
Brahma::Factory.instance.load_database
Brahma::Factory.instance.load_jobber
Brahma::Factory.instance.load_oauth2

