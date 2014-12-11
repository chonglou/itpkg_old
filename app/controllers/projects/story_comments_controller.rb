class Projects::StoryCommentsController < ApplicationController
  before_action :authenticate_user!
  before_action :check_user_authorization, except: [:create]
  before_action :prepare_story
  after_action  :redirect_back

  def create
    @story.story_comments.create(story_comment_params.merge(user: current_user))
  end

  def update
    @story_comment.update(story_comment_params)
  end

  def destroy
    @story_comment.destroy
  end

  private
  def check_user_authorization
    @story_comment = StoryComment.find params[:id]
    unless current_user.id == @story_comment.user_id
      flash[:alert] = t('message.unauthorized')
      redirect_to project_story_path(params[:project_id], params[:story_id])
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
