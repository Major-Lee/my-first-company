
cd `dirname $0`
Current_DIR=`pwd`
echo $Current_DIR


Deploy2WebServerRest='123.57.223.47'

#mysql-test1
Deploy2DataImportMysql='101.200.232.197'
#redis-test1
Deploy2ComponentServerRedis='101.200.182.211'
#openresty-test1
Deploy2ComponentServerOpenresty='123.57.223.47'
#kafka-test1
Deploy2ComponentServerKafka='101.200.202.106'
#elasticsearch-test1
Deploy2ComponentServerElasticsearch='101.200.232.26'

DeployUser=$1

read -n1 -p "Do you want to deploy 2 $Deploy2ComponentServerRedis(Remote Production) [Y/N]?"
case $REPLY in
	Y | y) echo
          echo "fine ,continue on .."
          ;;
	N | n) echo 
          echo "OK, goodbye..."
          exit
          ;;
    *) echo
    	echo "only accept Y,y,N,n"
    	exit
    	;;           
          #exit
esac

echo "starting deploy2=>"$Deploy2ComponentServerRedis
sleep 5
#Deploy2Server=$1
#回到msip-bhu-deploy目录进入deploy目录，并且创建每日的预发布文件存储目录
#cd msip-bhu-deploy
deploy=${Current_DIR}"/deploy"
echo $currentdeploy
if [ ! -d "$deploy" ]; then
  mkdir "$deploy"
fi
cd "$deploy"
CuDateDir=`date +%Y%m%d%H`

if [ ! -d $CuDateDir ]; then
	mkdir $CuDateDir
