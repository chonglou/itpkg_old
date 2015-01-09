require 'sidekiq'
require 'itpkg/linux/git'

class GitHookJob < ActiveJob::Base
  queue_as :git

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
