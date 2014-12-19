require 'itpkg/services/site'

class Projects::StoryTypesController < ApplicationController
  before_action :authenticate_user!
  before_action :prepare_project

  def index
    all_types = StoryType.all

    respond_to do |format|
      format.json { render json: all_types}
    end
  end

  private
  def prepare_project
    @project = Project.find params[:project_id]
  end
end