fi
echo '清除目录'$CuDateDir'下所有的文件'
rm -rf ./$CuDateDir/*
echo '清除目录'$CuDateDir'下所有的文件成功'
#拷贝生成的zip包到发布目录中。。。
echo '拷贝文件 msip_bhu_unit_input_processor-bin.zip到'$CuDateDir
cp ../../msip-bhu-unit/msip_bhu_unit_input_processor/target/msip_bhu_unit_input_processor-bin.zip ./$CuDateDir
echo '拷贝文件 msip_bhu_unit_input_terminal_processor-bin.zip到'$CuDateDir
cp ../../msip-bhu-unit/msip_bhu_unit_input_terminal_processor/target/msip_bhu_unit_input_terminal_processor-bin.zip ./$CuDateDir
echo '拷贝文件 msip_bhu_unit_daemon_processor-bin.zip到'$CuDateDir
cp ../../msip-bhu-unit/msip_bhu_unit_daemon_processor/target/msip_bhu_unit_daemon_processor-bin.zip ./$CuDateDir
echo '拷贝文件 msip_bhu_unit_devices-bin.zip到'$CuDateDir
cp ../../msip-bhu-unit/msip_bhu_unit_devices/target/msip_bhu_unit_devices-bin.zip ./$CuDateDir

echo '拷贝文件 msip_bhu_unit_rest_urouter-bin.zip到'$CuDateDir
cp ../../msip-bhu-unit/msip_bhu_unit_rest_urouter/target/msip_bhu_unit_rest_urouter-bin.zip ./$CuDateDir

echo '拷贝文件 msip_bhu_unit_vas-bin.zip到'$CuDateDir
cp ../../msip-bhu-unit/msip_bhu_unit_vas/target/msip_bhu_unit_vas-bin.zip ./$CuDateDir
echo '拷贝文件 msip_bhu_unit_uass-bin.zip到'$CuDateDir
cp ../../msip-bhu-unit/msip_bhu_unit_uass/target/msip_bhu_unit_uass-bin.zip ./$CuDateDir
#echo '拷贝文件 msip_bhu_unit_captchacode-bin.zip到'$CuDateDir
#cp ../../msip-bhu-unit/msip_bhu_unit_captchacode/target/msip_bhu_unit_captchacode-bin.zip ./$CuDateDir

echo '拷贝文件 msip_bhu_unit_agent-bin.zip到'$CuDateDir
cp ../../msip-bhu-unit/msip_bhu_unit_agent/target/msip_bhu_unit_agent-bin.zip ./$CuDateDir


#echo '拷贝文件 msip_bhu_spark_task-bin.zip到'$CuDateDir
#cp ../../msip-bhu-business-impl/msip_bhu_spark_task/target/msip_bhu_spark_task-bin.zip ./$CuDateDir
echo '拷贝文件 msip_bhu_backend_online-bin.zip到'$CuDateDir
cp ../../msip-bhu-business-impl/msip_bhu_backend_online/target/msip_bhu_backend_online-bin.zip ./$CuDateDir
echo '拷贝文件 msip_bhu_backend_async_online-bin.zip到'$CuDateDir
cp ../../msip-bhu-business-impl/msip_bhu_backend_async_online/target/msip_bhu_backend_async_online-bin.zip ./$CuDateDir

echo '拷贝文件 msip_bhu_backend_task-bin.zip到'$CuDateDir
cp ../../msip-bhu-business-impl/msip_bhu_backend_task/target/msip_bhu_backend_task-bin.zip ./$CuDateDir
#echo '拷贝文件 msip_bhu_backend_wifistasniffer-bin.zip到'$CuDateDir
#cp ../../msip-bhu-business-impl/msip_bhu_backend_wifistasniffer/target/msip_bhu_backend_wifistasniffer-bin.zip ./$CuDateDir
#echo '拷贝文件 msip_bhu_backend_modulestat-bin.zip到'$CuDateDir
#cp ../../msip-bhu-business-impl/msip_bhu_backend_modulestat/target/msip_bhu_backend_modulestat-bin.zip ./$CuDateDir
echo '拷贝文件 msip_bhu_dataimport-bin.zip到'$CuDateDir
cp ../../msip-bhu-business-impl/msip_bhu_dataimport/target/msip_bhu_dataimport-bin.zip ./$CuDateDir

echo '拷贝文件 msip_bhu_rest.war到'$CuDateDir
cp ../../msip-bhu-api/msip_bhu_rest/target/msip_bhu_rest.war ./$CuDateDir

#commdity 
echo '拷贝文件 msip_bhu_unit_commdity-bin.zip到'$CuDateDir
cp ../../msip-bhu-unit/msip_bhu_unit_commdity/target/msip_bhu_unit_commdity-bin.zip ./$CuDateDir

echo '拷贝文件 msip_bhu_backend_commdity-bin.zip到'$CuDateDir
cp ../../msip-bhu-business-impl/msip_bhu_backend_commdity/target/msip_bhu_backend_commdity-bin.zip ./$CuDateDir

echo '拷贝文件 msip_bhu_backend_task_applies_notify-bin.zip到'$CuDateDir
cp ../../msip-bhu-business-impl/msip_bhu_backend_task_applies_notify/target/msip_bhu_backend_task_applies_notify-bin.zip ./$CuDateDir

echo '拷贝文件 msip_bhu_commdity_rest.war到'$CuDateDir
cp ../../msip-bhu-api/msip_bhu_commdity_rest/target/msip_bhu_commdity_rest.war ./$CuDateDir

#tag
echo '拷贝文件 msip_bhu_unit_tag_bin.zip到'$CuDateDir
cp ../../msip-bhu-unit/msip_bhu_unit_tag/target/msip_bhu_unit_tag-bin.zip ./$CuDateDir

#statistics
echo '拷贝文件 msip_bhu_unit_unifyStatistics-bin.zip到'$CuDateDir
cp ../../msip-bhu-unit/msip_bhu_unit_unifyStatistics/target/msip_bhu_unit_unifyStatistics-bin.zip ./$CuDateDir

#advertise
echo '拷贝文件 msip_bhu_backend_task_advertise-bin.zip到'$CuDateDir
cp ../../msip-bhu-business-impl/msip_bhu_backend_task_advertise/target/msip_bhu_backend_task_advertise-bin.zip ./$CuDateDir

echo '拷贝文件 msip_bhu_unit_advertise-bin.zip到'$CuDateDir
cp ../../msip-bhu-unit/msip_bhu_unit_advertise/target/msip_bhu_unit_advertise-bin.zip ./$CuDateDir

cd $CuDateDir
echo '进行文件解压过程'
unzip -q msip_bhu_unit_input_processor-bin.zip
unzip -qo msip_bhu_unit_input_processor/bin/msip_bhu_unit_input_processor.jar -d msip_bhu_unit_input_processor/classes/

unzip -q msip_bhu_unit_input_terminal_processor-bin.zip
unzip -qo msip_bhu_unit_input_terminal_processor/bin/msip_bhu_unit_input_terminal_processor.jar -d msip_bhu_unit_input_terminal_processor/classes/

unzip -q msip_bhu_unit_daemon_processor-bin.zip
unzip -qo msip_bhu_unit_daemon_processor/bin/msip_bhu_unit_daemon_processor.jar -d msip_bhu_unit_daemon_processor/classes/
unzip -q msip_bhu_unit_devices-bin.zip
unzip -qo msip_bhu_unit_devices/bin/msip_bhu_unit_devices.jar -d msip_bhu_unit_devices/classes/

unzip -q msip_bhu_unit_rest_urouter-bin.zip
unzip -qo msip_bhu_unit_rest_urouter/bin/msip_bhu_unit_rest_urouter.jar -d msip_bhu_unit_rest_urouter/classes/
unzip -q msip_bhu_unit_uass-bin.zip
unzip -qo msip_bhu_unit_uass/bin/msip_bhu_unit_uass.jar -d msip_bhu_unit_uass/classes/

unzip -q msip_bhu_unit_vas-bin.zip
unzip -qo msip_bhu_unit_vas/bin/msip_bhu_unit_vas.jar -d msip_bhu_unit_vas/classes/

unzip -q msip_bhu_unit_agent-bin.zip
unzip -qo msip_bhu_unit_agent/bin/msip_bhu_unit_agent.jar -d msip_bhu_unit_agent/classes/

unzip -q msip_bhu_backend_online-bin.zip
unzip -qo msip_bhu_backend_online/bin/msip_bhu_backend_online.jar -d msip_bhu_backend_online/classes/
unzip -q msip_bhu_backend_async_online-bin.zip
unzip -qo msip_bhu_backend_async_online/bin/msip_bhu_backend_async_online.jar -d msip_bhu_backend_async_online/classes/

unzip -q msip_bhu_backend_task-bin.zip
unzip -qo msip_bhu_backend_task/bin/msip_bhu_backend_task.jar -d msip_bhu_backend_task/classes/

unzip -q msip_bhu_dataimport-bin.zip
unzip -qo msip_bhu_dataimport/bin/msip_bhu_dataimport.jar -d msip_bhu_dataimport/classes/

unzip -qo msip_bhu_rest.war -d msip_bhu_rest

#commdity 
unzip -q msip_bhu_unit_commdity-bin.zip
unzip -qo msip_bhu_unit_commdity/bin/msip_bhu_unit_commdity.jar -d msip_bhu_unit_commdity/classes/

unzip -q msip_bhu_backend_commdity-bin.zip
unzip -qo msip_bhu_backend_commdity/bin/msip_bhu_backend_commdity.jar -d msip_bhu_backend_commdity/classes/

unzip -q msip_bhu_backend_task_applies_notify-bin.zip
unzip -qo msip_bhu_backend_task_applies_notify/bin/msip_bhu_backend_task_applies_notify.jar -d msip_bhu_backend_task_applies_notify/classes/

unzip -qo msip_bhu_commdity_rest.war -d msip_bhu_commdity_rest

#tag
unzip -q msip_bhu_unit_tag-bin.zip
unzip -qo msip_bhu_unit_tag/bin/msip_bhu_unit_tag.jar -d msip_bhu_unit_tag/classes/

#statistics
unzip -q msip_bhu_unit_unifyStatistics-bin.zip
unzip -qo msip_bhu_unit_unifyStatistics/bin/msip_bhu_unit_unifyStatistics.jar -d msip_bhu_unit_unifyStatistics/classes/

#advertise
unzip -q msip_bhu_backend_task_advertise-bin.zip
unzip -qo msip_bhu_backend_task_advertise/bin/msip_bhu_backend_task_advertise.jar -d msip_bhu_backend_task_advertise/classes/

unzip -q msip_bhu_unit_advertise-bin.zip
unzip -qo msip_bhu_unit_advertise/bin/msip_bhu_unit_advertise.jar -d msip_bhu_unit_advertise/classes/

echo '文件解压过程成功'


echo '准备发布业务Input组件到'$Deploy2ComponentServerRedis

echo 'deploy msip_bhu_unit_input_processor to ...@'$Deploy2ComponentServerRedis
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_input_processor/lib/spring*.RELEASE.jar 	root@$Deploy2ComponentServerRedis:/BHUData/apps/msip_bhu_unit_input_processor/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_input_processor/lib/msip_*.jar  	root@$Deploy2ComponentServerRedis:/BHUData/apps/msip_bhu_unit_input_processor/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_input_processor/classes/com/ 	root@$Deploy2ComponentServerRedis:/BHUData/apps/msip_bhu_unit_input_processor/classes/com/
echo 'deploy msip_bhu_unit_input_processor successfully @'$Deploy2ComponentServerRedis

echo 'deploy msip_bhu_unit_input_terminal_processor to ...@'$Deploy2ComponentServerRedis
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_input_terminal_processor/lib/spring*.RELEASE.jar   root@$Deploy2ComponentServerRedis:/BHUData/apps/msip_bhu_unit_input_terminal_processor/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_input_terminal_processor/lib/msip_*.jar    root@$Deploy2ComponentServerRedis:/BHUData/apps/msip_bhu_unit_input_terminal_processor/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_input_terminal_processor/classes/com/    root@$Deploy2ComponentServerRedis:/BHUData/apps/msip_bhu_unit_input_terminal_processor/classes/com/
echo 'deploy msip_bhu_unit_input_terminal_processor successfully @'$Deploy2ComponentServerRedis


echo '准备发布业务组件到'$Deploy2ComponentServerOpenresty
echo 'deploy msip_bhu_unit_uass to ...@'$Deploy2ComponentServerOpenresty
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_uass/lib/spring*.RELEASE.jar  root@$Deploy2ComponentServerOpenresty:/BHUData/apps/msip_bhu_unit_uass/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_uass/lib/msip_*.jar           root@$Deploy2ComponentServerOpenresty:/BHUData/apps/msip_bhu_unit_uass/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_uass/classes/com/             root@$Deploy2ComponentServerOpenresty:/BHUData/apps/msip_bhu_unit_uass/classes/com/
echo 'deploy msip_bhu_unit_uass successfully @'$Deploy2ComponentServerOpenresty

echo 'deploy msip_bhu_unit_rest_urouter to ...@'$Deploy2ComponentServerOpenresty
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_rest_urouter/lib/spring*.RELEASE.jar  root@$Deploy2ComponentServerOpenresty:/BHUData/apps/msip_bhu_unit_rest_urouter/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_rest_urouter/lib/msip_*.jar           root@$Deploy2ComponentServerOpenresty:/BHUData/apps/msip_bhu_unit_rest_urouter/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_rest_urouter/classes/com/             root@$Deploy2ComponentServerOpenresty:/BHUData/apps/msip_bhu_unit_rest_urouter/classes/com/
echo 'deploy msip_bhu_unit_rest_urouter successfully @'$Deploy2ComponentServerOpenresty

echo 'deploy msip_bhu_unit_tag to ...@'$Deploy2ComponentServerOpenresty
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_tag/lib/spring*.RELEASE.jar  root@$Deploy2ComponentServerOpenresty:/BHUData/apps/msip_bhu_unit_tag/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_tag/lib/msip_*.jar           root@$Deploy2ComponentServerOpenresty:/BHUData/apps/msip_bhu_unit_tag/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_tag/classes/com/             root@$Deploy2ComponentServerOpenresty:/BHUData/apps/msip_bhu_unit_tag/classes/com/
echo 'deploy msip_bhu_unit_tag successfully @'$Deploy2ComponentServerOpenresty

echo 'deploy msip_bhu_backend_online to ...@'$Deploy2ComponentServerOpenresty
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_backend_online/lib/spring*.RELEASE.jar   root@$Deploy2ComponentServerOpenresty:/BHUData/apps/msip_bhu_backend_online/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_backend_online/lib/msip_*.jar    root@$Deploy2ComponentServerOpenresty:/BHUData/apps/msip_bhu_backend_online/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_backend_online/classes/com/    root@$Deploy2ComponentServerOpenresty:/BHUData/apps/msip_bhu_backend_online/bin/com/
echo 'deploy msip_bhu_backend_online successfully @'$Deploy2ComponentServerOpenresty

echo 'deploy msip_bhu_unit_unifyStatistics to ...@'$Deploy2ComponentServerOpenresty
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_unifyStatistics/lib/spring*.RELEASE.jar  root@$Deploy2ComponentServerOpenresty:/BHUData/apps/msip_bhu_unit_unifyStatistics/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_unifyStatistics/lib/msip_*.jar           root@$Deploy2ComponentServerOpenresty:/BHUData/apps/msip_bhu_unit_unifyStatistics/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_unifyStatistics/classes/com/             root@$Deploy2ComponentServerOpenresty:/BHUData/apps/msip_bhu_unit_unifyStatistics/classes/com/
echo 'deploy msip_bhu_unit_unifyStatistics successfully @'$Deploy2ComponentServerOpenresty


echo '发布业务组件成功'$Deploy2ComponentServerOpenresty

echo '准备发布其他服务到'$Deploy2ComponentServerKafka

echo 'deploy msip_bhu_backend_async_online to ...@'$Deploy2ComponentServerKafka
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_backend_async_online/lib/spring*.RELEASE.jar    root@$Deploy2ComponentServerKafka:/BHUData/apps/msip_bhu_backend_async_online/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_backend_async_online/lib/msip_*.jar   root@$Deploy2ComponentServerKafka:/BHUData/apps/msip_bhu_backend_async_online/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_backend_async_online/classes/com/   root@$Deploy2ComponentServerKafka:/BHUData/apps/msip_bhu_backend_async_online/bin/com/
echo 'deploy msip_bhu_backend_async_online successfully @'$Deploy2ComponentServerKafka

echo 'deploy msip_bhu_backend_task to ...@'$Deploy2ComponentServerKafka
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_backend_task/lib/spring*.RELEASE.jar   root@$Deploy2ComponentServerKafka:/BHUData/apps/msip_bhu_backend_task/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_backend_task/lib/msip_*.jar    root@$Deploy2ComponentServerKafka:/BHUData/apps/msip_bhu_backend_task/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_backend_task/classes/com/    root@$Deploy2ComponentServerKafka:/BHUData/apps/msip_bhu_backend_task/bin/com/
echo 'deploy msip_bhu_backend_task successfully @'$Deploy2ComponentServerKafka

echo 'deploy msip_bhu_backend_task_advertise to ...@'$Deploy2ComponentServerKafka
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_backend_task_advertise/lib/spring*.RELEASE.jar      root@$Deploy2ComponentServerKafka:/BHUData/apps/msip_bhu_backend_task_advertise/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_backend_task_advertise/lib/msip_*.jar     root@$Deploy2ComponentServerKafka:/BHUData/apps/msip_bhu_backend_task_advertise/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_backend_task_advertise/classes/com/     root@$Deploy2ComponentServerKafka:/BHUData/apps/msip_bhu_backend_task_advertise/bin/com/
echo 'deploy msip_bhu_backend_task_advertise successfully @'$Deploy2ComponentServerKafka

echo 'deploy msip_bhu_unit_advertise to ...@'$Deploy2ComponentServerKafka
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_advertise/lib/spring*.RELEASE.jar root@$Deploy2ComponentServerKafka:/BHUData/apps/msip_bhu_unit_advertise/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_advertise/lib/msip_*.jar  root@$Deploy2ComponentServerKafka:/BHUData/apps/msip_bhu_unit_advertise/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_advertise/classes/com/    root@$Deploy2ComponentServerKafka:/BHUData/apps/msip_bhu_unit_advertise/classes/com/
echo 'deploy msip_bhu_unit_advertise successfully @'$Deploy2ComponentServerKafka

echo '发布业务组件成功'$Deploy2ComponentServerKafka

echo '准备发布其他服务到'$Deploy2ComponentServerElasticsearch

echo 'deploy msip_bhu_unit_devices to ...@'$Deploy2ComponentServerElasticsearch
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_devices/lib/spring*.RELEASE.jar        root@$Deploy2ComponentServerElasticsearch:/BHUData/apps/msip_bhu_unit_devices/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_devices/lib/msip_*.jar     root@$Deploy2ComponentServerElasticsearch:/BHUData/apps/msip_bhu_unit_devices/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_devices/classes/com/     root@$Deploy2ComponentServerElasticsearch:/BHUData/apps/msip_bhu_unit_devices/classes/com/
echo 'deploy msip_bhu_unit_devices successfully @'$Deploy2ComponentServerElasticsearch

echo 'deploy msip_bhu_unit_daemon_processor to ...@'$Deploy2ComponentServerElasticsearch
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_daemon_processor/lib/spring*.RELEASE.jar  root@$Deploy2ComponentServerElasticsearch:/BHUData/apps/msip_bhu_unit_daemon_processor/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_daemon_processor/lib/msip_*.jar root@$Deploy2ComponentServerElasticsearch:/BHUData/apps/msip_bhu_unit_daemon_processor/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_daemon_processor/classes/com/   root@$Deploy2ComponentServerElasticsearch:/BHUData/apps/msip_bhu_unit_daemon_processor/classes/com/
echo 'deploy msip_bhu_unit_daemon_processor successfully @'$Deploy2ComponentServerElasticsearch

echo 'deploy msip_bhu_unit_vas to ...@'$Deploy2ComponentServerElasticsearch
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_vas/lib/spring*.RELEASE.jar     root@$Deploy2ComponentServerElasticsearch:/BHUData/apps/msip_bhu_unit_vas/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_vas/lib/msip_*.jar        root@$Deploy2ComponentServerElasticsearch:/BHUData/apps/msip_bhu_unit_vas/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_vas/classes/com/      root@$Deploy2ComponentServerElasticsearch:/BHUData/apps/msip_bhu_unit_vas/classes/com/
echo 'deploy msip_bhu_unit_vas successfully @'$Deploy2ComponentServerElasticsearch

echo 'deploy msip_bhu_unit_commdity to ...@'$Deploy2ComponentServerElasticsearch
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_commdity/lib/spring*.RELEASE.jar  root@$Deploy2ComponentServerElasticsearch:/BHUData/apps/msip_bhu_unit_commdity/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_commdity/lib/msip_*.jar           root@$Deploy2ComponentServerElasticsearch:/BHUData/apps/msip_bhu_unit_commdity/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_unit_commdity/classes/com/             root@$Deploy2ComponentServerElasticsearch:/BHUData/apps/msip_bhu_unit_commdity/classes/com/
echo 'deploy msip_bhu_unit_commdity successfully @'$Deploy2ComponentServerElasticsearch

echo 'deploy msip_bhu_backend_commdity to ...@'$Deploy2ComponentServerElasticsearch
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_backend_commdity/lib/spring*.RELEASE.jar    root@$Deploy2ComponentServerElasticsearch:/BHUData/apps/msip_bhu_backend_commdity/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_backend_commdity/lib/msip_*.jar   root@$Deploy2ComponentServerElasticsearch:/BHUData/apps/msip_bhu_backend_commdity/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_backend_commdity/classes/com/   root@$Deploy2ComponentServerElasticsearch:/BHUData/apps/msip_bhu_backend_commdity/bin/com/
echo 'deploy msip_bhu_backend_commdity successfully @'$Deploy2ComponentServerElasticsearch

echo 'deploy msip_bhu_backend_task_applies_notify to ...@'$Deploy2ComponentServerElasticsearch
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_backend_task_applies_notify/lib/spring*.RELEASE.jar    root@$Deploy2ComponentServerElasticsearch:/BHUData/apps/msip_bhu_backend_task_applies_notify/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_backend_task_applies_notify/lib/msip_*.jar   root@$Deploy2ComponentServerElasticsearch:/BHUData/apps/msip_bhu_backend_task_applies_notify/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_backend_task_applies_notify/classes/com/     root@$Deploy2ComponentServerElasticsearch:/BHUData/apps/msip_bhu_backend_task_applies_notify/bin/com/
echo 'deploy msip_bhu_backend_task_applies_notify successfully @'$Deploy2ComponentServerElasticsearch


echo '发布其他服务成功'$Deploy2ComponentServerElasticsearch

echo 'deploy msip_bhu_rest to ...@'$Deploy2WebServerRest
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_rest/WEB-INF/lib/spring*.RELEASE.jar   root@$Deploy2WebServerRest:/BHUData/apps/msip_bhu_rest/WEB-INF/lib/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_rest/WEB-INF/lib/msip_*.jar    root@$Deploy2WebServerRest:/BHUData/apps/msip_bhu_rest/WEB-INF/lib/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_rest/WEB-INF/classes/com/    root@$Deploy2WebServerRest:/BHUData/apps/msip_bhu_rest/WEB-INF/classes/com/
echo 'deploy msip_bhu_rest successfully @'$Deploy2WebServerRest

echo 'deploy msip_bhu_commdity_rest to ...@'$Deploy2WebServerRest
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_commdity_rest/WEB-INF/lib/spring*.RELEASE.jar    root@$Deploy2WebServerRest:/BHUData/apps/msip_bhu_commdity_rest/WEB-INF/lib/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_commdity_rest/WEB-INF/lib/msip_*.jar   root@$Deploy2WebServerRest:/BHUData/apps/msip_bhu_commdity_rest/WEB-INF/lib/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_commdity_rest/WEB-INF/classes/com/     root@$Deploy2WebServerRest:/BHUData/apps/msip_bhu_commdity_rest/WEB-INF/classes/com/
echo 'deploy msip_bhu_commdity_rest successfully @'$Deploy2WebServerRest


echo 'deploy msip_bhu_commdity_rest to ...@'$Deploy2DataImportMysql
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_dataimport/lib/spring*.RELEASE.jar    root@$Deploy2DataImportMysql:/BHUData/apps/msip_bhu_dataimport/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_dataimport/lib/msip_*.jar   root@$Deploy2DataImportMysql:/BHUData/apps/msip_bhu_dataimport/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_dataimport/classes/com/   root@$Deploy2DataImportMysql:/BHUData/apps/msip_bhu_dataimport/bin/com/
echo 'deploy msip_bhu_dataimport successfully @'$Deploy2DataImportMysql

echo '发布其他服务成功'

