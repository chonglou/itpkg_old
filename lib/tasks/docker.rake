namespace :docker do
  NAME='itpkg_registry'
  desc 'Start docker registry'
  task :start do
    port=5000
    puts `if sudo docker ps -a | grep --quiet #{NAME}; then sudo docker start #{NAME}; else sudo docker run -p #{port}:#{port} --name itpkg_registry -v #{Rails.root}/config/docker:/registry-conf -e SETTINGS_FLAVOR=prod -e DOCKER_REGISTRY_CONFIG=/registry-conf/config.yml -d registry; fi`
  end

  desc 'Stop docker registry'
  task :stop do
puts `sudo docker stop #{NAME}`

  end

  desc 'Docker registry status'
  task :status do
puts `sudo docker ps`
  end


end
