require 'test_helper'
require 'itpkg/utils/gitolite'
require 'fileutils'


class GitoliteTest < ActionDispatch::IntegrationTest
  # ADMIN_PATH = "#{Rails.root}/tmp/storage/gitolite-admin"
  #
  # test '0 clone' do
  #   @ig.pull
  #   @ig.export
  #   @ig.push
  # end
  #
  #
  # def setup
  #   if Dir.exist?(ADMIN_PATH)
  #     FileUtils.mv ADMIN_PATH, "#{ADMIN_PATH}-bak"
  #   end
  #   Setting.git_admin = {
  #       host:'localhost',
  #       user:ENV['USER'],
  #       pub:"#{ENV['HOME']}/.ssh/id_rsa.pub",
  #       key:"#{ENV['HOME']}/.ssh/id_rsa",
  #       email:'git@test.com'
  #   }
  #
  #   SshKey.create(Itpkg::Gitolite.key_pairs('u1').merge(user_id:1))
  #   SshKey.create(Itpkg::Gitolite.key_pairs('u2').merge(user_id:2))
  #
  #   @ig = Itpkg::Gitolite.new
  #   @ig.open
  # end
  #
  #
  # def teardown
  #   FileUtils.rm_r ADMIN_PATH
  #   if Dir.exist?("#{ADMIN_PATH}-bak")
  #     FileUtils.mv "#{ADMIN_PATH}-bak", ADMIN_PATH
  #   end
  #   @ig.close
  # end

end
