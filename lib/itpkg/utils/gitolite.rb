require 'rugged'

module Itpkg
  class Gitolite
    attr_reader :root

    def initialize
      @root = "#{Rails.root}/tmp/storage/gitolite-admin"
    end

    def open
      @repo = Dir.exist?(@root) ? Rugged::Repository.new(@root) : clone
    end

    def export
      File.open("#{@root}/1.log", 'w') do |f|
        f.write Time.now
      end
    end


    def push
      @repo.remotes['origin'].push(@repo.references.map { |r| r.name }, {credentials: ssh_key_credential})
    end

    def commit
      index = @repo.index
      index.read_tree @repo.head.target.tree
      index.add_all '.'

      Rugged::Commit.create(@repo, {
          parents: [
              @repo.branches['master'].target_id,
              @repo.branches['origin/master'].target_id
          ],
          tree: index.write_tree(@repo),
          message: 'Commit',
          author: {name: Setting.git_admin_username, email: Setting.git_admin_email},
          committer: {name: Setting.git_admin_username, email: Setting.git_admin_email},
          update_ref: 'HEAD'
      })

    end

    def close
      @repo.close
    end

    def clone
      Rugged::Repository.clone_at "#{Setting.git_admin_host}:gitolite-admin", @root, {credentials: ssh_key_credential}
    end

    def pull

      remote = @repo.remotes['origin']
      success = remote.fetch({credentials: ssh_key_credential})
      fail Error('Unable to pull without credentials') unless success
      #
      # index = @repo.merge_commits(
      #     @repo.branches['master'].target_id,
      #     @repo.branches['origin/master'].target_id
      # )
      #
      # fail 'Conflict detected!' if index.conflicts?
      #
      #
      # Rugged::Commit.create(@repo, {
      #     parents: [
      #         @repo.branches['master'].target_id,
      #         @repo.branches['origin/master'].target_id
      #     ],
      #     tree: index.write_tree(@repo),
      #     message: 'Merged `origin/master` into `master`',
      #     author: {name: Setting.git_admin_username, email: Setting.git_admin_email},
      #     committer: {name: Setting.git_admin_username, email: Setting.git_admin_email},
      #     update_ref: 'HEAD'
      # })


    end

    private
    def ssh_key_credential
      Rugged::Credentials::SshKey.new({
                                          username: Setting.git_admin_username,
                                          publickey: Setting.git_admin_pub_key,
                                          privatekey: Setting.git_admin_key
                                      })
    end


  end
end