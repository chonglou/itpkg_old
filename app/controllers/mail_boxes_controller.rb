require 'itpkg/utils/encryptor'
require 'itpkg/utils/mailer'

class MailBoxesController < ApplicationController
  layout 'mail_box/base'
  before_action :_mb_user
  before_action :_must_login, only: [:new, :create, :password]

  def password

    case request.method
      when 'GET'
        render 'password'
      when 'POST'
        cfg = params.permit(:old_password, :new_password, :re_password)
        if cfg.fetch(:new_password).size >=8 && cfg.fetch(:new_password)==cfg.fetch(:re_password)
          mailer = Itpkg::Mailer.new @mailer.user, cfg.fetch(:old_password)
          email = @mailer.user
          begin
            mailer.test
            flash[:notice] = t('labels.success')
            Email::User.find_by(email:email).update password:Itpkg::MysqlHelper.email_password(cfg.fetch(:new_password))
            reset_session
            redirect_to mail_boxes_sign_in_path and return

          rescue => e #Net::IMAP::NoResponseError Errno::ECONNREFUSED
            flash[:alert] = e
          end
        else
          flash[:alert] = t('labels.email_password')
        end
        render 'password'
      else
        render status: 404
    end
  end

  def sign_in
    if @mailer
      _goto_main and return
    end

    case request.method
      when 'GET'
        render 'sign_in'
      when 'POST'
        cfg = _sign_in_params
        mailer = Itpkg::Mailer.new cfg.fetch(:username), cfg.fetch(:password)
        begin
          mailer.test
          session[_session_key] = Itpkg::Encryptor.encode cfg
          flash[:notice] = t('labels.success')
          _goto_main and return
        rescue => e
          flash[:alert] = e
          render 'sign_in'
        end

      else
        render status: 404
    end
  end

  def sign_out
    reset_session
    redirect_to mail_boxes_sign_in_path
  end

  def create
    kv=params.permit(:to, :subject, :body)
    begin
      @mailer.push(kv.fetch(:to).split(';'), kv.fetch(:subject), kv.fetch(:body))
      flash[:notice] = t('labels.success')
      redirect_to new_mail_box_path
    rescue => e
      flash[:alert] = e
      render 'new'
    end

  end

  def index
    unless @mailer
      redirect_to(mail_boxes_sign_in_path) and return
    end
    unless params[:label]
      _goto_main and return
    end

    @items = @mailer.folders.map { |n| {name: n, url: mail_boxes_url(label: n)} }

    begin
      @mails = @mailer.pull(params[:label])
    rescue =>e
      flash[:alert] = e
      @mails = []
    end
  end

  private
  def _session_key
    :mb_user
  end

  def _mb_user
    if session.has_key? _session_key
      cfg = Itpkg::Encryptor.decode session[_session_key]
      @mailer = Itpkg::Mailer.new cfg.fetch(:username), cfg.fetch(:password)
    end
  end

  def _goto_main
    redirect_to(mail_boxes_path(label: 'INBOX'))
  end

  def _sign_in_params
    params.permit(:username, :password)
  end

  def _must_login
    unless @mailer
      flash[:alert] = t('labels.must_login')
      redirect_to mail_boxes_sign_in_path
    end
  end
end
