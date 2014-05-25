ENV['RACK_ENV'] ||= 'development'
PID_FILE='tmp/itpkgd.pid'

namespace :brahma do
  namespace :web do
    def itpkg_host_info
      require_relative '../config'
      Itpkg::Config.new('config/itpkg.yml').load ENV['RACK_ENV']
    end

    desc '启动'
    task :start do
      if File.exist?(PID_FILE)
        puts "服务已经启动，如果进程不存在，请删除[#{PID_FILE}]文件"
      else

        `puma -b tcp://localhost -p #{itpkg_host_info.fetch(:port)} --pidfile #{PID_FILE} -e production -d config.ru`
      end
    end
    desc '停止'
    task :stop do
      if File.exist?(PID_FILE)
        File.open pid do |file|
          puts `kill -9 #{file.read}`
        end
        FileUtils.rm PID_FILE
      else
        puts '服务未启动'
      end
    end

    desc '调试启动'
    task :run do
      sh "rerun -- rackup --port #{itpkg_host_info.fetch(:port)} config.ru"
    end
  end
end