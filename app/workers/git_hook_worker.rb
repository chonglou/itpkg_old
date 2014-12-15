require 'sidekiq'
require 'itpkg/linux/git'

module Itpkg
  class GitHookWorker
    include Sidekiq::Worker
    sidekiq_options queue: :git


    def perform(name)
      @git = Linux::Git.new name, Setting.git
      logger.info "open from #{@git.url}"
      @git.open
      logger.info "pull from #{@git.url}"
      @git.pull
      logger.info "close #{@git.url}"
      @git.close
    end
  end
end