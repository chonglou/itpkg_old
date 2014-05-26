require 'brahma/web/response'

class VpnController < ApplicationController
  before_action :require_login
  def index
    @ctl_links = {
        '/vpn/status'=>'当前状态'
    }
    @index='/vpn'
    goto_admin
  end
  def status
    list = Brahma::Web::List.new '主机列表'
    Client.where(user_id:current_user.fetch(:id), flag: Client.flags[:vpn]).all.each {|c|list.add c.name}
    render(json: list.to_h)
  end
end
