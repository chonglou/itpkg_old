class Dns::AclsController < ApplicationController
  before_action :must_admin!
  def index
    @buttons = [
        {label: t('links.dns_acl.create'), url: new_dns_acl_path, style: 'primary'},
        {label: t('links.dns'), url: dns_path, style: 'warning'},

    ]
    @acls = Dns::Acl.select(:id, :country, :region).map { |a| {cols: [a.to_s], url: edit_dns_acl_path(a.id)} }
  end
  def new
    @acl = Dns::Acl.new
  end

  def create
    @acl = Dns::Acl.new _acl_params
    if @acl.save
      redirect_to dns_acls_path
    else
      render 'new'
    end
  end
  def edit
    @acl = Dns::Acl.find params[:id]
  end
  def update
    @acl = Dns::Acl.find params[:id]
    if @acl.update(_acl_params)
      redirect_to dns_acls_path
    else
      render 'edit'
    end
  end

  def destroy
    if Dns::Record.where(code: params[:id]).count >0
      flash[:alert] = t('labels.in_using')
    else
      Dns::Acl.destroy params[:id]
    end
    redirect_to dns_acls_path
  end

  private
  def _acl_params
    params.require(:dns_acl).permit(:country, :region)
  end

end
