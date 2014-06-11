require 'faye'
require 'brahma/utils/logger'
LOGGER = Brahma::Utils::Logger.instance.create 'itpkg-faye'

app = Faye::RackAdapter.new(mount: '/faye', timeout: 20)

run app
