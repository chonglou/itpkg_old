require 'itpkg/utils/string_helper'

class ProjectsController < ApplicationController
  before_action :authenticate_user!

  def index
    @projects = Project.select(:id, :name, :details).where(creator_id: current_user.id).map { |p| {url: project_path(p.id), name: p.name, details: Itpkg::StringHelper.md2html(p.details)} }
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
      render :action => 'new'
    end

  end

  def show
    @project = Project.find params[:id]
    @buttons =  [
        {label: t('links.project.edit', name:@project.name), url: edit_project_path(params[:id]), style: 'primary'},
        {label: t('links.project.list'), url: projects_path, style: 'warning'},

    ]

    @items = [
        {
            url: document_show_path(name: 'help'),
            logo: 'flat/call37.png',
            label: t('links.about_us')
        },
        {
            url: document_show_path(name: 'help'),
            logo: 'flat/call37.png',
            label: t('links.about_us')
        },
        {
            url: document_show_path(name: 'help'),
            logo: 'flat/call37.png',
            label: t('links.about_us')
        },
        {
            url: document_show_path(name: 'help'),
            logo: 'flat/coins24.png',
            label: t('links.finance')
        },
        {
            url: document_show_path(name: 'help'),
            logo: 'flat/multiple25.png',
            label: t('links.contact')
        },
        {
            url: document_show_path(name: 'help'),
            logo: 'flat/call37.png',
            label: t('links.about_us')
        }
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
      render :action => 'edit'
    end
  end

  def destroy
    p = Project.find params[:id]
    if p.creator_id == current_user.id
      #todo 更多检查
      p.destroy
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
