require 'brahma/web/response'

class MonitorController < ApplicationController
  before_action :require_login

  def index
    @ctl_links = {
        '/monitor/help' => '帮助文档'
    }
    @index='/monitor'
    goto_admin
  end

end
