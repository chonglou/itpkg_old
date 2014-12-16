class Rss::ItemsController < ApplicationController
  before_action :authenticate_user!
  def index
    @items = RssItem.order(created: :desc).page(params[:page]).per(12)
  end
end
