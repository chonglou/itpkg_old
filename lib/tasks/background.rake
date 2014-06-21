namespace :brahma do
  namespace :agent do
    desc '编译agent'
    task :build do
      `cd agent && make`
    end
  end

  namespace :listener do
    def by_id(id)
      ["#{PID_PATH}/listener_#{id}.pid", 8080+id.to_i]
    end

    desc '启动WebSocket[id]'
    task :start, [:id] do |_, args|
      args.with_defaults(id: 0)
      pid, port = by_id args[:id]
      if File.exist?(pid)
        puts "服务已经启动，如果进程不存在，请删除[#{pid}]文件"
      else
        puts `puma wss.ru -b tcp://localhost -p #{port} --pidfile #{pid} -e production -d config.ru`
      end
    end

    desc '停止WebSocket[id]'
    task :stop, [:id] do |_, args|
      args.with_defaults(id: 0)
      pid, _ = by_id args[:id]
      if File.exist?(pid)
        File.open pid do |file|
          puts `kill -9 #{file.read}`
        end
        FileUtils.rm pid
      else
        puts '服务未启动'
      end
    end

    desc '运行WebSocket服务'
    task :run do
      puts '开发模式启动'
      sh "rerun 'puma wss.ru -p 8088'"
    end
  end

  namespace :worker do
    desc '启动[id]'
    task :start, [:id] do |_, args|
      args.with_defaults(id: 0)
      require 'brahma/backgrounds/worker'
      Brahma::Worker.new(args[:id]).start
    end

    desc '停止[id]'
    task :stop, [:id] do |_, args|
      args.with_defaults(id: 0)
      require 'brahma/backgrounds/worker'
      Brahma::Worker.new(args[:id]).stop
    end
  end

  namespace :dispatcher do
    desc '启动'
    task :start do
      require 'brahma/backgrounds/dispatcher'
      Brahma::Dispatcher.new.start
    end

    desc '停止'
    task :stop do
      require 'brahma/backgrounds/dispatcher'
      Brahma::Dispatcher.new.stop
    end
  end
end
