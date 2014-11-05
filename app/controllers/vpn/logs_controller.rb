class Vpn::LogsController < ApplicationController
  before_action :must_admin!
  def index
    @items = Vpn::Log.select(:username, :message, :created).last(200).map{|l| "#{l.created}[#{l.username}]: #{l.message}"}
  end
end
