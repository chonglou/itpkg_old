namespace :docker do
  desc 'Start docker registry'
  task :start do
    port=8080
    `sudo docker run -p #{port}:#{port} -e DOCKER_REGISTRY_CONFIG=config/docker.yml registry`
  end

  desc 'Stop docker registry'
  task :stop do

  end

  desc :'Docker registry status'
  task :status do

  end


end