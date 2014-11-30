class NodeTypes::VolumesController < ApplicationController
  before_action :must_admin!
  include NodeTypesHelper
  before_action :get_node_type

  def new
    @volume = NtVolume.new
    render 'new', layout:'node_types/view'
  end

  def create
    @volume = NtVolume.new params.require(:nt_volume).permit(:d_path, :s_path)
    @volume.node_type_id = params[:node_type_id]
    if @volume.save
      redirect_to node_type_path(params[:node_type_id])
    else
      render 'new', layout:'node_types/view'
    end
  end

  def destroy
    NtVolume.destroy(params[:id])
    redirect_to node_type_path(params[:node_type_id])
  end

end