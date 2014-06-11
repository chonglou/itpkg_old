namespace :brahma do
  namespace :agent do
    desc '编译agent'
    task :build do
      `cd agent && make`
    end
  end
  
  namespace :listener do
    desc '启动[id]'
    task :start, [:id] do |_, args|
      args.with_defaults(id: 0)
      require 'brahma/listener'
      Brahma::Listener.new(args[:id].to_i).run
    end
    desc '停止[id]'
    task :stop, [:id] do |_, args|
      args.with_defaults(id: 0)
      require 'brahma/listener'
      Brahma::Listener.new(args[:id].to_i).kill
    end
  end

  namespace :worker do
    desc '启动[id]'
    task :start, [:id] do |_, args|
      args.with_defaults(id: 0)
      require 'brahma/worker'
      Brahma::Worker.new(args[:id]).start
    end
    desc '停止[id]'
    task :stop, [:id] do |_, args|
      args.with_defaults(id: 0)
      require 'brahma/worker'
      Brahma::Worker.new(args[:id]).stop
    end
  end

  namespace :dispatcher do
    desc '启动'
    task :start do
      require 'brahma/dispatcher'
      Brahma::Dispatcher.new.start
    end
    desc '停止[port]'
    task :stop do
      require 'brahma/dispatcher'
      Brahma::Dispatcher.new.stop
    end
  end
end
