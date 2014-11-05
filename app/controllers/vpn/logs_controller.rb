class Vpn::LogsController < ApplicationController
  before_action :must_admin!
  def index
    @items = Vpn::Log.select(:email, :message, :created).last(200).map{|l| "#{l.created}[#{l.email}]: #{l.message}"}
  end
end
