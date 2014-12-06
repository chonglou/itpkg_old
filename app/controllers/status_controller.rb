class StatusController < ApplicationController
  before_action :must_admin!

  def versions
    @index = 0

    @items=[
        t('links.status.versions.rb', ruby: RUBY_VERSION, rails: Rails.version, app: Setting.version),
        t('links.status.versions.env', env: Rails.env),
        t('links.status.versions.root', root: Rails.root),
        t('links.status.versions.time', time: Time.now),
    ]

    render 'versions', layout: 'status/view'
  end

  def workers
    @index = 1
    render 'workers', layout: 'status/view'
  end

  def logs
    @index = 2
    render 'logs', layout: 'status/view'
  end
end
