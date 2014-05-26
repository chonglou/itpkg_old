require 'brahma/web/response'

class EmailController < ApplicationController
  before_action :require_login
  def index
    @ctl_links = {
        '/email/status'=>'当前状态'
    }
    @index='/email'
    goto_admin
  end
  def status
    list = Brahma::Web::List.new '主机列表'
    Client.where(user_id:current_user.fetch(:id), flag: Client.flags[:email]).all.each {|c|list.add c.name}
    render(json: list.to_h)
  end
end
