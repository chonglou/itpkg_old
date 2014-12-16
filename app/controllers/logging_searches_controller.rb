class LoggingSearchesController < ApplicationController
  layout 'tabbed'
  before_action :must_ops!
  include LoggingNodesHelper
  before_action :_nav_items

  def quick
    case request.method
      when 'GET'
      when 'POST'
      else
        render status:404
    end
  end
  def index
  end


  private
  def _nav_items
    @nav_items = logging_nodes_nav_items
  end
end
