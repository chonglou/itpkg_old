
module RepositoriesHelper
  def repositories_nav_buttons
    buttons = [
        {label: t('links.repository.user.list', name: @repository.name), url: repository_users_path(repository_id: params[:repository_id]||params[:id]), style: 'success'},
        {label: t('links.repository.show', name: @repository.name), url: repository_path(@repository), style: 'default'},
        {label: t('links.repository.list'), url: repositories_path, style: 'warning'}
    ]
    if repositories_can_edit?
      buttons.insert 1, {label: t('links.repository.edit', name: @repository.name), url: edit_repository_path(params[:repository_id]||params[:id]), style: 'primary'}
    end
    buttons
  end


  def repositories_can_view?
    @repository && @repository.enable && (current_user.is_reader_of?(@repository) || current_user.is_writer_of?(@repository) || current_user.is_creator_of?(@repository))
  end

  def repositories_can_edit?
    @repository && @repository.enable && (current_user.is_creator_of?(@repository))
  end

  def repositories_user_select_options
    User.where('id != ?', current_user.id).select{|u| u.confirmed? && !(u.is_reader_of?(@repository) || u.is_writer_of?(@repository) || u.is_creator_of?(@repository)) }.map{|u| [u.to_s, u.id]}
  end

end