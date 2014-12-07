require 'sidekiq'
require 'fileutils'
require 'itpkg/utils/string_helper'

class DockerWorker
  include Sidekiq::Worker
  sidekiq_options queue: :docker


  def perform(nt_id, cache=true)
    nt = NodeType.find nt_id
    logger.info "Begin build #{nt.name}"
    v = "v#{Itpkg::StringHelper.current}"
    bp = "#{Rails.root}/tmp/docker/#{v}"
    tag = "#{nt.name}:#{v}"

    FileUtils.mkdir_p bp
    File.open("#{bp}/Dockerfile", 'w') { |f| f.write nt.dockerfile.gsub("\r", '') }

    log = ''
    Open3.popen3("docker build #{'--no-cache' unless cache} -t #{tag} #{bp}") do |_, out, err, thr|
      log << "TAG: #{tag}"
      log << "STATUS: #{thr.value}\n"
      log << "STDOUT: #{out}\n"
      log << "STDERR: #{err}\n"
      if thr.value.success?
        log << 'PUSH: '
        log << `docker push #{tag}`
        log << "\n"
      end
    end
    logger.info log
    logger.info "End build #{nt.name}"

    UserMailer.log(user_id, "Build docker #{tag}", log)

  end
end
