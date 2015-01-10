class Logging::SearchesController < ApplicationController
  layout 'tabbed'
  before_action :authenticate_user!
  include LoggingNodesHelper
  before_action :_nav_items


  def quick
    case request.method
      when 'GET'
      when 'POST'
        kv = params.permit(:vips, :keyword, :tags, :since, :until).tap { |p|
          kv[:since] = Time.parse(kv[:since]) if kv[:since]
          kv[:until] = Time.parse(kv[:until]) if kv[:until]
        }.delete_if {|_,v| v.nil? || v=='' }


        render 'show'
      else
        render status: 404
    end
  end

  def index
    size=params[:size]||120
    page=params[:page]||1
  end


  private
  def _nav_items
    @nav_items = logging_nodes_nav_items
  end
end
