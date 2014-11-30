module NodeTypesHelper

  def node_types_nav_buttons
    @node_type = NodeType.find(params[:node_type_id] || params[:id])
    @buttons = [
        {label: t('links.node_type.edit_docker', name:@node_type.name), url: edit_node_type_path(params[:id]), style: 'primary'},
        {label: t('links.node_type.list'), url: node_types_path, style: 'warning'},
    ]
  end
end
