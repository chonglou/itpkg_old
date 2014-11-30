require 'itpkg/services/permission'

module RepositoriesHelper
  def repositories_nav_buttons
    buttons = [
        {label: t('links.repository.show', name: @repository.name), url: repository_path(@repository), style: 'default'},
        {label: t('links.repository.list'), url: repositories_path, style: 'warning'}
    ]
    if _can_edit?
      buttons.insert 1, {label: t('links.repository.user.list', name: @repository.name), url: repository_users_path(repository_id: params[:repository_id]||params[:id]), style: 'success'}
      buttons.insert 1, {label: t('links.repository.edit', name: @repository.name), url: edit_repository_path(params[:repository_id]||params[:id]), style: 'primary'}
    end
    buttons
  end


  def repositories_can_view?
    @repository ||= Repository.find params[:id]
    uid = current_user.id
    @repository && @repository.enable && (RepositoryUser.find_by(repository_id: @repository.id, user_id: uid) || Itpkg::PermissionService.admin?(uid))
  end

  def repositories_can_edit?
    @repository ||= Repository.find params[:id]
    @repository && @repository.enable && Itpkg::PermissionService.admin?(uid)
  end
end