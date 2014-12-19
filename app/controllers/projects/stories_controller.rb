require 'itpkg/services/site'

class Projects::StoriesController < ApplicationController
  before_action :authenticate_user!
  before_action :check_user_authority
  before_action :prepare_project
  before_action :prepare_story, except: [:new, :create]

  def new
    @story = Story.new
  end

  def create
    @story = @project.stories.build(story_params)
    @story.requester_id = current_user.id

    @story.story_type_ids = prepare_story_type_ids(story_params)
    @story.story_tag_ids  = prepare_story_tag_ids(story_params)

    if @story.save
      Itpkg::LogService.teamwork current_user.label, @project.id, project_story_path(@story.id, project_id: @project.id), t('logs.project.story.create', title: @story.title), story_id:@story.id
      redirect_to project_story_path(@project, @story)
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
    params_for_update = story_params
    @story.story_type_ids = prepare_story_type_ids(params_for_update)
    @story.story_tag_ids  = prepare_story_tag_ids(params_for_update)

    if @story.update(params_for_update)
      redirect_to project_story_path(@project, @story)
    else
      render :edit
    end
  end

  def destroy
    if @story.requester_id == current_user.id
      @story.destroy
    else
      flash[:alert] = t('labels.in_using')
    end

    redirect_to project_path(@project)
  end

  private
  def prepare_project
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

  def prepare_story_type_ids(params)
    story_types     = params.delete(:story_type_ids).try(:split, ',') || []
    story_types_ids = []

    if story_types.any?
      story_types_ids = story_types.map.select { |st| st.to_i != 0 }
      new_types       = story_types.map.select { |st| st.to_i == 0 }

      story_types_ids += new_types.map { |nt| StoryType.create(name: nt, project: @project).id } if new_types.any?
    end

    story_types_ids
  end

  def prepare_story_tag_ids(params)
    story_tags    = params.delete(:story_tag_ids).try(:split, ',') || []
    story_tag_ids = []

    if story_tags.any?
      story_tag_ids = story_tags.map.select { |st| st.to_i != 0 }
      new_tags      = story_tags.map.select { |st| st.to_i == 0 }

      story_tag_ids += new_tags.map { |nt| StoryTag.create(name: nt, project: @project).id } if new_tags.any?
    end

    story_tag_ids
  end

  def story_params
    params.require(:story).permit(:title, :point, :status, :description, :story_type_ids, :story_tag_ids)
  end
end
