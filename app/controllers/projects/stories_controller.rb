require 'itpkg/services/site'

class Projects::StoriesController < ApplicationController
  before_action :authenticate_user!
  before_action :check_user_authority
  before_action :prepare_story, except: [:new, :create, :index]

  def index
    @buttons = []
    @stories = []
    @story_type = params.delete(:type_name)
    @story_tag = params.delete(:tag_name)

    if @story_type
      @stories = StoryType.find_by_name(@story_type).stories
    elsif @story_tag
      @stories = StoryTag.find_by_name(@story_tag).stories
    end
  end

  def new
    @story = Story.new
  end

  def create
    params_for_create = story_params
    story_type_ids    = params_for_create.delete(:story_type_ids)
    story_tag_ids     = params_for_create.delete(:story_tag_ids)

    @story = @project.stories.build(params_for_create)
    current_user.add_role :creator, @story
    @story.story_type_ids = prepare_story_type_ids(story_type_ids.try(:split, ',') || [])
    @story.story_tag_ids  = prepare_story_tag_ids(story_tag_ids.try(:split, ',') || [])

    if @story.save
      Itpkg::LogService.teamwork current_user.label,
                                 @project.id,
                                 project_story_path(@story.id,
                                                    project_id: @project.id),
                                 t('logs.project.story.create', title: @story.title),
                                 story_id:@story.id
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

    @story_comments = @story.story_comments.select(&:active?)
  end

  def edit
  end

  def update
    params_for_update     = story_params
    @story.story_type_ids = prepare_story_type_ids(params_for_update.delete(:story_type_ids).try(:split, ',') || [])
    @story.story_tag_ids  = prepare_story_tag_ids(params_for_update.delete(:story_tag_ids).try(:split, ',') || [])

    if @story.update(params_for_update)
      redirect_to project_story_path(@project, @story)
    else
      render :edit
    end
  end

  def destroy
    if current_user.is_member_of? @project
      @story.destroy
    else
      flash[:alert] = t('labels.in_using')
    end

    redirect_to project_path(@project)
  end

  def update_status
    status           = params[:status]
    real_start_time  = @story.real_start_time
    real_finish_time = @story.real_finish_time

    case params[:status]
      when 'processing'
        text            = t('logs.project.story.start', title: @story.title)
        real_start_time ||= Time.zone.now
      when 'done'
        text             = t('logs.project.story.finish', title: @story.title)
        real_finish_time = Time.zone.now
      else
        text = nil
    end

    @story.update(status: status, real_start_time: real_start_time, real_finish_time: real_finish_time)

    if text.present?
      Itpkg::LogService.teamwork current_user.label, @project.id, project_story_path(@story.id, project_id: @project.id),
                                 text, story_id: @story.id
    end

    redirect_to :back
  end

  private
  def prepare_story
    @story = Story.where(id: params[:id], active: true).first
  end

  def check_user_authority
    @project = Project.where(id: params[:project_id], active: true).first

    unless current_user.is_member_of? @project
      flash[:alert] = t('message.unauthorized')
      redirect_to :back
    end
  end

  def prepare_story_type_ids(story_types)
    story_types_ids = []

    story_types.each do |st_id|
      st = StoryType.where(id: st_id)

      story_types_ids << (st.any? ? st.first.id : StoryType.create(name: st_id, project: @project).id)
    end

    story_types_ids
  end

  def prepare_story_tag_ids(story_tags)
    story_tag_ids = []

    story_tags.each do |st_id|
      st = StoryTag.where(id: st_id)

      story_tag_ids << (st.any? ? st.first.id : StoryTag.create(name: st_id, project: @project).id)
    end

    story_tag_ids
  end

  def story_params
    params.require(:story).permit(:title, :point, :status, :description, :story_type_ids, :story_tag_ids,
                                  :plan_start_time, :plan_finish_time)
  end
end
