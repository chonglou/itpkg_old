require 'brahma/web/response'

class EmailController < ApplicationController
  before_action :require_login
  def index
    @ctl_links = {
        '/email/help'=>'帮助文档'
    }
    @index='/email'
    goto_admin
  end
  def help
    list = Brahma::Web::List.new '帮助文档'
    render(json: list.to_h)
  end
end
