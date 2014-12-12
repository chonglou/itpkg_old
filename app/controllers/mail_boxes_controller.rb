require 'itpkg/utils/encryptor'

class MailBoxesController < ApplicationController
  layout 'mail_box/base'
  before_action :_mb_user
  before_action :_must_login, only: [:show, :new, :create, :edit, :update, :destroy]

  def inbox

  end

  def sign_in
    if @user
      redirect_to(mail_boxes_inbox_path)  and return
    end

    case request.method
      when 'GET'
        render 'sign_in'
      when 'POST'
        session[_session_key] = Itpkg::Encryptor.encode _sign_in_params
        redirect_to(mail_boxes_inbox_path) and return
      else
        render status:404
    end
  end

  def sign_out
    reset_session
    redirect_to mail_boxes_sign_in_path
  end

  def index
    redirect_to(@user ? mail_boxes_inbox_path : mail_boxes_sign_in_path)
  end

  private
  def _session_key
    :mb_user
  end
  def _mb_user
    if session.has_key? _session_key
      @user = Itpkg::Encryptor.decode session[_session_key]
    end
  end
  def _goto_main
    redirect_to( mail_boxes_path(label: :inbox))
  end
  def _sign_in_params
    params.permit(:username, :password)
  end
end
