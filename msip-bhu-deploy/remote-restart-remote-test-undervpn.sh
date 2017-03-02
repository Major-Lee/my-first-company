#!/bin/bash
echo newworld123.

Deploy2WebServerRest='10.51.66.17'

#mysql-test1
Deploy2DataImportMysql='10.44.203.67'
#redis-test1
Deploy2ComponentServerRedis='10.44.164.201'
#openresty-test1
Deploy2ComponentServerOpenresty='10.51.66.17'
#kafka-test1
Deploy2ComponentServerKafka='10.44.165.204'
#elasticsearch-test1
Deploy2ComponentServerElasticsearch='10.44.203.169'


echo "restart $Deploy2ComponentServerRedis daemon components"
ssh -p 65008 root@$Deploy2ComponentServerRedis '/BHUData/appsh/restart.sh'
sleep 5

echo "restart $Deploy2ComponentServerOpenresty daemon components"
ssh -p 65008 root@$Deploy2ComponentServerOpenresty '/BHUData/appsh/restart.sh'
sleep 5

echo "restart $Deploy2ComponentServerKafka daemon components"
ssh -p 65008 root@$Deploy2ComponentServerKafka '/BHUData/appsh/restart.sh'
sleep 5

echo "restart $Deploy2ComponentServerElasticsearch daemon components"
ssh -p 65008 root@$Deploy2ComponentServerElasticsearch '/BHUData/appsh/restart.sh'
sleep 5

sleep 5
echo "restart $Deploy2WebServerRest web"
ssh -p 65008 root@$Deploy2WebServerRest '/usr/local/tomcats/stop_tomcats.sh'
ssh -p 65008 root@$Deploy2WebServerRest '/usr/local/tomcats/startup_tomcats.sh'







