class ProjectsController < ApplicationController
  before_action :authenticate_user!

  def index
    @icons = [
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

  def new
    @project = Project.new
  end

  def create

  end

  def show

  end

  def edit

  end

  def update

  end

  def destroy

  end
end
