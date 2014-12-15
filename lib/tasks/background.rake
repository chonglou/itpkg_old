namespace :proxy do
  desc 'Start proxy server'
  task :start, [:port] => :environment do |_, args|
    args.with_defaults(port: 10001)
    require 'itpkg/backgrounds/proxy'
    proxy = Itpkg::Background::ProxyServer.new args[:port]
    proxy.start
  end

  desc 'Generate GPF'
  task :gpf do
    %w(lib/itpkg tools/agent).each do |d|
      `protoc -I #{Rails.root}/tools --ruby_out #{Rails.root}/#{d} #{Rails.root}/tools/protocols.proto`
    end

    target = "#{ENV['GOPATH']}/src/itpkg"
    unless Dir.exist?(target)
      require 'fileutils'
      FileUtils.mkdir_p target
    end
    `protoc -I #{Rails.root}/tools --go_out #{target} #{Rails.root}/tools/protocols.proto`
  end
end

namespace :dispatcher do
  desc 'Start scheduler'
  task start: :environment do
    require 'itpkg/backgrounds/dispatcher'
    Itpkg::Background::Dispatcher.start
  end
end

namespace :journal do
  desc 'Start journal server'
  task :start, [:host, :port] => :environment do |_, args|
    args.with_defaults(host: 'localhost')
    args.with_defaults(port: 10002)
    require 'itpkg/backgrounds/journal'
    Itpkg::Background::Journal.start  args[:host], args[:port]
  end
end
