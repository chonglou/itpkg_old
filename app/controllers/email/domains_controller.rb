class Email::DomainsController < ApplicationController
  before_action :must_admin!

  def index
    @buttons = [
        {label: t('links.email_domain.create'), url: new_email_domain_path, style: 'primary'},
        {label: t('links.email'), url: email_path, style: 'warning'},

    ]
    @domains = Email::Domain.select(:id, :name).map do |h|
      {
          cols: [h.name],
          url: edit_email_domain_path(h.id)
      }
    end

  end

  def new
    @domain = Email::Domain.new
  end

  def create
    @domain = Email::Domain.new params.require(:email_domain).permit(:name)
    if @domain.save
      redirect_to email_domains_path
    else
      render 'new'
    end
  end

  def edit
    @domain = Email::Domain.find params[:id]
  end

  def update

    if in_use?(params[:id])
      flash[:alert] = t('labels.in_using')
      redirect_to email_domains_path
    else
      @domain = Email::Domain.find params[:id]
      if @domain.update(params.require(:email_domain).permit(:name))
        redirect_to email_domains_path
      else
        render 'edit'
      end
    end

  end

  def destroy
    if in_use?(params[:id])
      flash[:alert] = t('labels.in_using')
    else
      Email::Domain.destroy params[:id]
    end
    redirect_to email_domains_path
  end

  private
  def in_use?(domain)
    Email::User.where(domain_id: domain).count >0
  end
end
