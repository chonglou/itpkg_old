class WikisController < ApplicationController
  before_action :authenticate_user!

  def index
    @buttons = [
        {label: t('links.wiki.create'), url: new_wiki_path, style: 'primary'},
    ]

    @wikis = Wiki.order(updated_at: :desc).page(params[:page])
  end

  def new
    @wiki = Wiki.new
  end

  private
  def _wiki_params
    params.require(:wiki).permit(:title, :body)
  end
end
