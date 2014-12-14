class NodeTypes::VolumesController < ApplicationController
  layout 'buttoned'

  include NodeTypesHelper
  before_action :must_admin!
  before_action :get_node_type


  def new
    @volume = NtVolume.new
    render 'new'
  end

  def create
    @volume = NtVolume.new params.require(:nt_volume).permit(:t_path, :s_path)
    @volume.node_type_id = params[:node_type_id]
    if @volume.save
      redirect_to node_type_path(params[:node_type_id])
    else
      render 'new'
    end
  end

  def edit
    @volume = NtVolume.find params[:id]
    render 'edit'
  end

  def update
    @volume = NtVolume.find params[:id]
    if @volume.update(params.require(:nt_volume).permit(:t_path, :s_path))
      redirect_to node_type_path(params[:node_type_id])
    else
      render 'edit'
    end
  end
  def destroy
    NtVolume.destroy(params[:id])
    redirect_to node_type_path(params[:node_type_id])
  end

end