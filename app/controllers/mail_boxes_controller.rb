require 'itpkg/utils/encryptor'

class MailBoxesController < ApplicationController
  layout 'mail_box/view'
  before_action :_imap
  before_action :_must_login, only: [:show, :index, :new, :create, :edit, :update, :destroy]

  def sign_in
    if @imap
      _goto_main and return
    end
    case request.method
      when 'GET'
        render 'sign_in'
      when 'POST'
        session[_session_key] = Itpkg::Encryptor.encode _sign_in_params
        _goto_main and return
      else
        render status:404
    end
  end

  def sign_out
    reset_session
    puts '#'*100, session
    redirect_to mail_boxes_sign_in_path
  end

  def index

  end

  private
  def _test_auth
    #todo
    true
  end
  def _session_key
    :mb_user
  end

  def _goto_main
    redirect_to( mail_boxes_path(label: :inbox))
  end

  def _imap
    if current_user
      session.delete _session_key
      @imap = current_user.settings.mail_box
    else
      if session.has_key? _session_key
        @imap = session[_session_key]
      end
    end
    @imap
  end

  def _must_login

    unless _imap
      flash[:alert] = t('labels.must_login')
      redirect_to mail_boxes_sign_in_path
    end

  end

  def _must_non_login
    if _imap
      redirect_to mail_boxes_path
    end
  end

  def _sign_in_params
    params.permit(:smtp_host, :smtp_port, :imap_host,:imap_port, :username, :password)
  end
end
