#!/bin/sh

pid="/tmp/.brahma.pid"
script="python app.py"

function start(){
	if [ -f $pid ]; then
		echo "服务已经启动。"
		exit 1
	else
		echo "正在启动..."
		$script start >/dev/null  &
		echo $! > $pid
	fi
}

function stop(){
	if [ -f $pid ]; then
		echo "正在停止..."
		pgid=$(ps -o pgid `cat $pid` | grep [0-9])
		#kill -TERM -$pgid  2> /dev/null
		#echo $pgid
		kill -TERM -$pgid 
		rm $pid
	else
		echo "服务未启动。"
		exit 1
	fi
}

case "$1" in
	start)
		start
		;;
	stop)
		stop
		;;
	status)
		if [ -f $pid ]; then
			echo "正在运行"
		else
			echo "尚未启动"
		fi
		;;
	restart)
		start
		sleep 1
		stop
		;;
	debug)
		echo "调试模式启动..."
		$script debug
		;;
	*)
		echo "Usage: brahma.sh start|stop|restart|status|debug"
		;;
esac


