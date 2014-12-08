require 'itpkg/utils/string_helper'

class ProjectsController < ApplicationController
  before_action :authenticate_user!

  def index
    @projects = Project.where(creator_id: current_user.id)
  end

  def new
    @project = Project.new
  end

  def create
    @project = Project.new(project_params)
    @project.creator_id = current_user.id
    if @project.save
      redirect_to project_path(@project.id)
    else
      render :new
    end
  end

  def show
    @project = Project.includes(:stories).find(params[:id])
    @buttons = [
        {label: t('links.project.edit', name: @project.name), url: edit_project_path(params[:id]), style: 'primary'},
        {label: t('links.project.list'), url: projects_path, style: 'warning'}
    ]
  end

  def edit
    @project = Project.find params[:id]
  end

  def update
    @project = Project.find params[:id]
    if @project.update(project_params)
      redirect_to project_path(@project.id)
    else
      render :edit
    end
  end

  def destroy
    p = Project.find params[:id]
    if p.creator_id == current_user.id
      if ProjectUser.where(project_id: p.id).empty?
        p.destroy
      else
        flash[:alert] = t('labels.in_using')
      end
    end
    redirect_to projects_path
  end

  private
  def _check
    @project.creator
  end

  def project_params
    params.require(:project).permit(:name, :details)
  end
end
