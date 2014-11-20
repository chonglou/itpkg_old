require 'itpkg/utils/gitolite'

class GitWorker
  include Sidekiq::Worker

  def export
    if Setting.git_modify_flag
      ig = Itpkg::Gitolite
      ig.open
      ig.export
      ig.push
      ig.close
    end
  end

end