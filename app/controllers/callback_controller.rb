require 'json'
class CallbackController < ApplicationController
  #protect_from_forgery except: :git
  skip_before_action :verify_authenticity_token

  def confirm
    token = params[:token]
    user = current_user
    c = Confirmation.find_by token: token
    if user && c && c.submit? && c.user_id == user.id
      if Time.now <= c.deadline
        extra = JSON.parse c.extra
        RepositoryUser.create repository_id: extra.fetch('repository_id'), user_id: user.id, writable:true
        GitAdminWorker.perform_async
        c.update status: :done
        flash[:notice] = t('labels.success')
        redirect_to(params[:from] || root_path) and return
      else
        c.update status: :done
        flash[:alert] = t('labels.not_valid')
      end
      flash[:alert] = t('labels.not_valid')
    end
    redirect_to(root_path)
  end

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
