class StatusController < ApplicationController
  layout 'status/base'

  before_action :must_admin!
  include ActionView::Helpers::DateHelper

  def versions

    @items=[
        t('links.status.versions.rb', ruby: RUBY_VERSION, rails: Rails.version, app: Setting.version),
        t('links.status.versions.env', env: Rails.env),
        t('links.status.versions.root', root: Rails.root),
        t('links.status.versions.uptime', time: time_ago_in_words(Itpkg::BOOTED_AT)),
        t('links.status.versions.time', time: Time.now),

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

end
