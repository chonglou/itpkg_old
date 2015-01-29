require 'fileutils'

namespace :bower do
  desc 'Install 3rd package'
  task :install do
    puts `bower install -p`

  end

  desc 'List 3rd package'
  task :list do
    puts `bower list`
  end

  desc 'Build third-part libary'
  task :third do
    bower_home = "#{Rails.root}/vendor/assets/bower_components"
    third_home = "#{Rails.root}/public/3rd"

    #-------JStree-------
    s = "#{bower_home}/jstree"
    d = "#{third_home}/jstree"
    unless Dir.exists?(d)
      puts `cp -av #{s}/dist #{d}`
    end

    #-------jquery.fileupload

    # s = "#{bower_home}/jquery-file-upload"
    # d = "#{third_home}/jquery-file-upload"
    # unless Dir.exists?(d)
    #   FileUtils.mkdir_p d
    #   %w(css img js).each {|n| puts `cp -av #{s}/#{n} #{d}/#{n}`}
    # end

  end

end

task 'assets:precompile' => 'bower:third'


