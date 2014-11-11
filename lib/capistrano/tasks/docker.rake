namespace :docker do
  NAME='itpkg_registry'
  PORT=5000

  desc 'Install docker registry'
  task :install do
    on roles(:repo) do
      [
          'sudo apt-get install -y docker.io',
          "sudo docker run -p #{PORT}:#{PORT} --name itpkg_registry -v #{Rails.root}/config/docker:/registry-conf -e SETTINGS_FLAVOR=prod -e DOCKER_REGISTRY_CONFIG=/registry-conf/config.yml -d registry",
          'sudo docker pull ubuntu',
          "sudo docker tag ubuntu localhost:#{PORT}/ubuntu",
          "sudo docker push localhost:#{PORT}/ubuntu"
      ].each { |cmd| execute cmd }


    end
  end

  desc 'Start docker registry'
  task :start do
    on roles(:repo) do
      #execute "if sudo docker ps -a | grep --quiet #{NAME}; then ; else ; fi"
      execute "sudo docker start #{NAME}"
    end
  end

  desc 'Stop docker registry'
  task :stop do
    on roles(:repo) do
      execute "sudo docker stop #{NAME}"
    end
  end

  desc 'Docker registry status'
  task :status do
    on roles(:repo) do
      ['docker version', 'docker info', 'docker ps'].each { |cmd| execute "sudo #{cmd}" }
    end
  end

end