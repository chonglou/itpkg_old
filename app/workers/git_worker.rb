require 'itpkg/utils/gitolite'

class GitWorker
  include Sidekiq::Worker

  def export
    ig = Itpkg::Gitolite
    ig.open
    ig.export
    ig.push
    ig.close
  end

end