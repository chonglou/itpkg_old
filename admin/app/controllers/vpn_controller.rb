require 'brahma/web/response'
require 'brahma/services/vpn'

class VpnController < ApplicationController
  before_action :require_login

  def index
    @ctl_links ={}
    Brahma::ClientService.list(current_user.fetch(:id), :vpn).each{|c|@ctl_links["/vpn/clients/#{c.id}"]=c.name}
    @ctl_links['/vpn/help']='帮助文档'
    @index='/vpn'
    goto_admin
  end

  def help
    list = Brahma::Web::List.new '帮助文档'
    render(json: list.to_h)
  end

end
