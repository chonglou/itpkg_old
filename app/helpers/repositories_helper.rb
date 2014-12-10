
module RepositoriesHelper
  def repositories_nav_buttons
    buttons = [
        {label: t('links.repository.show', name: @repository.name), url: repository_path(@repository), style: 'default'},
        {label: t('links.repository.list'), url: repositories_path, style: 'warning'}
    ]
    if repositories_can_edit?
      buttons.insert 1, {label: t('links.repository.user.list', name: @repository.name), url: repository_users_path(repository_id: params[:repository_id]||params[:id]), style: 'success'}
      buttons.insert 1, {label: t('links.repository.edit', name: @repository.name), url: edit_repository_path(params[:repository_id]||params[:id]), style: 'primary'}
    end
    buttons
  end


  def repositories_can_view?
    @repository && @repository.enable && (current_user.has_role?(:reader, @repository) || current_user.has_role?(:writer, @repository) || current_user.has_role?(:creator, @repository))
  end

  def repositories_can_edit?
    @repository && @repository.enable && (current_user.has_role?(:creator, @repository))
  end
end