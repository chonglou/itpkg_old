namespace :brahma do
  namespace :faye do

    def faye_pid(port)
      "tmp/pids/faye-#{port}.pid"
    end

    desc '启动faye[port]'
    task :start, [:port] do |_, args|
      args.with_defaults(port: 8088)
      port = args[:port]

      pid = faye_pid port
      if File.exist?(pid)
        puts "服务已经启动，如果进程不存在，请删除[#{pid}]文件"
      else
        puts `puma -b tcp://localhost -p #{port} --pidfile #{pid} -e production -d faye.ru`
      end

    end

    desc '停止faye[port]'
    task :stop, [:port] do |_, args|
      args.with_defaults(port: 8088)
      pid = faye_pid args[:port]
      if File.exist?(pid)
        File.open pid do |file|
          puts `kill -9 #{file.read}`
        end
        FileUtils.rm pid
      else
        puts '服务未启动'
      end
    end

    desc '运行faye'
    task :run do
      puts '开发模式启动'
      sh "rerun 'puma -e development -p 8088 faye.ru'"
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
