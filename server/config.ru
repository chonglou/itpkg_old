Dir.glob('lib/itpkg/*.rb').each { |f| require f }

run Itpkg::API