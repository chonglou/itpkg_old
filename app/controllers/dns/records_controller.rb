# INSERT INTO `dns_records`
# (`id`,`zone`,`host`,`type`,`data`,`ttl`,`mx_priority`,`refresh`,`retry`,`expire`,`minimum`,`serial`,`resp_person`,`primary_ns`)
# VALUES
# (1, 'example.com', '@', 'SOA', NULL, 180, NULL, 10800, 7200, 604800, 86400, 2011091101, 'admins.mail.hotmail.com', '77.84.21.84'),
# (2, 'example.com', '@', 'NS', '77.84.21.84', 180, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
# (3, 'example.com', '@', 'A', '77.84.21.84', 180, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
# (4, 'example.com', 'www', 'A', '77.84.21.84', 180, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
# (5, 'xn--unicode-example.com', '@', 'SOA', NULL, 180, NULL, 10800, 7200, 604800, 86400, 2011091101, 'admins.mail.hotmail.com', '77.84.21.84'),
# (6, 'xn--unicode-example.com', '@', 'NS', '77.84.21.84', 180, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
# (7, 'xn--unicode-example.com', '@', 'A', '77.84.21.84', 180, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
# (8, 'xn--unicode-example.com', 'www', 'A', '77.84.21.84', 180, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)
# ;
class Dns::RecordsController < ApplicationController
  before_action :must_admin!

  def index
    @buttons = [
        {label: t('links.dns_record.create'), url: new_dns_record_path, style: 'primary'},
        {label: t('links.dns'), url: dns_path, style: 'warning'},

    ]
    @records = Dns::Record.select(:id, :zone, :host, :flag, :data, :ttl, :mx_priority).map { |r| {cols: [r.zone, r.host, r.flag, r.data, r.ttl, r.mx_priority], url: edit_dns_record_path(r.id)} }
  end

  def new
    @record = Dns::Record.new
  end

  def create
    @record = Dns::Record.new _record_params
    if @record.save
      redirect_to dns_records_path
    else
      render 'new'
    end
  end

  def edit
    @record = Dns::Record.find params[:id]
  end

  def update
    @record = Dns::Record.find params[:id]
    if @record.update(_record_params)
      redirect_to dns_records_path
    else
      render 'edit'
    end
  end

  def destroy
    Dns::Record.destroy params[:id]
    redirect_to dns_records_path
  end

  private

  def _record_params
    case params.require(:dns_record).fetch(:flag)
      when 'A'
        params.require(:dns_record).permit(:host, :flag, :ttl, :zone, :code, :data)
      when 'NS'
        params.require(:dns_record).permit(:host, :flag, :ttl, :zone, :code, :data)
      when 'MX'
        params.require(:dns_record).permit(:host, :flag, :ttl, :zone, :code, :data, :mx_priority)
      when 'SOA'
        params.require(:dns_record).permit(:host, :flag, :ttl, :zone, :code)
      else
    end
  end
end
