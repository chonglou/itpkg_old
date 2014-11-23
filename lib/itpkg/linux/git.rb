require 'rugged'
require 'net/ssh'

module Linux
  class Git
    def self.key_pairs(username)
      key = OpenSSL::PKey::RSA.new 2048
      {private_key: key.to_pem, public_key: "#{key.ssh_type} #{[key.public_key.to_blob].pack('m0')} #{username}@#{ENV['ITPKG_DOMAIN']}"}
    end

    attr_reader :root, :name, :url

    def initialize(repo_name, host: 'localhost',
                   port: 22,
                   username: 'git',
                   email: "git@#{ENV['ITPKG_DOMAIN']}",
                   public_key: "#{ENV['HOME']}/.ssh/id_rsa.pub",
                   private_key: "#{ENV['HOME']}/.ssh/id_rsa")
      @root = "#{Rails.root}/tmp/repos/#{repo_name}"
      @name = repo_name
      @credential = Rugged::Credentials::SshKey.new({
                                                        username: username,
                                                        publickey: public_key,
                                                        privatekey: private_key
                                                    })
      @user ={name: username, email: email}
      @url = "ssh://#{username}@#{host}:#{port}/#{repo_name}.git"
    end

    def open
      @repo = Dir.exist?(@root) ? Rugged::Repository.new(@root) : Rugged::Repository.clone_at(@url, @root, {credentials: @credential})
    end

    def branches
      @repo.branches.each_name(:remote).sort
    end

    def target_id(branch)
      @repo.branches[branch].target_id
    end


    def logs(oid, size)

      walker = Rugged::Walker.new @repo
      walker.sorting( Rugged::SORT_DATE)
      #walker.sorting(Rugged::SORT_TOPO | Rugged::SORT_REVERSE)
      walker.push(oid)
      logs = []
      walker.each_with_index do |c, i|
        a = c.author
        logs << yield(c.oid, a.fetch(:email), a.fetch(:name), a.fetch(:time), c.message)
        break if i==size
      end
      walker.reset
      logs
    end

    def patch(oid)
      c = @repo.lookup oid
      unless c.parents.empty?
        diff = c.parents.first.diff(c)
        diff.patch
      end

    end

    def real_path(name)
      "#{@root}/#{name}"
    end

    def write(index, filename, mode=0100644)
      File.open(real_path(filename), 'w') { |f| yield f }
      index.add path: filename, oid: (Rugged::Blob.from_workdir @repo, filename), mode: mode
    end

    def remove(index, filename)
      File.unlink real_path(filename)
      index.remove filename
    end

    def push(branch='origin')
      @repo.remotes[branch].push(@repo.references.map { |r| r.name }, {credentials: @credential})
    end


    def commit(message)
      index = @repo.index
      index.read_tree @repo.head.target.tree
      yield index

      index.write
      Rugged::Commit.create(@repo, {
          parents: [@repo.head.target],
          tree: index.write_tree(@repo),
          message: message,
          author: @user,
          committer: @user,
          update_ref: 'HEAD'
      })

    end

    def close
      @repo.close
    end

    def pull(branch='origin')
      remote = @repo.remotes[branch]
      success = remote.fetch({credentials: @credential})
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

  end

end