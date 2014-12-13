class Projects::StoryCommentsController < ApplicationController
  before_action :authenticate_user!
  before_action :check_user_authority
  before_action :prepare_story

  def create
    story_comment = StoryComment.new(story_comment_params.merge(user: current_user))

    unless story_comment.save
      flash[:alert] = t('message.empty_comment')
      redirect_back and return
    end

    @story.story_comments << story_comment
    redirect_back
  end

  def update
    @story_comment.update(story_comment_params)
    redirect_back
  end

  def destroy
    if current_user.id == @story_comment.user_id
      @story_comment.destroy
    else
      flash[:alert] = t('message.unauthorized')
      redirect_to project_story_path(params[:project_id], params[:story_id])
    end

    redirect_back
  end

  private

  def check_user_authority
    if current_user.has_role? :member, Project.find(params[:project_id])
      @story_comment = StoryComment.find(params[:id]) if params[:id].present?
    else
      flash[:alert] = t('message.unauthorized')
    end
  end

  def prepare_story
    @story = Story.find params[:story_id]
  end

  def story_comment_params
    params.require(:story_comment).permit(:content)
  end

  def redirect_back
    redirect_to project_story_path(params[:project_id], @story)
  end
end
