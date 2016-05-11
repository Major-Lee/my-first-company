#!/bin/sh

pgm=$1
lock_file=/VCData/run/.lock_$1

if [ -f ${lock_file} ]
then
    kill -15 `cat ${lock_file}`
    sleep 3
    if [ $? -eq 0 ]
    then
        echo "Stopped `cat ${lock_file}`"
        rm ${lock_file}
        exit 0
    fi
fi