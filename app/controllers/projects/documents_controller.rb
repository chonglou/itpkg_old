class Projects::DocumentsController < ApplicationController
  before_action :authenticate_user!
  before_action :_can_project?

  def index
    @documents = Document.select(:id, :title, :ext, :size).where('project_id = ? AND status = ?', @project.id, Document.statuses[:project]).map { |d| {cols:[d.title, d.ext, d.size], url:project_document_path(d.id,project_id:@project.id)} }
    @buttons=[]
  end

  def show

  end

  def new

  end

  def create

  end

  def edit

  end

  def update

  end

  def destroy

  end

  private
  def _can_edit?
    @document.creator_id == current_user.id
  end

  def _can_view?
    #todo
    true
  end

  def _can_project?
    #todo
    @project = Project.find params[:project_id]
  end
end
