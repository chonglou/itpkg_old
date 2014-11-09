class TemplatesController < ApplicationController
  before_action :must_admin!

  def index
    @buttons = [
        {label: t('links.template.create'), url: new_template_path, style: 'primary'},

    ]
    @templates = Template.select(:id, :flag, :name, :owner, :mode, :updated_at).map do |t|
      {
          cols: [t.flag, t.name, t.owner, t.mode, t.updated_at],
          url: template_path(t.id)
      }
    end

  end

  def new
    @template = Template.new
    @owners = _owners
    @modes = _modes
  end

  def create
    @template = Template.new params.require(:template).permit(:flag, :name, :owner, :mode, :body)
    @owners = _owners
    @modes = _modes
    if @template.save
      redirect_to template_path(@template.id)
    else
      render 'new'
    end
  end

  def edit
    @template = Template.find params[:id]
    @owners = _owners
    @modes = _modes
  end

  def update

    @template = Template.find params[:id]
    @owners = _owners
    @modes = _modes

    if @template.update(params.require(:template).permit(:name, :mode, :owner, :body))
      redirect_to template_path(@template.id)
    else
      render 'edit'
    end
  end

  def destroy
    Template.destroy params[:id]
    redirect_to templates_path
  end

  def show
    @template = Template.find params[:id]
    @buttons = [
        {label: t('buttons.edit'), url: edit_template_path(@template.id), style: 'primary'},
        {label: t('links.template.list'), url: templates_path, style: 'info'},

    ]

  end

  private
  def _owners
    [
        %w(deploy:deploy deploy:deploy),
        %w(nobody:nobody nobody:nodoby),
        %w(root:root root:root),
        %w(vmail:vmail vmail:vmail),
        %w(named:named named:named),
    ]
  end

  def _modes
    [
        %w(r--r----- 440),
        %w(r-xr-x--- 550),
        %w(r-wr----- 640),
        %w(rwxr-x--- 750),
        %w(r--r--r-- 444)
    ]
  end
end
