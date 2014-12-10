
class StatusController < ApplicationController
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

    render 'versions', layout: 'status/view'
  end

  def workers
    render 'workers', layout: 'status/view'
  end

  def logs
    @items = BgLog.order(_id: :desc).page(params[:page])
    render 'logs', layout: 'status/view'
  end

  def users
    @users = User.order(id: :desc).page(params[:page])
    @items = @users.map {|u|{cols:[u.email, u.current_sign_in_at||u.last_sign_in_at, u.contact.to_s]}}
    render 'users',layout: 'status/view'
  end
end
