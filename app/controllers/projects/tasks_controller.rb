class Projects::TasksController < ApplicationController
  before_action :prepare_project_and_story
  before_action :prepare_task, except: [:new, :create]
  before_action :check_user_authority

  def new
    @task = Task.new
  end

  def create
    @task = @story.tasks.build(task_params)

    if @task.save
      redirect_to project_story_task_path(@project, @story, @task)
    else
      render :new
    end
  end

  def show
    @buttons = [
      {label: t('links.task.edit'), url: edit_project_story_task_path(@project, @story, @task), style: 'primary'},
      {label: t('links.task.create'), url: new_project_story_task_path(@project, @story), style: 'warning'}
    ]

    @task_comments = @task.task_comments.where(active: true)
  end

  def edit
  end

  def update
    if @task.update(task_params)
      redirect_to project_story_path(@project, @story)
    end
  end

  def destroy
    if current_user.is_member_of? @project
      @task.destroy
    else
      flash[:alert] = t('labels.in_using')
    end

    redirect_to project_story_path(@project, @story)
  end

  private
  def prepare_project_and_story
    @project = Project.where(id: params[:project_id], active: true).first
    @story   = Story.where(id: params[:story_id], active: true).first
  end

  def prepare_task
    @task = Task.includes(:task_comments).where(id: params[:id], active: true).first
  end

  def check_user_authority
    unless current_user.is_member_of? @project
      flash[:alert] = t('message.unauthorized')
      redirect_to :back
    end
  end

  def task_params
    params.require(:task).permit(:point, :status, :priority, :details, :story_id)
  end
end
