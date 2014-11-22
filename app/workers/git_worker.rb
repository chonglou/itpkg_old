require 'sidekiq'
require 'itpkg/linux/git'

class GitWorker
  include Sidekiq::Worker
  sidekiq_options queue: :git

  NAME = 'gitolite-admin'

  def perform
    @git = Linux::Git.new NAME, Setting.git_admin
    logger.info "open from #{@git.url}"
    @git.open

    logger.info "commit to #{@git.url}"
    @git.commit('Export users from database') do |index|
      Dir[@git.real_path('keydir/*')].each do |f|
        f = File.basename f
        unless f == 'id_rsa.pub'
          remove index, "keydir/#{f}"
        end
      end

      @git.write(index, 'conf/gitolite.conf') do |f|
        f.puts 'repo gitolite-admin'
        f.puts "\tRW+\t= id_rsa"
        f.puts 'repo testing'
        f.puts "\tRW+\t= @all"

        Repository.where(enable:true).each do |r|
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

      @git.write(index, 'version') { |f| f.write Time.now }
    end

    logger.info "push to #{@git.url}"
    @git.push
    logger.info "close from #{@git.url}"
    @git.close
  end


  private

  def write_key(index, uid, label)
    pk = "keydir/#{label}.pub"
    unless File.exist?(@git.real_path(pk))
      key = SshKey.select(:public_key).find_by(user_id: uid)
      @git.write(index, pk) { |f| f.write key.public_key } if key
    end
  end
end