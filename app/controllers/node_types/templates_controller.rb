class NodeTypes::TemplatesController < ApplicationController
  before_action :must_admin!
  include NodeTypesHelper
  before_action :get_node_type

  def new
    @template = NtTemplate.new
    @owners = _owners
    @modes = _modes
    render 'new', layout:'node_types/view'
  end

  def create
    @template = NtTemplate.new params.require(:nt_template).permit(:name, :owner, :mode, :body)
    @template.node_type_id = params[:node_type_id]
    @owners = _owners
    @modes = _modes
    if @template.save
      redirect_to node_type_path(params[:node_type_id])
    else
      render 'new', layout:'node_types/view'
    end
  end

  def edit
    @template = NtTemplate.find params[:id]
    @owners = _owners
    @modes = _modes
    render 'edit', layout:'node_types/view'
  end

  def update

    @template = NtTemplate.find params[:id]
    @owners = _owners
    @modes = _modes

    if @template.update(params.require(:nt_template).permit(:name, :mode, :owner, :body))
      redirect_to node_type_path(params[:node_type_id])
    else
      render 'edit', layout:'node_types/view'
    end
  end

  def destroy
    NtTemplate.destroy params[:id]
    redirect_to node_type_path(params[:node_type_id])
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
