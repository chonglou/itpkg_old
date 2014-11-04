require 'itpkg/utils/string_helper'

class ProjectsController < ApplicationController
  before_action :authenticate_user!

  def index
    @projects = Project.select(:id, :name, :details).where(creator_id:current_user.id).map{|p| {url:project_path(p.id), name:p.name, details: Itpkg::StringHelper.md2html(p.details)}}
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
    @project = Project.find_by params[:id]
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
