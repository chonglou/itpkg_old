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
    `protoc -I #{Rails.root}/tools --ruby_out #{Rails.root}/lib/itpkg #{Rails.root}/tools/protocols.proto`
    target = "#{ENV['GOPATH']}/src/itpkg"
    unless Dir.exist?(target)
      require 'fileutils'
      FileUtils.mkdir_p target
    end
    `protoc -I #{Rails.root}/tools --go_out #{target} #{Rails.root}/tools/protocols.proto`
  end
end
