#!/bin/sh

pgm=$1
lock_file=.lock_$1

nohup java -jar ${1}.jar &
pid=$!
echo $pid > ./${lock_file}
echo "Started ${pgm}, pid is $lock_file"
