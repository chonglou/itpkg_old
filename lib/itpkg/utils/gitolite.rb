require 'rugged'

module Itpkg
  module Gitolite
    module_function

    def admin!
      git_admin = "#{Rails.root}/tmp/storage/gitolite-admin"
      unless Dir.exist?(git_admin)
        begin
          Rugged::Repository.clone_at 'localhost:gitolite-admin', git_admin
        rescue =>e
          Rails.logger.error e
        end
      end
    end
  end
end