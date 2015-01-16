class Projects::TaskCommentsController < ApplicationController
  before_action :authenticate_user!
  before_action :check_user_authority
  before_action :prepare_task, only: [:create]
  before_action :prepare_task_comment, only: [:update, :destroy]

  def create
    task_comment = TaskComment.new(task_comment_params.merge(user: current_user, task: @task))

    unless task_comment.save
      flash[:alert] = t('message.empty_comment')
    end

    redirect_to :back
  end

  def update
    @task_comment.update(task_comment_params)
    redirect_to :back
  end

  def destroy
    if current_user == @task_comment.user
      @task_comment.destroy
    else
      flash[:alert] = t('message.unauthorized')
    end

    redirect_to :back
  end

  private

  def check_user_authority
    unless current_user.is_member_of? Project.where(id: params[:project_id], active: true).first
      flash[:alert] = t('message.unauthorized')
      redirect_to :back
    end
  end

  def prepare_task
    @task = Task.where(id: params[:task_id], active: true).first
  end

  def prepare_task_comment
    @task_comment = TaskComment.where(id: params[:id], active: true).first
  end

  def task_comment_params
    params.require(:task_comment).permit(:content)
  end
end
