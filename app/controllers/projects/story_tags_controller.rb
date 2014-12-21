require 'itpkg/services/site'

class Projects::StoryTagsController < ApplicationController
  before_action :authenticate_user!
  before_action :prepare_project

  def index
    all_tags = StoryTag.where(project: @project)

    respond_to do |format|
      format.json { render json: all_tags}
    end
  end

  private
  def prepare_project
    @project = Project.find params[:project_id]
  end
end
