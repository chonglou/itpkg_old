require 'brahma/daemon'
require 'json'
require 'eventmachine'

module Brahma
  class Listener < Brahma::Daemon
    def initialize(id)
      super "itpkg-listener-#{id}", "#{Rails.root}/tmp/pids"
      @id = id
      $logger = @logger
    end

    def run
      super do
        begin
          EventMachine.run do
            Signal.trap('INT') { EventMachine.stop }
            Signal.trap('TERM') { EventMachine.stop }
            EventMachine.start_server('localhost', 10000+@id, Connection)
          end
        rescue => e
          @logger.error e
        end
      end
    end
  end

  class Connection <EM::Connection
    @@clients = []
    attr_reader :serial

    # EM handlers
    def post_init
      @serial = nil
      $logger.info '新的连接'
      ask_login
    end

    def unbind
      @@clients.delete(self)
      $logger.info "#{@serial} 离开"
    end

    def receive_data(data)
      if login?
        handle_message data.strip
      else
        handle_serial data.strip
      end
    end

    #helpers
    def other_peers
      @@clients.reject { |c| self==c }
    end

    def login?
      @serial && !@serial.empty?
    end

    def handle_serial(input)
      if input.empty? # todo 登录验证
        send_line :login, ['错误登录信息!']
      else
        #todo 登录写入登录信息
        @serial = input
        @@clients << self
        $logger.info "#{@serial} 刚刚登录"
        send_line :hi, [@serial]
      end
    end

    def ask_login
      self.send_line :login
    end

    def handle_message(input)
      if command?(input)
        handle_command input
        return
      end
      begin
        handle_task JSON.parse input
      rescue => e
        $logger.error e
        send_line :fail
      end
    end

    def handle_task(task)
      case task['act']
        when 'heart'
          self.send_line :ok
        when 'next'
          # todo
          self.send_line :command, ["ls -l /tmp", "ls -l /tmp1"]
          self.send_line :mysql, ["use test", "select now()", "select now1()"]
          self.send_line :file, ["/tmp/aaa/111.aaa", "flamen:flamen", "400", "1111", "2222", "\t3333"]
        when 'back'
          # todo
          $logger.info task.data
        else
          self.send_line :unknown
      end
    end

    def handle_command(cmd)
      case cmd
        when 'bye'
          self.close_connection
        else
          send_line :unknown
      end
    end

    def send_line(act, data=[])
      send_data "#{JSON.generate({act: act, data: data, created: Time.now})}\n"
    end

    def command?(input)
      input =~ /(bye|status)$/i
    end
  end
end