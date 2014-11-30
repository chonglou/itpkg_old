class NodeTypes::VarsController < ApplicationController
  before_action :must_admin!
  include NodeTypesHelper
  before_action :get_node_type

  def new
    @var = NtVar.new
    render 'new', layout:'node_types/view'
  end

  def create
    @var = NtVar.new params.require(:nt_var).permit(:name, :def_v)
    @var.node_type_id = params[:node_type_id]
    if @var.save
      redirect_to node_type_path(params[:node_type_id])
    else
      render 'new', layout:'node_types/view'
    end
  end

  def edit
    @var = NtVar.find params[:id]
    render 'edit', layout:'node_types/view'
  end

  def update
    @var = NtVar.find params[:id]
    if @var.update(params.require(:nt_var).permit(:name, :def_v))
      redirect_to node_type_path(params[:node_type_id])
    else
      render 'new', layout:'node_types/view'
    end
  end

  def destroy
    NtVar.destroy(params[:id])
    redirect_to node_type_path(params[:node_type_id])
  end


end