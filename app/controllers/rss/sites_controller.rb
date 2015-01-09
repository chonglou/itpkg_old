class Rss::SitesController < ApplicationController
  layout 'tabbed'
  before_action :must_admin!
  before_action :_nav_items
  include SettingsHelper

  def index
    @items = RssSite.all.map { |rs| {cols: [rs.title, rs.url, rs.last_sync], url: edit_rss_site_path(rs.id)} }
    @buttons =  [label: t('links.rss_site.create'), url: new_rss_site_path, style: 'primary']
  end

  def new
    @site = RssSite.new
  end

  def create
    @site = RssSite.create _rs_params
    if @site.save
      RssSyncJob.perform_later @site.id
      redirect_to rss_sites_path
    else
      render 'new'
    end
  end

  def edit
    @site = RssSite.find params[:id]
  end

  def update
    @site = RssSite.find params[:id]
    if @site.update(_rs_params)
      RssSyncJob.perform_later @site.id
      redirect_to rss_sites_path
    else
      render 'edit'
    end
  end

  def destroy
    RssSite.destroy params[:id]
    redirect_to rss_sites_path
  end

  private
  def _rs_params
    params.require(:rss_site).permit(:url)
  end

  def _nav_items
    @nav_items = settings_nav_items
  end
end
