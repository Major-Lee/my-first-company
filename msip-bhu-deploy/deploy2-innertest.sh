
cd `dirname $0`
Current_DIR=`pwd`
echo $Current_DIR

#Deploy2Server=$1
Deploy2ServerBrook=192.168.66.191
Deploy2ServerSanji=192.168.66.147
Deploy2ServerFranky=192.168.66.123
#Deploy2ServerWeb=192.168.66.124

read -n1 -p "Do you want to deploy 2 $Deploy2Server(Local InnterTest) [Y/N]?"
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
#echo "starting"


#echo "starting deploy2=>"$Deploy2ServerWeb
#sleep 5

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
echo '拷贝文件 msip_bhu_unit_daemon_processor-bin.zip到'$CuDateDir
cp ../../msip-bhu-unit/msip_bhu_unit_daemon_processor/target/msip_bhu_unit_daemon_processor-bin.zip ./$CuDateDir

#echo '拷贝文件 msip_bhu_unit_devices-bin.zip到'$CuDateDir
#cp ../../msip-bhu-unit/msip_bhu_unit_devices/target/msip_bhu_unit_devices-bin.zip ./$CuDateDir

#echo '拷贝文件 msip_bhu_unit_vas-bin.zip到'$CuDateDir
#cp ../../msip-bhu-unit/msip_bhu_unit_vas/target/msip_bhu_unit_vas-bin.zip ./$CuDateDir
#echo '拷贝文件 msip_bhu_unit_rest_urouter-bin.zip到'$CuDateDir
#cp ../../msip-bhu-unit/msip_bhu_unit_rest_urouter/target/msip_bhu_unit_rest_urouter-bin.zip ./$CuDateDir

#echo '拷贝文件 msip_bhu_unit_uass-bin.zip到'$CuDateDir
#cp ../../msip-bhu-unit/msip_bhu_unit_uass/target/msip_bhu_unit_uass-bin.zip ./$CuDateDir

#echo '拷贝文件 msip_bhu_unit_agent-bin.zip到'$CuDateDir
#cp ../../msip-bhu-unit/msip_bhu_unit_agent/target/msip_bhu_unit_agent-bin.zip ./$CuDateDir


#echo '拷贝文件 msip_bhu_backend_online-bin.zip到'$CuDateDir
#cp ../../msip-bhu-business-impl/msip_bhu_backend_online/target/msip_bhu_backend_online-bin.zip ./$CuDateDir
#echo '拷贝文件 msip_bhu_backend_task-bin.zip到'$CuDateDir
#cp ../../msip-bhu-business-impl/msip_bhu_backend_task/target/msip_bhu_backend_task-bin.zip ./$CuDateDir
#echo '拷贝文件 msip_bhu_backend_wifistasniffer-bin.zip到'$CuDateDir
#cp ../../msip-bhu-business-impl/msip_bhu_backend_wifistasniffer/target/msip_bhu_backend_wifistasniffer-bin.zip ./$CuDateDir

#echo '拷贝文件 msip_bhu_backend_modulestat-bin.zip到'$CuDateDir#
#cp ../../msip-bhu-business-impl/msip_bhu_backend_modulestat/target/msip_bhu_backend_modulestat-bin.zip ./$CuDateDir


#echo '拷贝文件 msip_bhu_dataimport-bin.zip到'$CuDateDir
#cp ../../msip-bhu-business-impl/msip_bhu_dataimport/target/msip_bhu_dataimport-bin.zip ./$CuDateDir
#echo '拷贝文件 msip_bhu_rest.war到'$CuDateDir
#cp ../../msip-bhu-api/msip_bhu_rest/target/msip_bhu_rest.war ./$CuDateDir
#echo '拷贝文件 msip_bhu_agent_rest.war到'$CuDateDir
#cp ../../msip-bhu-api/msip_bhu_agent_rest/target/msip_bhu_agent_rest.war ./$CuDateDir

#commdity 
#echo '拷贝文件 msip_bhu_unit_commdity-bin.zip到'$CuDateDir
#cp ../../msip-bhu-unit/msip_bhu_unit_commdity/target/msip_bhu_unit_commdity-bin.zip ./$CuDateDir

#echo '拷贝文件 msip_bhu_backend_commdity-bin.zip到'$CuDateDir
#cp ../../msip-bhu-business-impl/msip_bhu_backend_commdity/target/msip_bhu_backend_commdity-bin.zip ./$CuDateDir

#echo '拷贝文件 msip_bhu_backend_task_applies_notify-bin.zip到'$CuDateDir
#cp ../../msip-bhu-business-impl/msip_bhu_backend_task_applies_notify/target/msip_bhu_backend_task_applies_notify-bin.zip ./$CuDateDir

#echo '拷贝文件 msip_bhu_commdity_rest.war到'$CuDateDir
#cp ../../msip-bhu-api/msip_bhu_commdity_rest/target/msip_bhu_commdity_rest.war ./$CuDateDir



cd $CuDateDir
echo '进行文件解压过程'
unzip -q msip_bhu_unit_input_processor-bin.zip
unzip -qo msip_bhu_unit_input_processor/bin/msip_bhu_unit_input_processor.jar -d msip_bhu_unit_input_processor/classes/
unzip -q msip_bhu_unit_daemon_processor-bin.zip
unzip -qo msip_bhu_unit_daemon_processor/bin/msip_bhu_unit_daemon_processor.jar -d msip_bhu_unit_daemon_processor/classes/
#unzip -q msip_bhu_unit_devices-bin.zip
#unzip -qo msip_bhu_unit_devices/bin/msip_bhu_unit_devices.jar -d msip_bhu_unit_devices/classes/

