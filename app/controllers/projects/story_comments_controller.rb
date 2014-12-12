class Projects::StoryCommentsController < ApplicationController
  before_action :authenticate_user!
  before_action :check_user_authority
  before_action :prepare_story
  after_action  :redirect_back

  def create
    @story.story_comments.create(story_comment_params.merge(user: current_user))
  end

  def update
    @story_comment.update(story_comment_params)
  end

  def destroy
    if current_user.id == @story_comment.user_id
      @story_comment.destroy
    else
      flash[:alert] = t('message.unauthorized')
      redirect_to project_story_path(params[:project_id], params[:story_id])
    end
  end

  private

  def check_user_authority
    if current_user.has_role? :member, Project.find(params[:project_id])
      @story_comment = StoryComment.find params[:id]
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
