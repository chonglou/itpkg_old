require 'yaml'
require 'brahma/config/_store'

module Itpkg
  class Config < Brahma::Config::Storage
    def load(env)
      cfg = read.fetch env
      {
          port:cfg.fetch('port'),
          domain: cfg.fetch('domain')
      }
    end

    def setup!
      p_s '配置Server信息'
      domain = ask('域名： '){|q|q.default = 'localhost'}.to_s
      port = ask('端口： ',Integer) do |q|
        q.default = 8888
        q.in = 1..65536
      end
      data = {}
      Brahma::Config::ENVIRONMENTS.each do |env|
        data[env] = {'domain'=>domain, 'port'=>port}
      end
      p_s '检查完毕'
      write data
    end
  end
end