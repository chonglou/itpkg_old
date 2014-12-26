class Projects::StoryCommentsController < ApplicationController
  before_action :authenticate_user!
  before_action :check_user_authority
  before_action :prepare_story, only: [:create]
  before_action :prepare_story_comment, only: [:update, :destroy]

  def create
    story_comment = StoryComment.new(story_comment_params.merge(user: current_user, story: @story))

    unless story_comment.save
      flash[:alert] = t('message.empty_comment')
    end

    redirect_to :back
  end

  def update
    @story_comment.update(story_comment_params)
    redirect_to :back
  end

  def destroy
    if current_user.id == @story_comment.user_id
      @story_comment.destroy
    else
      flash[:alert] = t('message.unauthorized')
      redirect_to project_story_path(params[:project_id], params[:story_id])
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

  def prepare_story
    @story = Story.where(id: params[:story_id], active: true).first
  end

  def prepare_story_comment
    @story_comment = StoryComment.where(id: params[:id], active: true).first
  end

  def story_comment_params
    params.require(:story_comment).permit(:content)
  end
end
