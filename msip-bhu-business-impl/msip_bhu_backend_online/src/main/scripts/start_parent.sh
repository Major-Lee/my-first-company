#!/bin/sh

pgm=$1
lock_file=/VCData/run/.lock_$1
if [ "$2" = "stage" ] ; then
	JAVA_OPTS=" -server -Xms512M -Xmx512M -XX:PermSize=64M -XX:MaxNewSize=128m "
else
	JAVA_OPTS=" -server -Xms2g -Xmx2g -XX:PermSize=128M -XX:MaxNewSize=512m "
fi

java $JAVA_OPTS -jar ${1}.jar | cronolog /VCData/logs/backendonline/backendonline.out.%Y-%m-%d >> /dev/null &
PIDS=`ps -f | grep java | grep "$1" | awk '{print $2}'`
#pid=$!
echo $PIDS > ${lock_file}
echo "Started ${pgm}, pid is $lock_file"
