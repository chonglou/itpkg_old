require 'securerandom'
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
    success = false

    if lbl && lbl != current_user.label
      user = User.find_by label: lbl
      if user
        unless RepositoryUser.find_by repository_id: @repository.id, user_id: user.id
          c = Confirmation.create extra: {
              repository_id: @repository.id,
              type: :add_to_repository,
              from: repository_url(@repository.id)
          }.to_json,
                                  subject:t('mails.add_to_repository.subject', name:@repository.name),
                                  user_id: user.id, token: SecureRandom.uuid, deadline: 1.days.since
          UserMailer.delay.confirm(c.id)
          success = true
        end
      end
    end

    if success
      redirect_to(repository_users_path(@repository))
      else
        flash[:alert] = t('labels.not_valid')
        render 'new'
    end
  end

  def show
    @ru = RepositoryUser.find_by params[:id]
    @user = User.find @ru.user_id
  end

  def destroy
    ur = RepositoryUser.find params[:id]
    uid = ur.user_id
    ur.destroy
    GitAdminWorker.perform_async
    UserMailer.delay.remove_from_repository(repository_id: @repository.id, user_id: uid)
    redirect_to repository_users_path(repository_id: @repository.id)
  end

  private
  def owner!
    if params[:repository_id] && current_user
      @repository = Repository.find_by(id: params[:repository_id], creator_id: current_user.id, enable: true)
      return if @repository
    end
    flash[:alert] = t('labels.not_valid')
    redirect_to root_path
  end
end

