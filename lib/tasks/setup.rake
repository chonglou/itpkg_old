namespace :brahma do
  namespace :setup do
    desc '设置站点信息'
    task :site do
      require 'brahma/config/site'
      Brahma::Config::Site.new.setup! :client
    end
  end
end