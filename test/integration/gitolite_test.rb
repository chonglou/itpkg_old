require 'test_helper'
require 'itpkg/utils/gitolite'
require 'fileutils'


class GitoliteTest < ActionDispatch::IntegrationTest
  ADMIN_PATH = "#{Rails.root}/tmp/storage/gitolite-admin"

  test 'clone admin' do
    assert @ig.admin!
  end

  def setup
    @ig = Itpkg::Gitolite
    if Dir.exist?(ADMIN_PATH)
      FileUtils.mv ADMIN_PATH, "#{ADMIN_PATH}-bak"
    end
    Setting.git_admin_host = 'localhost'
    Setting.git_admin_username = ENV['USER']
    Setting.git_admin_pub_key = "#{ENV['HOME']}/.ssh/id_rsa.pub"
    Setting.git_admin_key = "#{ENV['HOME']}/.ssh/id_rsa"

  end

  def teardown
    if Dir.exist?("#{ADMIN_PATH}-bak")
      FileUtils.mv "#{ADMIN_PATH}-bak", ADMIN_PATH
    end
    FileUtils.rm_r ADMIN_PATH
  end

end
