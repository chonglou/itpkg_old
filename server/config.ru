Dir.glob('lib/itpkg/*.rb').each { |f| require_relative f.split('.')[0] }

run Itpkg::API