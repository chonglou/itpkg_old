require 'itpkg/linux/openssl'

namespace :nginx do
  desc 'Generate nginx need openssl files'
  task :ssl do
    ssl = Linux::Openssl.new
    unless ssl.exist?
      ssl.init
    end
    name='web'
    unless ssl.exist?(name)
      ssl.init name
    end
    puts "#{'#'*20} FILES #{'#'*20}"
    puts ssl.files(name)
  end

end