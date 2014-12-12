require 'itpkg/utils/encryptor'
require 'itpkg/utils/mailer'

class MailBoxesController < ApplicationController
  layout 'mail_box/base'
  before_action :_mb_user
  before_action :_must_login, only: [:show, :new, :create, :edit, :update, :destroy]

  def inbox

  end

  def create
    kv=params.permit(:to, :subject, :body)
    begin
      @mailer.push(kv.fetch(:to), kv.fetch(:subject), kv.fetch(:body))
      flash[:notice] = t('labels.success')
      redirect_to new_mail_box_path
    rescue => e
      flash[:alert] = e
      render 'new'
    end

  end

  def sign_in
    if @mailer
      redirect_to(mail_boxes_path) and return
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
          redirect_to(mail_boxes_path) and return
        rescue Net::IMAP::NoResponseError => e
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

  def index
    unless @mailer
      redirect_to(mail_boxes_sign_in_path) and return
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
    redirect_to(mail_boxes_path(label: :inbox))
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
