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
    {
        'jstree/dist'=>'jstree',
        'viewerjs/ViewerJS'=>'viewerjs'
    }.each{|s, d| puts(`cp -av #{bower_home}/#{s} #{third_home}/#{d}`) unless Dir.exist?("#{third_home}/#{d}")}


  end

end

task 'assets:precompile' => 'bower:third'


