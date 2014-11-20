require 'rugged'
require 'net/ssh'

module Itpkg
  class Gitolite
    def self.key_pairs(label)
      key = OpenSSL::PKey::RSA.new 2048
      {private_key: key.to_pem, public_key: "#{key.ssh_type} #{[key.public_key.to_blob].pack('m0')} #{label}@#{ENV['ITPKG_DOMAIN']}"}
    end

    attr_reader :root

    def initialize
      @root = "#{Rails.root}/tmp/storage/gitolite-admin"
    end

    def open
      @repo = Dir.exist?(@root) ? Rugged::Repository.new(@root) : clone
    end

    def write(index, filename, mode=0100644)
      File.open("#{@root}/#{filename}", 'w') { |f| yield f }
      index.add path: filename, oid: (Rugged::Blob.from_workdir @repo, filename), mode: mode
    end

    def remove(index, filename)
      File.unlink "#{@root}/#{filename}"
      index.remove filename
    end


    def export
      commit('Export users from database') do |index|
        Dir["#{@root}/keydir/*"].each do |f|
          f = File.basename f
          unless f == 'id_rsa.pub'
            remove index, "keydir/#{f}"
          end
        end
        write(index, 'conf/gitolite.conf') do |f|
          f.puts 'repo gitolite-admin'
          f.puts "\tRW+\t= id_rsa"
          f.puts 'repo testing'
          f.puts "\tRW+\t= @all"

          Repository.all.each do |r|
            f.puts "repo #{r.name}"

            u = r.creator
            f.puts "\tRW+\t= #{u.label}"
            write_key index, u.id, u.label

            RepositoryUser.where(repository_id: r.id).each do |ru|
              u = User.find(ru.user_id)
              f.puts "\t#{ru.writable ? 'RW+' : 'R'}\t = #{u.label}"
              write_key index, u.id, u.label
            end


          end

        end

        write(index, 'version') { |f| f.write Time.now }


      end
    end


    def push
      @repo.remotes['origin'].push(@repo.references.map { |r| r.name }, {credentials: ssh_key_credential})
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
          author: {name: Setting.git_admin.fetch(:user), email: Setting.git_admin.fetch(:email)},
          committer: {name: Setting.git_admin.fetch(:user), email: Setting.git_admin.fetch(:email)},
          update_ref: 'HEAD'
      })

    end

    def close
      @repo.close
    end

    def clone
      Rugged::Repository.clone_at "#{Setting.git_admin.fetch(:host)}:gitolite-admin", @root, {credentials: ssh_key_credential}
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
                                          username: Setting.git_admin.fetch(:user),
                                          publickey: Setting.git_admin.fetch(:pub),
                                          privatekey: Setting.git_admin.fetch(:key)
                                      })
    end

    def write_key(index, uid, label)
      pk = "keydir/#{label}.pub"
      unless File.exist?("#{@root}/#{pk}")
        write(index, pk) { |f| f.write SshKey.select(:public_key).find_by(user_id: uid).public_key }
      end
    end


  end
end