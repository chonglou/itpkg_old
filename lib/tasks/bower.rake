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
    unless Dir.exist?(third_home)
      puts `mkdir -pv #{third_home}`
    end

    #-------JStree-------
    s = "#{bower_home}/jstree"
    d = "#{third_home}/jstree"
    unless Dir.exists?(d)
      puts `cp -av #{s}/dist #{d}`
    end

    #-------viewerjs------
    s = "#{bower_home}/viewerjs"
    d = "#{third_home}/viewerjs"
    unless Dir.exists?(d)
      puts `cp -av #{s}/ViewerJS #{d}`
    end

  end

end

task 'assets:precompile' => 'bower:third'


