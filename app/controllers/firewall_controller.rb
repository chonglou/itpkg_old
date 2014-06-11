require 'brahma/web/response'

class FirewallController < ApplicationController
  before_action :require_login

  def index
    @ctl_links = {
        '/firewall/help' => '帮助文档'
    }
    @index='/firewall'
    goto_admin
  end

end
