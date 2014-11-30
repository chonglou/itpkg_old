class NodeTypes::PortsController < ApplicationController
  before_action :must_admin!


  def new
    @port = NtPort.new
    render 'new', layout:'node_types/view'
  end

  def create
    @port = NtPort.new params.require(:nt_port).permit(:d_port, :s_port, :tcp)
    @port.node_type_id = params[:node_type_id]
    if @port.save
      redirect_to node_type_path(params[:node_type_id])
    else
      render 'new', layout:'node_types/view'
    end
  end

  def destroy
    NtPort.destroy(params[:id])
    redirect_to node_type_path(params[:node_type_id])
  end
end