class Repositories::UsersController < ApplicationController
  before_action :owner!

  def index
    @buttons = [
        {label: t('links.repository.user.create', name: @repository.name), url: new_repository_user_path, style: 'primary'},
        {label: t('links.repository.list', name: @repository.name), url: repositories_path, style: 'warning'},

    ]
    @users = RepositoryUser.where(repository_id: @repository.id).map do |ru|
      u = User.find ru.user_id
      {cols: [u.label, u.email, u.contact], url: repository_user_path(ru.id, repository_id: @repository.id)}
    end
  end

  def create
    lbl = params[:user_label]

    if lbl && lbl != current_user.label
      user = User.find_by label: lbl
      if user
        unless RepositoryUser.find_by repository_id: @repository.id, user_id: user.id
          # todo 应该发邀请信
          RepositoryUser.create repository_id: @repository.id, user_id: user.id
          GitAdminWorker.perform_async
          redirect_to(repository_users_path(@repository)) and return
        end
      end
    end
    flash[:alert] = t('labels.not_valid')
    render 'new'
  end

  def show
    @ru = RepositoryUser.find_by params[:id]
    @user = User.find @ru.user_id
  end

  def destroy
    RepositoryUser.destroy params[:id]
    GitAdminWorker.perform_async
    #todo 发送提示
    redirect_to repository_users_path(repository_id: @repository.id)
  end

  private
  def owner!
    if params[:repository_id] && current_user
      @repository = Repository.find_by(id: params[:repository_id], creator_id: current_user.id)
      return if @repository
    end
    flash[:alert] = t('labels.not_valid')
    redirect_to root_path
  end
end

