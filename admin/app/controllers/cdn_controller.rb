require 'brahma/web/response'

class CdnController < ApplicationController
  before_action :require_login

  def index
    @ctl_links = {
        '/cdn/help'=>'帮助文档'
    }
    @index='/cdn'
    goto_admin
  end
  def help
    list = Brahma::Web::List.new '帮助文档'
    render(json: list.to_h)
  end
end