#unzip -q msip_bhu_unit_vas-bin.zip
#unzip -qo msip_bhu_unit_vas/bin/msip_bhu_unit_vas.jar -d msip_bhu_unit_vas/classes/
#unzip -q msip_bhu_unit_uass-bin.zip
#unzip -qo msip_bhu_unit_uass/bin/msip_bhu_unit_uass.jar -d msip_bhu_unit_uass/classes/

#unzip -q msip_bhu_unit_rest_urouter-bin.zip
#unzip -qo msip_bhu_unit_rest_urouter/bin/msip_bhu_unit_rest_urouter.jar -d msip_bhu_unit_rest_urouter/classes/


#unzip -q msip_bhu_unit_agent-bin.zip
#unzip -qo msip_bhu_unit_agent/bin/msip_bhu_unit_agent.jar -d msip_bhu_unit_agent/classes/

#unzip -q msip_bhu_backend_online-bin.zip
#unzip -qo msip_bhu_backend_online/bin/msip_bhu_backend_online.jar -d msip_bhu_backend_online/classes/

#unzip -q msip_bhu_backend_task-bin.zip
#unzip -qo msip_bhu_backend_task/bin/msip_bhu_backend_task.jar -d msip_bhu_backend_task/classes/

#unzip -q msip_bhu_backend_wifistasniffer-bin.zip
#unzip -qo msip_bhu_backend_wifistasniffer/bin/msip_bhu_backend_wifistasniffer.jar -d msip_bhu_backend_wifistasniffer/classes/

#unzip -q msip_bhu_backend_modulestat-bin.zip
#unzip -qo msip_bhu_backend_modulestat/bin/msip_bhu_backend_modulestat.jar -d msip_bhu_backend_modulestat/classes/


#unzip -q msip_bhu_dataimport-bin.zip
#unzip -qo msip_bhu_dataimport/bin/msip_bhu_dataimport.jar -d msip_bhu_dataimport/classes/

#unzip -qo msip_bhu_rest.war -d msip_bhu_rest
#unzip -qo msip_bhu_agent_rest.war -d msip_bhu_agent_rest

#commdity 
#unzip -q msip_bhu_unit_commdity-bin.zip
#unzip -qo msip_bhu_unit_commdity/bin/msip_bhu_unit_commdity.jar -d msip_bhu_unit_commdity/classes/

#unzip -q msip_bhu_backend_commdity-bin.zip
#unzip -qo msip_bhu_backend_commdity/bin/msip_bhu_backend_commdity.jar -d msip_bhu_backend_commdity/classes/

#unzip -q msip_bhu_backend_task_applies_notify-bin.zip
#unzip -qo msip_bhu_backend_task_applies_notify/bin/msip_bhu_backend_task_applies_notify.jar -d msip_bhu_backend_task_applies_notify/classes/

#unzip -qo msip_bhu_commdity_rest.war -d msip_bhu_commdity_rest

echo '文件解压过程成功'

echo '准备发布业务组件到'$Deploy2Server

#echo 'deploy msip_bhu_unit_input_processor to ...@'$Deploy2Server
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_input_processor/lib/spring*.RELEASE.jar  	root@$Deploy2Server:/BHUData/apps/msip_bhu_unit_input_processor/libs/
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_input_processor/lib/msip_*.jar  	root@$Deploy2Server:/BHUData/apps/msip_bhu_unit_input_processor/libs/
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_input_processor/classes/com/ 		root@$Deploy2Server:/BHUData/apps/msip_bhu_unit_input_processor/classes/com/
#echo 'deploy msip_bhu_unit_input_processor successfully @'$Deploy2Server

echo 'deploy msip_bhu_unit_input_processor to ...@'$Deploy2ServerFranky
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_input_processor/lib/spring*.RELEASE.jar   root@$Deploy2ServerFranky:/BHUData/apps/msip_bhu_unit_input_processor/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_input_processor/lib/msip_*.jar    root@$Deploy2ServerFranky:/BHUData/apps/msip_bhu_unit_input_processor/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_input_processor/classes/com/    root@$Deploy2ServerFranky:/BHUData/apps/msip_bhu_unit_input_processor/classes/com/
echo 'deploy msip_bhu_unit_input_processor successfully @'$Deploy2ServerFranky

echo 'deploy msip_bhu_unit_input_processor to ...@'$Deploy2ServerSanji
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_input_processor/lib/spring*.RELEASE.jar   root@$Deploy2ServerSanji:/BHUData/apps/msip_bhu_unit_input_processor/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_input_processor/lib/msip_*.jar    root@$Deploy2ServerSanji:/BHUData/apps/msip_bhu_unit_input_processor/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_input_processor/classes/com/    root@$Deploy2ServerSanji:/BHUData/apps/msip_bhu_unit_input_processor/classes/com/
echo 'deploy msip_bhu_unit_input_processor successfully @'$Deploy2ServerSanji

echo 'deploy msip_bhu_unit_daemon_processor to ...@'$Deploy2ServerBrook
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_daemon_processor/lib/spring*.RELEASE.jar	root@$Deploy2ServerBrook:/BHUData/apps/msip_bhu_unit_daemon_processor/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_daemon_processor/lib/msip_*.jar	root@$Deploy2ServerBrook:/BHUData/apps/msip_bhu_unit_daemon_processor/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_daemon_processor/classes/com/ 		root@$Deploy2ServerBrook:/BHUData/apps/msip_bhu_unit_daemon_processor/classes/com/
echo 'deploy msip_bhu_unit_daemon_processor successfully @'$Deploy2ServerBrook

echo '发布其他服务成功'




