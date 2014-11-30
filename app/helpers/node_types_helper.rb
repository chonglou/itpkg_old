module NodeTypesHelper
  def show_buttons
@buttons = [
        {label: t('links.node_type.edit_docker', name: @type.name), url: edit_node_type_path(params[:id]), style: 'primary'},
        {label: t('links.node_type.list'), url: node_types_path, style: 'warning'},
    ]
    end
  end
