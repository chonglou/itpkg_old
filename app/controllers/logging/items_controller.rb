class Logging::ItemsController < ApplicationController
  layout 'tabbed'
  before_action :must_ops!
  include LoggingNodesHelper
  before_action :_nav_items

  def index
    query = params[:query] ? JSON.parse(params[:query], symbolize_names: true) : {match_all: {}}
    @size = params[:size] ? params[:size].to_i : 120
    @page = params[:page] ? params[:page].to_i : 1

    @logs = LoggingItem.search( query: query,
                               sort: [{created: {order: 'desc'}}],
        size: @size,
        from: @size*(@page-1))
    @pager = Kaminari.paginate_array([],total_count: @logs.total).page(@page).per(@size)

  end

  private
  def _nav_items
    @nav_items = logging_nodes_nav_items
  end
end