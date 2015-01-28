class Logging::NodesController < ApplicationController
  layout 'tabbed'
  before_action :authenticate_user!
  before_action :must_admin!, only: [:edit, :update]
  include LoggingNodesHelper
  before_action :_nav_items

  def index
    @buttons = [
        {label: t('links.logging_node.create'), url: new_logging_node_path, style: 'primary'},

    ]

    if current_user.is_admin?
      @nodes = LoggingNode.order(id: :desc).page(params[:page])
      @items = @nodes.map do |n|
        {
            cols: [n.name, n.vip, n.flag, n.created_at],
            url: edit_logging_node_path(n.id)
        }
      end
    else
      @nodes =LoggingNode.with_role(:reader, current_user)
      @items = @nodes.map do |n|
        {
            cols: [n.name, n.vip, n.flag, n.created_at]
        }
      end
    end

  end

  def show
    @node = LoggingNode.find params[:id]
    @buttons = [
        {label: t('links.logging_node.edit', name: @node.name), url: edit_logging_node_path(@node), style: 'primary'},
    ]
  end

  def new
    @cfg = <<EOF
#!/bin/sh
LC_ALL=en_US.utf8
since=$(date +"%Y-%m-%d %H:%M:%S")
echo $since >> .history
NC="nc log.#{ENV['ITPKG_DOMAIN']} 10002"
if which journalctl >/dev/null
then
  journalctl --utc --since "$since" -f | $NC
else
  tail -n 0 -f /var/log/syslog | $NC
fi
EOF
  end

  def edit
    @node = LoggingNode.find params[:id]
  end

  def update
    @node = LoggingNode.find params[:id]
    kv = params.require(:logging_node).permit(:name, :flag).tap do |p|
      p[:flag]= p[:flag] ? p[:flag].to_sym : LoggingNode.flags[:disable]
    end
    @node.update kv
    redirect_to logging_nodes_path
  end

  private
  def _nav_items
    @nav_items = logging_nodes_nav_items
  end
end
