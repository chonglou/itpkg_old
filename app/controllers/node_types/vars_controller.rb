class NodeTypes::VarsController < ApplicationController
  before_action :must_admin!
  include NodeTypesHelper
  before_action :get_node_type

  def new
    @flags = _flag_options
    @var = NtVar.new
    render 'new', layout: 'node_types/view'
  end

  def create
    @flags = _flag_options
    @var = NtVar.new _params
    @var.node_type_id = params[:node_type_id]
    if @var.save
      redirect_to node_type_path(params[:node_type_id])
    else
      render 'new', layout: 'node_types/view'
    end
  end

  def edit
    @flags = _flag_options
    @var = NtVar.find params[:id]
    render 'edit', layout: 'node_types/view'
  end

  def update
    @flags = _flag_options
    @var = NtVar.find params[:id]

    if @var.update(_params)
      redirect_to node_type_path(params[:node_type_id])
    else
      render 'new', layout: 'node_types/view'
    end
  end

  def destroy
    NtVar.destroy(params[:id])
    redirect_to node_type_path(params[:node_type_id])
  end

  private
  def _params
    params.require(:nt_var).permit(:name, :def_v, :flag).tap do |v|
      v[:flag] = v[:flag].to_i if v[:flag]
    end
  end
  def _flag_options
    NtVar.flags.map { |k, v| [k,v] }
  end

end