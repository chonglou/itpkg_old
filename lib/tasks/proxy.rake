namespace :proxy do
  desc 'Start proxy server'
  task :start, [:port] => :environment do |_, args|
    args.with_defaults(port: 9999)
    require 'itpkg/backgrounds/proxy'
    proxy = Itpkg::Background::ProxyServer.new args[:port]
    proxy.start
  end

  desc 'Generate GPF'
  task :gpf do
    %w(request response).each do |k|
    `protoc -I #{Rails.root}/tools/protocols --ruby_out #{Rails.root}/lib/itpkg/protocols #{Rails.root}/tools/protocols/#{k}.proto`
    end
  end
end
