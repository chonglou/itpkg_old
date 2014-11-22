class CallbackController < ApplicationController
  #protect_from_forgery except: :git
  skip_before_action :verify_authenticity_token

  def git
    ip = request.ip
    name = params[:name]

    ok = false
    reason = nil
    if ip == Setting.git.fetch(:host)
      if Repository.find_by(name: name, enable: true)
        GitHookWorker.perform_async name
        ok = true
      else
        reason = 'invalid repo name.'
      end
    else
      reason = 'invalid request ip'
    end

    render json: {ok: ok, created: Time.now, ip: ip, name: name, reason: reason}
  end
end
