echo newworld123.

Deploy2WebServerRest='123.57.223.47'

#redis-test1
Deploy2ComponentServerRedis='101.200.182.211'
#openresty-test1
Deploy2ComponentServerOpenresty='123.57.223.47'
#kafka-test1
Deploy2ComponentServerKafka='101.200.202.106'
#elasticsearch-test1
Deploy2ComponentServerElasticsearch='101.200.232.26'


echo "stop $Deploy2ComponentServerRedis input components"
ssh -p 22 root@$Deploy2ComponentServerRedis '/BHUData/appsh/stop.sh'

echo "restart $Deploy2ComponentServerRedis daemon components"
ssh -p 22 root@$Deploy2ComponentServerRedis '/BHUData/appsh/restart.sh'
sleep 5

echo "restart $Deploy2ComponentServerOpenresty daemon components"
ssh -p 22 root@$Deploy2ComponentServerOpenresty '/BHUData/appsh/restart.sh'
sleep 5

echo "restart $Deploy2ComponentServerKafka daemon components"
ssh -p 22 root@$Deploy2ComponentServerKafka '/BHUData/appsh/restart.sh'
sleep 5

echo "restart $Deploy2ComponentServerElasticsearch daemon components"
ssh -p 22 root@$Deploy2ComponentServerElasticsearch '/BHUData/appsh/restart.sh'
sleep 5

echo "restart $Deploy2ComponentServerRedis input components"
ssh -p 22 root@$Deploy2ComponentServerRedis'/BHUData/appsh/start.sh'

sleep 5
echo "restart $Deploy2WebServerRest web"
ssh -p 22 root@$Deploy2WebServerRest '/usr/local/tomcats/stop_tomcats.sh'
ssh -p 22 root@$Deploy2WebServerRest '/usr/local/tomcats/startup_tomcats.sh'

