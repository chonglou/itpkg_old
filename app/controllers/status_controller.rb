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

  def user
    @user = User.find params[:id]
    case request.method
      when 'GET'
        render 'user', layout: false
      when 'POST'
        unless @user.is_root?
          if params[:status] == 'yes'
            @user.add_role :admin unless @user.is_admin?
          elsif params[:status] == 'no'
            @user.remove_role :admin if @user.is_admin?
          end
        end
        redirect_to status_users_path
      else
        render status: 404
    end


  end

  def users
    @users = User.order(id: :desc).page(params[:page])
    @items = @users.map { |u| {cols: [u.email, u.is_admin? ? 'Y':'N', u.current_sign_in_at||u.last_sign_in_at, u.contact.to_s], url: get_status_user_path(u.id)} }
    render 'users', layout: 'status/view'
  end
end
