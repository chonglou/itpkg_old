class StatusController < ApplicationController
  layout 'tabbed'

  before_action :must_admin!
  before_action :_nav_items
  include ActionView::Helpers::DateHelper

  def versions

    @items=[
        t('links.status.versions.os', info:`uname -a`),
        t('links.status.versions.rb', ruby: RUBY_VERSION, rails: Rails.version, app: Setting.version),
        t('links.status.versions.env', env: Rails.env),
        t('links.status.versions.root', root: Rails.root),
        t('links.status.versions.uptime', time: time_ago_in_words(Itpkg::BOOTED_AT)),
        #t('links.status.versions.time', time: Time.now),

    ]

    render 'versions'
  end

  def workers
    render 'workers'
  end

  def logs
    @items = BgLog.order(_id: :desc).page(params[:page])
    render 'logs'
  end

  private
  def _nav_items
    @nav_items = [
        {
            name: t('links.status.versions.title'),
            url: status_versions_url
        },
        {
            name: t('links.status.workers.title'),
            url: status_workers_url
        },
        {
            name: t('links.status.cache.title'),
            url: status_cache_url
        },
        {
            name: t('links.status.search.title'),
            url: status_search_url
        },
        {
            name: t('links.status.logs.title'),
            url: status_logs_url
        }
    ]

  end
end
