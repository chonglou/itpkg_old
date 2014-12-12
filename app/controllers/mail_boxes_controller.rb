require 'itpkg/utils/encryptor'
require 'itpkg/utils/mailer'

class MailBoxesController < ApplicationController
  layout 'mail_box/base'
  before_action :_mb_user
  before_action :_must_login, only: [:show, :new, :create, :edit, :update, :destroy]


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
          _goto_main and return
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

  def destroy
    lbl = params[:label]
    @mailer.remove lbl, params[:id]
    redirect_to mail_boxes_path(label:lbl)
  end

  def index
    unless @mailer
      redirect_to(mail_boxes_sign_in_path) and return
    end
    unless params[:label]
      _goto_main and return
    end
    @items = @mailer.folders.map { |n| {name: t("links.mail_box.#{n.downcase}", default:n), url: mail_boxes_url(label: n)} }
    if @items.size == 1
      %w(Outbox Drafts Spam Trash).each {|n| @mailer.mkdir n}
      @items = @mailer.folders.map { |n| {name: t("links.mail_box.#{n.downcase}", default:n), url: mail_boxes_url(label: n)} }
    end
    #%w(inbox outbox drafts spam trash).each { |n| @items << {name: , url: mail_boxes_url(label: n)} }
    begin
      @mails = @mailer.pull(params[:label])
    rescue Net::IMAP::NoResponseError
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
