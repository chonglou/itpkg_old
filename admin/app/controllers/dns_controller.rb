require 'brahma/web/response'

class DnsController < ApplicationController
  before_action :require_login
  def index
    @ctl_links = {
        '/dns/status'=>'当前状态'
    }
    @index='/dns'
    goto_admin
  end
  def status
    list = Brahma::Web::List.new '主机列表'
    Client.where(user_id:current_user.fetch(:id), flag: Client.flags[:dns]).all.each {|c|list.add c.name}
    render(json: list.to_h)
  end
end
