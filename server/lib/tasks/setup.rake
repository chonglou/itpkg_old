require 'brahma/config/_store'
Brahma::Config.tasks('brahma_utils', %w(setup)) { |t| import t }

namespace :brahma do
  desc '服务配置[路径]'
  task :itpkg, [:path] do |_, args|
    args.with_defaults(path: 'config/itpkg.yml')
    require_relative '../config'
    Itpkg::Config.new(args[:path]).setup!
  end
end