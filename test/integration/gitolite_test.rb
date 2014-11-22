require 'test_helper'
require 'itpkg/linux/git'
require 'fileutils'


class GitoliteTest < ActionDispatch::IntegrationTest
  test '0 git clone and pull' do
    @git.pull
  end

  test '1 write commit and push' do
    @git.commit('test') do |index|
      @git.write(index, '1.txt') do |f|
        f.puts Time.now
      end
    end
    @git.push
  end

  def teardown
    @git.close
  end

  def setup
    @git = Linux::Git.new 'testing', {
        host: 'localhost',
        username: ENV['USER'],
        public_key: "#{ENV['HOME']}/.ssh/id_rsa.pub",
        private_key: "#{ENV['HOME']}/.ssh/id_rsa",
        email: 'git@test.com'
    }
    @git.open
  end

end
