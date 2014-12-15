class LoggingNodesController < ApplicationController
  before_action :must_ops!

  def index
    @buttons = [
        {label: t('links.logging_node.create'), url: new_logging_node_path, style: 'primary'},

    ]

    @nodes = LoggingNode.order(id: :desc).page(params[:page])
    @items = @nodes.map do |n|
      {
          cols: [n.name, n.vip, n.created_at],
          url: logging_node_path(n.id)
      }
    end


  end

  def show
    @node = LoggingNode.find params[:id]
    @buttons = [
        {label: t('links.logging_node.edit', name:@node.name), url: edit_logging_node_path(@node), style: 'primary'},
    ]
  end

  def new
    @cfg = <<EOF
#!/bin/sh
LC_ALL=en_US.utf8
since=$(date +"%Y-%m-%d %H:%M:%S")
echo since >> .history
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
    @node.update params.require(:logging_node).permit(:name)
    redirect_to logging_nodes_path
  end

  private
end
