require 'itpkg/utils/string_helper'
require 'itpkg/services/site'
require 'itpkg/services/history'

class ProjectsController < ApplicationController
  before_action :authenticate_user!
  before_action :prepare_project, only: [:edit, :update, :show, :destroy, :add_users]
  before_action :check_user_authority, except: [:index, :new, :create]

  def index
    @items  = Project.with_role(:member, current_user)
                 .where(active: true)
                 .map { |p| {id: p.id, details: p.details, name: p.name, logs: ProjectLog.where(project_id: p.id).order(created: :desc).limit(5)} }
    @buttons = [{label: t('buttons.create'), url: new_project_path, style: 'primary'}]
  end

  def new
    @project = Project.new
  end

  def create
    @project = Project.new(project_params)

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
    @project = Project.includes(:stories).where(id: params[:id], active: true).first

    if @project.present?
      # todo include?有性能问题
      @buttons = []
      @buttons << {label: t('links.project.edit', name: @project.name), url: edit_project_path(params[:id]), style: 'primary'}
      @buttons << {label: t('links.project.story.create'), url: new_project_story_path(@project), style: 'info'}
      @buttons << {label: t('links.project.add'), url: '', method: '', data: {toggle: 'modal', target: '#invite_modal'}, style: 'success'}
      @buttons << {label: t('links.project.list'), url: projects_path, style: 'warning'}

      #todo 分页显示
      @logs = ProjectLog.where(project_id: @project.id).order(created: :desc).limit(5)
      @users = User.all.reject { |u| u == current_user }
    else
      flash[:alert] = t('message.project_not_exists')
      redirect_to :back
    end
  end

  def edit
  end

  def update
    bak = {name: @project.name, details: @project.details}
    if @project.update(project_params)
      Itpkg::LogService.teamwork current_user.label, @project.id, project_path(@project), t('logs.project.edit', name: @project.name)
      Itpkg::HistoryService.backup :project, @project.id, bak

      redirect_to project_path(@project.id)
    else
      render :edit
    end
  end

  def destroy
    if User.with_role(:member, @project).size == 1 && @project.stories.select(&:active?).empty?
      Itpkg::LogService.teamwork current_user.label, @project.id, nil, t('logs.project.remove')
      Itpkg::HistoryService.backup :project, @project.id, {name: @project.name, details: @project.details}
      @project.destroy
    else
      flash[:alert] = t('labels.in_using')
      redirect_to :back and return
    end

    redirect_to projects_path
  end

  def add_users
    params[:project_members].split(',').each do |user_id|
      User.find(user_id).add_role :member, @project
    end

    params[:none_project_members].split(',').each do |user_id|
      User.find(user_id).remove_role :member, @project
    end

    redirect_to project_path(@project)
  end

  def remove_user
    User.find(params[:user_id]).remove_role :member, @project

    respond_to do |format|
      format.json { render json: :success }
    end
  end

  private

  def project_params
    params.require(:project).permit(:name, :details)
  end

  def project_id
    params.permit(:id, :project_id)
  end

  def check_user_authority
    unless current_user.is_member_of?(@project)
      flash[:alert] = t('message.unauthorized')
      redirect_to :back
    end
  end

  def prepare_project
    @project = Project.find(project_id[:id] || project_id[:project_id])
  end
end
