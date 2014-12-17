class MonitorNodesController < ApplicationController
  before_action :must_admin!

  def index
    @buttons = [
        {label: t('links.monitor_node.create'), url: new_monitor_node_path, style: 'primary'},
        {label: t('links.monitor_node.list'), url: monitor_nodes_path, style: 'warning'},

    ]
    @nodes = MonitorNode.select(:id, :name, :vip, :flag, :status, :next_run).all.map do |n|
      {
          cols: [n.name, n.vip, n.flag, n.status, n.next_run],
          url: show_monitor_node_path(n.id)
      }
    end

  end

  def show
    #todo
  end

  def new
    @node = MonitorNode.new
  end

  def create
    if @node.create(_node_params)
      redirect_to monitor_nodes_path
    else
      render 'new'
    end
  end

  def edit
    @node = MonitorNode.find params[:id]
  end

  def update
    @node = MonitorNode.find params[:id]
    if @node.update(_node_params)
      redirect_to monitor_nodes_path
    else
      render 'edit'
    end
  end

  def destroy
    MonitorNode.destroy params[:id]
    redirect_to monitor_nodes_path
  end

  private
  def _node_params

  end

end
