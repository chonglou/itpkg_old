class ProjectsController < ApplicationController
  before_action :authenticate_user!

  def index
    @projects = Project.select(:id, :name, :details).where(creator_id:current_user.id)
  end

  def new
    @project = Project.new
  end

  def create
    @project = Project.new(project_params)
    @project.creator_id = current_user.id
    if @project.save
      flash[:notice] = t('labels.success')
      redirect_to projects_path
    else
      render :action=>'new'
    end

  end

  def show

  end

  def edit

  end

  def update

  end

  def destroy

  end

  private
  def project_params
    params.require(:project).permit(:name, :details)
  end
end
