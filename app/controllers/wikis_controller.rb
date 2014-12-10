class WikisController < ApplicationController
  before_action :authenticate_user!
  before_action :_check_author, only:[:edit, :update, :destroy]

  def index
    @buttons = [
        {label: t('links.wiki.create'), url: new_wiki_path, style: 'primary'},
    ]

    @wikis = Wiki.order(updated_at: :desc).page(params[:page])
  end

  def show
    @wiki = Wiki.find params[:id]
  end

  def new
    @wiki = Wiki.new
  end

  def create
    @wiki = Wiki.new _wiki_params
    if @wiki.save
      current_user.add_role :author, @wiki
      redirect_to wiki_path(@wiki)
    else
      render 'new'
    end
  end


  def update
    @wiki = Wiki.find params[:id]
    if @wiki.update(_wiki_params)
      redirect_to wiki_path(@wiki)
    else
      render 'edit'
    end
  end

  def destroy
    @wiki.destroy
    redirect_to wikis_path
  end

  private
  def _wiki_params
    params.require(:wiki).permit(:title, :body)
  end

  def _check_author

    @wiki = Wiki.find(params[:id])
    unless current_user.is_author_of?(@wiki)
      render status: 404
    end
  end
end
