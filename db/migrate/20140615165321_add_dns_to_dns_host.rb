class AddDnsToDnsHost < ActiveRecord::Migration
  def change
    add_column :dns_hosts, :dns1, :string, null:false, default: '8.8.8.8'
    add_column :dns_hosts, :dns2, :string, null:false, default:'8.8.4.4'
  end
end
