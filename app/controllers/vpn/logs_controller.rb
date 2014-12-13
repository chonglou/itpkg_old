class Vpn::LogsController < ApplicationController
  before_action :must_admin!

  def index
    @items = Vpn::Log.order(id: :desc).page(params[:page])
    @logs = @items.map do |i|
      {
          cols:[i.user, "#{i.trusted_ip}:#{i.trusted_port}", "#{i.remote_ip}:#{i.remote_port}", i.start_time, i.end_time, i.received, i.send, i.message]
      }
    end
  end
end
