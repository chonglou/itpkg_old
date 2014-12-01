module NodeTypesHelper

  def node_types_nav_buttons
    @buttons = [
        {label: t('links.node_type.show', name:@node_type.name), url: node_type_path(@node_type.id), style: 'info'},
        {label: t('links.node_type.edit_docker', name:@node_type.name), url: edit_node_type_path(@node_type.id), style: 'primary'},
        {label: t('links.node_type.template.create', name:@node_type.name), url: new_node_type_template_path(node_type_id:@node_type.id), style: 'success'},
        {label: t('links.node_type.var.create', name:@node_type.name), url: new_node_type_var_path(@node_type.id), style: 'primary'},
        {label: t('links.node_type.port.create', name:@node_type.name), url: new_node_type_port_path(@node_type.id), style: 'info'},
        {label: t('links.node_type.volume.create', name:@node_type.name), url: new_node_type_volume_path(@node_type.id), style: 'primary'},
        {label: t('links.node_type.list'), url: node_types_path, style: 'warning'},
    ]
  end


  def get_node_type
    @node_type = NodeType.find params[:node_type_id]
  end
end
