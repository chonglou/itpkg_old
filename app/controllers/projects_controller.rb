require 'itpkg/utils/string_helper'
require 'itpkg/services/site'
require 'itpkg/services/history'

class ProjectsController < ApplicationController
  before_action :authenticate_user!
  before_action :check_user_authority, expcept: [:index, :show]
  before_action :prepare_project, only: [:edit, :update, :destroy]

  def index
    @items = current_user.projects.map { |p| {id: p.id, details: p.details, name: p.name, logs: ProjectLog.where(project_id: p.id).order(created: :desc).limit(5)} }
    @buttons = []
    if current_user.is_admin?
      @buttons << {label: t('buttons.create'), url: new_project_path, style: 'primary'}
    end
  end

  def new
    @project = Project.new
  end

  def create
    @project = Project.new(project_params)
    @project.creator_id = current_user.id
    @project.users << current_user
    if @project.save
      current_user.add_role :creator, @project
      current_user.add_role :member, @project
      Itpkg::LogService.teamwork current_user.label, @project.id, project_path(@project.id), t('logs.project.create')
      redirect_to project_path(@project.id)
    else
      render :new
    end
  end

  def show

    @project = Project.includes(:stories).find(params[:id])
    # todo include?有性能问题
    if current_user.is_admin? || current_user.projects.include?(params[:id])
      @buttons = []
      if current_user.is_admin?
        @buttons << {label: t('links.project.edit', name: @project.name), url: edit_project_path(params[:id]), style: 'primary'}
      end
      @buttons << {label: t('links.project.story.create'), url: new_project_story_path(@project), style: 'info'}
      @buttons << {label: t('links.project.list'), url: projects_path, style: 'warning'}

      #todo 分页显示
      @logs = ProjectLog.where(project_id: @project.id).order(created: :desc).limit(5)
    else
      render status: 404
    end
  end

  def edit
  end

  def update
    bak = {name: @project.name, details: @project.details}
    if @project.update(project_params)
      Itpkg::LogService.teamwork current_user.label, @project.id, project_path(@project.id), t('logs.project.edit', name: @project.name)
      Itpkg::HistoryService.backup :project, @project.id, bak
      redirect_to project_path(@project.id)
    else
      render :edit
    end
  end

  def destroy
    if p.creator_id == current_user.id
      if p.users.count==1 && p.stories.empty?
        Itpkg::LogService.teamwork current_user.label, p.id, nil, t('logs.project.remove')
        Itpkg::HistoryService.backup :project, p.id, {name: p.name, details: p.details}
        p.destroy
      else
        flash[:alert] = t('labels.in_using')
      end
    end
    redirect_to projects_path
  end

  private

  def project_params
    params.require(:project).permit(:name, :details)
  end

  def check_user_authority
    unless current_user.is_admin?
      flash[:alert] = t('message.unauthorized')
      redirect_to :back
    end
  end

  def prepare_project
    @project = Project.find params[:id]
  end
end
