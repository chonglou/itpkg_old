#!/bin/sh

root_dir=$(cd "$(dirname "$0")"; pwd)
args=" -server -cp ${root_dir}/etc:${root_dir}/lib:${root_dir}/itpkgd.jar -pidfile ${root_dir}/var/.itpkgd -DXms512m -DXmx1024m com.odong.itpkg.App "

start(){
	if [ -s "$pid" ]; then
		echo "已经启动，请先停止。"
	else
		echo "正在启动..."
		${root_dir}/bin/jsvc  -outfile /dev/null -errfile /dev/null -user root ${args}
	fi
}
stop(){
	if [ -s "$pid" ]; then
		echo "正在停止..."
		${root_dir}/bin/jsvc -user root  -stop ${args}
	else
		echo "尚未启动，请先启动。"
	fi
}

case $1 in
	start)
		start
		;;
	stop)
		stop 
		;;
	debug)
		if [ -s "$pid" ]; then
			echo "已经启动"
		else
			echo "正在调试启动..."
			${root_dir}/bin/jsvc -outfile ${root_dir}/var/stdout -errfile ${root_dir}/var/stderr -debug ${args}
		fi
		;;
	restart)
		stop
		sleep 2
		start
		;;
	*)
		echo "Usage: $0 {start|stop|debug|restart}" 
		;;
esac





