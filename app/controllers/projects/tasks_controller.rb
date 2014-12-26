class Projects::TasksController < ApplicationController
  before_action :prepare_project_and_story
  before_action :prepare_task, except: [:new, :create]
  before_action :check_user_authority

  def new
  end

  def create
    task = @story.tasks.build(task_params)

    if task.save
      respond_to do |format|
        format.json { render json: task }
      end
    end
  end

  def edit
    render 'edit', layout:nil
  end

  def update
    if @task.update(task_params_for_update)
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
    @task = Task.where(id: params[:id], active: true).first
  end

  def check_user_authority
    unless current_user.is_member_of? @project
      flash[:alert] = t('message.unauthorized')
      redirect_to :back
    end
  end

  def task_params
    params.permit(:details, :story_id)
  end

  def task_params_for_update
    params.require(:task).permit(:details, :story_id)
  end
end
