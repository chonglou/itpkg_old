class NodeTypes::PortsController < ApplicationController
  layout 'buttoned'

  before_action :must_admin!
  include NodeTypesHelper
  before_action :get_node_type


  def new
    @port = NtPort.new
    @tcp_options = _tcp_items
    render 'new'
  end

  def create
    @tcp_options = _tcp_items
    @port = NtPort.new params.require(:nt_port).permit(:t_port, :s_port, :tcp)
    @port.node_type_id = params[:node_type_id]
    if @port.save
      redirect_to node_type_path(params[:node_type_id])
    else
      render 'new'
    end
  end


  def edit
    @tcp_options = _tcp_items
    @port = NtPort.find params[:id]
    render 'edit'
  end

  def update
    @tcp_options = _tcp_items
    @port = NtPort.find params[:id]
    if @port.update(params.require(:nt_port).permit(:t_port, :s_port, :tcp))
      redirect_to node_type_path(params[:node_type_id])
    else
      render 'edit'
    end
  end

  def destroy
    NtPort.destroy(params[:id])
    redirect_to node_type_path(params[:node_type_id])
  end

  private
  def _tcp_items

  end

end