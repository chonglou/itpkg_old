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
    if @story.requester_id == current_user.id
      @task.destroy
    else
      flash[:alert] = t('labels.in_using')
    end

    redirect_to project_story_path(@project, @story)
  end

  private
  def prepare_project_and_story
    @project = Project.find params[:project_id]
    @story   = Story.find params[:story_id]
  end

  def prepare_task
    @task = Task.find params[:id]
  end

  def check_user_authority
    unless current_user.is_member_of? Project.find(params[:project_id])
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
