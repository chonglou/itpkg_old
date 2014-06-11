require 'brahma/web/response'

class CdnController < ApplicationController
  before_action :require_login

  def index
    @ctl_links = {
        '/cdn/help' => '帮助文档'
    }
    @index='/cdn'
    goto_admin
  end

end
