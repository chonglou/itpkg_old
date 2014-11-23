require 'itpkg/linux/git'

class RepositoriesController < ApplicationController
  before_action :authenticate_user!

  def log
    repo = Repository.find params[:repository_id]
    oid = params[:oid]
    if oid && _can_view?(repo)

      git = Linux::Git.new repo.name
      git.open
      @patch = git.patch(oid)
      git.close
      render 'log', layout:false
      #render plain:@patch
    end
  end
  def index
    uid = current_user.id
    rs = Repository.where(creator_id: uid, enable: true)+current_user.repositories
    @repositories = rs.map { |p| {url: repository_path(p.id), name: p.name, details: p.title} }
  end

  def new
    @repository = Repository.new
  end

  def create
    @repository = Repository.new(params.require(:repository).permit(:name, :title))
    @repository.creator_id = current_user.id

    if @repository.save
      GitAdminWorker.perform_async
      redirect_to(repository_path(@repository.id)) and return
    end

    render :action => 'new'

  end

  def show
    @repository = Repository.find params[:id]
    if _can_view?(@repository)
      @buttons = [{label: t('links.repository.list'), url: repositories_path, style: 'warning'}]
      if _can_edit?(@repository)
        @buttons.insert 0, {label: t('links.repository.user.list', name: @repository.name), url: repository_users_path(repository_id:params[:id]), style: 'success'}
        @buttons.insert 0, {label: t('links.repository.edit', name: @repository.name), url: edit_repository_path(params[:id]), style: 'primary'}
      end

      git = Linux::Git.new @repository.name
      git.open

      @branches = git.branches
      unless @branches.empty?
        @branch = params[:branch]
        @branch = 'origin/master' unless @branches.include?(@branch)
        @logs = []
        @page = params[:page] ? params[:page].to_i : 1
        @size = 50
        git.logs(page:@page, size:@size, branch:@branch) do |oid, email, user, time, message|
          @logs << [oid, time, "#{user}<#{email}>", message]
        end
        git.close
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
      r.update enable: false
      GitAdminWorker.perform_async
    else
      flash[:alert] = t('labels.in_using')

    end
    redirect_to repositories_path
  end


  private
  def _can_view?(repo)
    uid = current_user.id
    repo && repo.enable && (RepositoryUser.find_by(repository_id: repo.id, user_id: uid) || repo.creator_id == uid)
  end

  def _can_edit?(repo)
    repo && repo.enable && repo.creator_id == current_user.id
  end
end
