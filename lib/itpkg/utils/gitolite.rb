require 'rugged'

module Itpkg
  module Gitolite
    module_function

    def admin!
      git_admin = "#{Rails.root}/tmp/storage/gitolite-admin"
      unless Dir.exist?(git_admin)
        Rugged::Repository.clone_at "#{Setting.git_admin_host}:gitolite-admin", git_admin, {
            credentials: ssh_key_credential
        }
      end
    end

    def ssh_key_credential
      Rugged::Credentials::SshKey.new({
                                          username: Setting.git_admin_username,
                                          publickey: Setting.git_admin_pub_key,
                                          privatekey: Setting.git_admin_key
                                      })
    end
  end
end