require 'itpkg/services/permission'

class RepositoriesController < ApplicationController
  before_action :authenticate_user!

  def index
    uid = current_user.id
    rs = Repository.where(creator_id: uid)
    Itpkg::PermissionService.resources(uid, 'MEMBER', 'repository').each {|id| rs << Repository.find(id)}
    @repositories = rs.map { |p| {url: repository_path(p.id), name: p.name, details: p.title} }
  end

  def new
    @repository = Repository.new
  end

  def create
    @repository = Repository.new(params.require(:repository).permit(:name, :title))
    @repository.creator_id = current_user.id
    if @repository.save
      redirect_to repository_path(@repository.id)
    else
      render :action => 'new'
    end

  end

  def show
    @repository = Repository.find params[:id]

    if _can_view?(@repository)
      @buttons = [{label: t('links.repository.list'), url: repositories_path, style: 'warning'}]
      if _can_edit?(@repository)
        @buttons.insert 0, {label: t('links.repository.edit', name: @repository.name), url: edit_repository_path(params[:id]), style: 'primary'}
      end
    else
      redirect_to repositories_path
    end
  end

  def edit
    @repository = Repository.find params[:id]
    unless _can_edit?(@repository)
      redirect_to repositories_path
    end
  end

  def update

    @repository = Repository.find params[:id]
    if _can_edit?(@repository)
      if @repository.update(params.require(:repository).permit(:title))
        redirect_to repository_path(@repository.id)
      else
        render action: 'edit'
      end
    else
      redirect_to repositories_path
    end

  end

  def destroy

    r = Repository.find params[:id]
    if _can_edit?(r) && RepositoryUser.where(repository_id: params[:id]).count == 0
      r.destroy
    else
      flash[:alert] = t('labels.in_using')

    end
    redirect_to repositories_path
  end


  private
  def _can_view?(repo)
    Itpkg::PermissionService.auth?("user://#{current_user.id}", 'MEMBER', "repository://#{repo.id}") || _can_edit?(repo)
  end

  def _can_edit?(repo)
    repo.creator_id == current_user.id
  end
end
