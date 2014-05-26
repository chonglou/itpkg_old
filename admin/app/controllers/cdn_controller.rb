require 'brahma/web/response'

class CdnController < ApplicationController
  before_action :require_login

  def index
    @ctl_links = {
        '/cdn/status'=>'当前状态'
    }
    @index='/cdn'
    goto_admin
  end
  def status
    list = Brahma::Web::List.new '主机列表'
    Client.where(user_id:current_user.fetch(:id), flag: Client.flags[:cdn]).all.each {|c|list.add c.name}
    render(json: list.to_h)
  end
end
