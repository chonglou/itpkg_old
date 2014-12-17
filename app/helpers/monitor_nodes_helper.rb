module MonitorNodesHelper
  def monitor_node_flag_options
    [
        ['ping', MonitorNode.flags[:ping]],
        ['snmp', MonitorNode.flags[:snmp]],
    ]
  end
end