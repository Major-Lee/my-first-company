#!/bin/sh

pgm=$1
lock_file=.lock_$1

if [ -f ${lock_file} ]
then
    kill `cat ${lock_file}`
    if [ $? -eq 0 ]
    then
        echo "Stopped `cat ${lock_file}`"
        rm ${lock_file}
        exit 0
    fi
fi