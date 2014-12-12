require 'itpkg/services/site'

class Projects::StoriesController < ApplicationController
  before_action :authenticate_user!
  before_action :prepare_project
  before_action :prepare_story, except: [:new, :create]
  before_action :check_user_authority

  def new
    @story = Story.new
  end

  def create
    @story = @project.stories.build(story_params)
    @story.requester_id = current_user.id
    if @story.save
      Itpkg::LogService.teamwork current_user.label, @project.id, project_story_path(@story.id, project_id: @project.id), t('logs.project.story.create', title: @story.title), story_id:@story.id
      redirect_to project_path(@project.id)
    else
      render :new
    end
  end

  def show
    @buttons = [
        {label: t('links.story.edit', name: @story.title), url: edit_project_story_path(@story.project, @story), style: 'primary'},
        {label: t('links.story.list'), url: project_path(@story.project), style: 'warning'}
    ]
  end

  def edit
  end

  def update
    if @story.update(story_params)
      redirect_to project_story_path(@project, @story)
    else
      render :edit
    end
  end

  def destroy
    if @story.requester_id == current_user.id
      @story.destroy
      project = Project.find params[:project_id]
    else
      flash[:alert] = t('labels.in_using')
    end

    redirect_to project_path(project)
  end

  private
  def prepare_project
    # todo 判断权限
    @project = Project.find params[:project_id]
  end

  def prepare_story
    @story = Story.find params[:id]
  end

  def check_user_authority
    unless current_user.has_role? :member, Project.find(params[:project_id])
      flash[:alert] = t('message.unauthorized')
      redirect_to :back
    end
  end

  def story_params
    params.require(:story).permit(:title, :point, :status, :description)
  end
end
