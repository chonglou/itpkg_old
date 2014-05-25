require 'yaml'
require 'highline/import'

module Itpkg
  class Config
    def initialize(name)
      @name = name
    end

    def server
      load 'server'
    end

    def client
      load 'client'
    end

    def setup!
      server={}
      client={}
      puts '设置服务端信息'
      server['host'] = ask('地址？ '){|q|q.default='localhost'}.to_s
      server['port'] = ask('端口？ ', Integer) do |q|
        q.default = 8888
        q.in = 1..65536
      end
      puts '设置代理端信息'
      client['id'] = ask('ID？ ').to_s
      client['key'] = ask('KEY？ ').to_s
      write({'server'=>server, 'client'=>client})
    end

    private
    def load(key)
      YAML.load_file(@name).fetch(key)
    end

    def write(data)
      if File.exist?(@name)
        puts "文件[#{@name}]已存在,如有必要请先备份"
        ok = ask('确认覆盖?(y/n): ') { |q| q.default='n' }.to_s
        if ok == 'y'
          File.delete(@name)
        else
          puts '放弃更改'
          return
        end
      end
      File.open @name, 'w', 0400 do |file|
        file.write data.to_yaml
      end
      puts '保存成功'
    end
  end
end