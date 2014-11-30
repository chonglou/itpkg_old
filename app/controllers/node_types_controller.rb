class NodeTypesController < ApplicationController
  before_action :must_admin!

  def index
    @buttons = [
        {label: t('links.node_type.create'), url: new_node_type_path, style: 'primary'},
        {label: t('links.node_type.list'), url: node_types_path, style: 'warning'},

    ]
    @types = NodeType.select(:id, :name, :creator_id, :updated_at).map do |nt|
      {
          cols: [nt.name, nt.creator.label, nt.updated_at],
          url: node_type_path(nt.id)
      }
    end
  end

  def show
    @node_type = NodeType.find params[:id]
    render 'show', layout:'node_types/view'
  end

  def new
    user = current_user
    @type = NodeType.new
    @type.dockerfile = <<EOF
FROM base/ubuntu:latest
MAINTAINER #{user.label} <#{user.email}>

USER root
CMD ["/sbin/init"]
EOF
  end


  def create
    @type = NodeType.new params.require(:node_type).permit(:name, :dockerfile)
    user = current_user
    @type.creator_id = user.id

    if @type.save
      redirect_to node_type_path(@type.id)
    else
      render 'new'
    end
  end


  def edit
    @type = NodeType.find params[:id]
  end

  def update
    @type = NodeType.find params[:id]

    if @type.update(params.require(:node_type).permit(:dockerfile))
      redirect_to node_type_path(@type.id)
    else
      render 'edit'
    end
  end


  def destroy
    type = NodeType.find params[:id]
    if Node.where(node_type_id: type.id).count>0
      flash[:alert] = t('labels.in_using')
    else
      NtTemplate.destroy_all(node_type_id: type.id)
      NtPort.destroy_all(node_type_id: type.id)
      NtVolume.destroy_all(node_type_id: type.id)
      NtVar.destroy_all(node_type_id: type.id)
      type.destroy
    end
    redirect_to node_types_path
  end


end