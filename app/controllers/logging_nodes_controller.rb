#使用数字证书
#LC_ALL=en_US.utf8 journalctl -n 0 --utc -f | nc log.localhost.localdomain 10002
#tail -n 10 -f /var/log/syslog | nc log.localhost.localdomain 10002
class LoggingNodesController < ApplicationController
  def index
  end

  private
  def _sh(type)
    <<EOF
#!/bin/sh
LC_ALL=en_US.utf8
since=$(date +"%Y-%m-%d %H:%M:%S")
echo since >> .log
#{type == :journald ? 'journalctl --utc --since "$since" -f' : 'tail -n 0 -f $1'} | nc log.#{ENV[ITPKG_DOMAIN]} 10002
EOF
  end
end
