
cd `dirname $0`
Current_DIR=`pwd`
echo $Current_DIR

#Deploy2Server=$1
Deploy2Server0=192.168.66.162
Deploy2Server1=192.168.66.188
Deploy2Server2=192.168.66.155
Deploy2ServerWeb=192.168.66.7
##if read -t 5 -p "Confirm to deploy 2 $Deploy2Server(Local Test):(yes/no)"
#then
#  echo "hello $REPLY, welcome to come back here"
#else
#  echo "sorry , you are too slow "
#  exit
#fi

read -n1 -p "Do you want to deploy 2 $Deploy2Server(Local Test) [Y/N]?"
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


echo "starting deploy2=>"$Deploy2Server0
sleep 5

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
echo '拷贝文件 msip_bhu_unit_devices-bin.zip到'$CuDateDir
cp ../../msip-bhu-unit/msip_bhu_unit_devices/target/msip_bhu_unit_devices-bin.zip ./$CuDateDir
echo '拷贝文件 msip_bhu_unit_vas-bin.zip到'$CuDateDir
cp ../../msip-bhu-unit/msip_bhu_unit_vas/target/msip_bhu_unit_vas-bin.zip ./$CuDateDir

echo '拷贝文件 msip_bhu_unit_rest_urouter-bin.zip到'$CuDateDir
cp ../../msip-bhu-unit/msip_bhu_unit_rest_urouter/target/msip_bhu_unit_rest_urouter-bin.zip ./$CuDateDir
echo '拷贝文件 msip_bhu_unit_uass-bin.zip到'$CuDateDir
cp ../../msip-bhu-unit/msip_bhu_unit_uass/target/msip_bhu_unit_uass-bin.zip ./$CuDateDir


#echo '拷贝文件 msip_bhu_unit_agent-bin.zip到'$CuDateDir
#cp ../../msip-bhu-unit/msip_bhu_unit_agent/target/msip_bhu_unit_agent-bin.zip ./$CuDateDir


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
#echo '拷贝文件 msip_bhu_spark_task-bin.zip到'$CuDateDir
#cp ../../msip-bhu-business-impl/msip_bhu_spark_task/target/msip_bhu_spark_task-bin.zip ./$CuDateDir
echo '拷贝文件 msip_bhu_rest.war到'$CuDateDir
cp ../../msip-bhu-api/msip_bhu_rest/target/msip_bhu_rest.war ./$CuDateDir
#echo '拷贝文件 msip_bhu_agent_rest.war到'$CuDateDir
#cp ../../msip-bhu-api/msip_bhu_agent_rest/target/msip_bhu_agent_rest.war ./$CuDateDir

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
echo '拷贝文件 msip_bhu_unit_tag-bin.zip到'$CuDateDir
cp ../../msip-bhu-unit/msip_bhu_unit_tag/target/msip_bhu_unit_tag-bin.zip ./$CuDateDir



cd $CuDateDir
echo '进行文件解压过程'
unzip -q msip_bhu_unit_input_processor-bin.zip
unzip -qo msip_bhu_unit_input_processor/bin/msip_bhu_unit_input_processor.jar -d msip_bhu_unit_input_processor/classes/
unzip -q msip_bhu_unit_daemon_processor-bin.zip
unzip -qo msip_bhu_unit_daemon_processor/bin/msip_bhu_unit_daemon_processor.jar -d msip_bhu_unit_daemon_processor/classes/
unzip -q msip_bhu_unit_devices-bin.zip
unzip -qo msip_bhu_unit_devices/bin/msip_bhu_unit_devices.jar -d msip_bhu_unit_devices/classes/
unzip -q msip_bhu_unit_vas-bin.zip
unzip -qo msip_bhu_unit_vas/bin/msip_bhu_unit_vas.jar -d msip_bhu_unit_vas/classes/

unzip -q msip_bhu_unit_rest_urouter-bin.zip
unzip -qo msip_bhu_unit_rest_urouter/bin/msip_bhu_unit_rest_urouter.jar -d msip_bhu_unit_rest_urouter/classes/
unzip -q msip_bhu_unit_uass-bin.zip
unzip -qo msip_bhu_unit_uass/bin/msip_bhu_unit_uass.jar -d msip_bhu_unit_uass/classes/



#unzip -q msip_bhu_unit_agent-bin.zip
#unzip -qo msip_bhu_unit_agent/bin/msip_bhu_unit_agent.jar -d msip_bhu_unit_agent/classes/




unzip -q msip_bhu_backend_online-bin.zip
unzip -qo msip_bhu_backend_online/bin/msip_bhu_backend_online.jar -d msip_bhu_backend_online/classes/

unzip -q msip_bhu_backend_async_online-bin.zip
unzip -qo msip_bhu_backend_async_online/bin/msip_bhu_backend_async_online.jar -d msip_bhu_backend_async_online/classes/

unzip -q msip_bhu_backend_task-bin.zip
unzip -qo msip_bhu_backend_task/bin/msip_bhu_backend_task.jar -d msip_bhu_backend_task/classes/

#unzip -q msip_bhu_backend_wifistasniffer-bin.zip
#unzip -qo msip_bhu_backend_wifistasniffer/bin/msip_bhu_backend_wifistasniffer.jar -d msip_bhu_backend_wifistasniffer/classes/

#unzip -q msip_bhu_backend_modulestat-bin.zip
#unzip -qo msip_bhu_backend_modulestat/bin/msip_bhu_backend_modulestat.jar -d msip_bhu_backend_modulestat/classes/


unzip -q msip_bhu_dataimport-bin.zip
unzip -qo msip_bhu_dataimport/bin/msip_bhu_dataimport.jar -d msip_bhu_dataimport/classes/

#unzip -q msip_bhu_spark_task-bin.zip
#unzip -qo msip_bhu_spark_task/bin/msip_bhu_spark_task.jar -d msip_bhu_spark_task/classes/

unzip -qo msip_bhu_rest.war -d msip_bhu_rest
#unzip -qo msip_bhu_agent_rest.war -d msip_bhu_agent_rest

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


echo '文件解压过程成功'

echo '准备发布业务组件到'$Deploy2Server

#echo 'deploy msip_bhu_unit_input_processor to ...@'$Deploy2Server
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_input_processor/lib/spring*.RELEASE.jar   root@$Deploy2Server:/BHUData/apps/msip_bhu_unit_input_processor/libs/
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_input_processor/lib/msip_*.jar    root@$Deploy2Server:/BHUData/apps/msip_bhu_unit_input_processor/libs/
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_input_processor/classes/com/    root@$Deploy2Server:/BHUData/apps/msip_bhu_unit_input_processor/classes/com/
#echo 'deploy msip_bhu_unit_input_processor successfully @'$Deploy2Server

echo 'deploy msip_bhu_unit_input_processor to ...@'$Deploy2Server1
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_input_processor/lib/spring*.RELEASE.jar   root@$Deploy2Server1:/BHUData/apps/msip_bhu_unit_input_processor/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_input_processor/lib/msip_*.jar    root@$Deploy2Server1:/BHUData/apps/msip_bhu_unit_input_processor/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_input_processor/classes/com/    root@$Deploy2Server1:/BHUData/apps/msip_bhu_unit_input_processor/classes/com/
echo 'deploy msip_bhu_unit_input_processor successfully @'$Deploy2Server1

echo 'deploy msip_bhu_unit_input_processor to ...@'$Deploy2Server2
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_input_processor/lib/spring*.RELEASE.jar   root@$Deploy2Server2:/BHUData/apps/msip_bhu_unit_input_processor/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_input_processor/lib/msip_*.jar    root@$Deploy2Server2:/BHUData/apps/msip_bhu_unit_input_processor/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_input_processor/classes/com/    root@$Deploy2Server2:/BHUData/apps/msip_bhu_unit_input_processor/classes/com/
echo 'deploy msip_bhu_unit_input_processor successfully @'$Deploy2Server2

echo 'deploy msip_bhu_unit_daemon_processor to ...@'$Deploy2Server0
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_daemon_processor/lib/spring*.RELEASE.jar root@$Deploy2Server0:/BHUData/apps/msip_bhu_unit_daemon_processor/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_daemon_processor/lib/msip_*.jar  root@$Deploy2Server0:/BHUData/apps/msip_bhu_unit_daemon_processor/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_daemon_processor/classes/com/    root@$Deploy2Server0:/BHUData/apps/msip_bhu_unit_daemon_processor/classes/com/
echo 'deploy msip_bhu_unit_daemon_processor successfully @'$Deploy2Server0

echo 'deploy msip_bhu_unit_devices to ...@'$Deploy2Server0
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_devices/lib/spring*.RELEASE.jar        root@$Deploy2Server0:/BHUData/apps/msip_bhu_unit_devices/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_devices/lib/msip_*.jar       root@$Deploy2Server0:/BHUData/apps/msip_bhu_unit_devices/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_devices/classes/com/         root@$Deploy2Server0:/BHUData/apps/msip_bhu_unit_devices/classes/com/
echo 'deploy msip_bhu_unit_devices successfully @'$Deploy2Server0

echo 'deploy msip_bhu_unit_vas to ...@'$Deploy2Server0
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_vas/lib/spring*.RELEASE.jar      root@$Deploy2Server0:/BHUData/apps/msip_bhu_unit_vas/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_vas/lib/msip_*.jar       root@$Deploy2Server0:/BHUData/apps/msip_bhu_unit_vas/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_vas/classes/com/         root@$Deploy2Server0:/BHUData/apps/msip_bhu_unit_vas/classes/com/
echo 'deploy msip_bhu_unit_vas successfully @'$Deploy2Server0

echo 'deploy msip_bhu_unit_uass to ...@'$Deploy2Server1
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_uass/lib/spring*.RELEASE.jar  root@$Deploy2Server1:/BHUData/apps/msip_bhu_unit_uass/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_uass/lib/msip_*.jar           root@$Deploy2Server1:/BHUData/apps/msip_bhu_unit_uass/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_uass/classes/com/             root@$Deploy2Server1:/BHUData/apps/msip_bhu_unit_uass/classes/com/
echo 'deploy msip_bhu_unit_uass successfully @'$Deploy2Server1


echo 'deploy msip_bhu_unit_rest_urouter to ...@'$Deploy2Server1
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_rest_urouter/lib/spring*.RELEASE.jar  root@$Deploy2Server1:/BHUData/apps/msip_bhu_unit_rest_urouter/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_rest_urouter/lib/msip_*.jar           root@$Deploy2Server1:/BHUData/apps/msip_bhu_unit_rest_urouter/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_rest_urouter/classes/com/             root@$Deploy2Server1:/BHUData/apps/msip_bhu_unit_rest_urouter/classes/com/
echo 'deploy msip_bhu_unit_rest_urouter successfully @'$Deploy2Server1

#echo 'deploy msip_bhu_unit_agent to ...@'$Deploy2Server2
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_agent/lib/spring*.RELEASE.jar      root@$Deploy2Server2:/BHUData/apps/msip_bhu_unit_agent/libs/
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_agent/lib/msip_*.jar       root@$Deploy2Server2:/BHUData/apps/msip_bhu_unit_agent/libs/
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_agent/classes/com/         root@$Deploy2Server2:/BHUData/apps/msip_bhu_unit_agent/classes/com/
#echo 'deploy msip_bhu_unit_agent successfully @'$Deploy2Server2

echo 'deploy msip_bhu_unit_commdity to ...@'$Deploy2Server1
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_commdity/lib/spring*.RELEASE.jar  root@$Deploy2Server1:/BHUData/apps/msip_bhu_unit_commdity/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_commdity/lib/msip_*.jar           root@$Deploy2Server1:/BHUData/apps/msip_bhu_unit_commdity/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_commdity/classes/com/             root@$Deploy2Server1:/BHUData/apps/msip_bhu_unit_commdity/classes/com/
echo 'deploy msip_bhu_unit_commdity successfully @'$Deploy2Server1

echo 'deploy msip_bhu_unit_tag to ...@'$Deploy2Server0
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_tag/lib/spring*.RELEASE.jar  root@$Deploy2Server0:/BHUData/apps/msip_bhu_unit_tag/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_tag/lib/msip_*.jar           root@$Deploy2Server0:/BHUData/apps/msip_bhu_unit_tag/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_tag/classes/com/             root@$Deploy2Server0:/BHUData/apps/msip_bhu_unit_tag/classes/com/
echo 'deploy msip_bhu_unit_tag successfully @'$Deploy2Server0

echo '发布业务组件成功'

echo '准备发布其他服务到'$Deploy2Server0

echo 'deploy msip_bhu_backend_task to ...@'$Deploy2Server0
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_task/lib/spring*.RELEASE.jar    root@$Deploy2Server0:/BHUData/apps/msip_bhu_backend_task/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_task/lib/msip_*.jar   root@$Deploy2Server0:/BHUData/apps/msip_bhu_backend_task/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_task/classes/com/     root@$Deploy2Server0:/BHUData/apps/msip_bhu_backend_task/bin/com/
echo 'deploy msip_bhu_backend_task successfully @'$Deploy2Server0

#echo 'deploy msip_bhu_backend_wifistasniffer to ...@'$Deploy2Server0
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_wifistasniffer/lib/spring*.RELEASE.jar    root@$Deploy2Server0:/BHUData/apps/msip_bhu_backend_wifistasniffer/libs/
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_wifistasniffer/lib/msip_*.jar   root@$Deploy2Server0:/BHUData/apps/msip_bhu_backend_wifistasniffer/libs/
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_wifistasniffer/classes/com/     root@$Deploy2Server0:/BHUData/apps/msip_bhu_backend_wifistasniffer/bin/com/
#echo 'deploy msip_bhu_backend_wifistasniffer successfully @'$Deploy2Server0

#echo 'deploy msip_bhu_backend_modulestat to ...@'$Deploy2Server0
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_modulestat/lib/spring*.RELEASE.jar    root@$Deploy2Server0:/BHUData/apps/msip_bhu_backend_modulestat/libs/
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_modulestat/lib/msip_*.jar   root@$Deploy2Server0:/BHUData/apps/msip_bhu_backend_modulestat/libs/
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_modulestat/classes/com/     root@$Deploy2Server0:/BHUData/apps/msip_bhu_backend_modulestat/bin/com/
#echo 'deploy msip_bhu_backend_modulestat successfully @'$Deploy2Server0

echo 'deploy msip_bhu_dataimport to ...@'$Deploy2Server0
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_dataimport/lib/spring*.RELEASE.jar      root@$Deploy2Server0:/BHUData/apps/msip_bhu_dataimport/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_dataimport/lib/msip_*.jar     root@$Deploy2Server0:/BHUData/apps/msip_bhu_dataimport/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_dataimport/classes/com/     root@$Deploy2Server0:/BHUData/apps/msip_bhu_dataimport/bin/com/
echo 'deploy msip_bhu_dataimport successfully @'$Deploy2Server0

echo 'deploy msip_bhu_dataimport to ...@'$Deploy2Server2
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_dataimport/lib/spring*.RELEASE.jar      root@$Deploy2Server2:/BHUData/apps/msip_bhu_dataimport/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_dataimport/lib/msip_*.jar     root@$Deploy2Server2:/BHUData/apps/msip_bhu_dataimport/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_dataimport/classes/com/     root@$Deploy2Server2:/BHUData/apps/msip_bhu_dataimport/bin/com/
echo 'deploy msip_bhu_dataimport successfully @'$Deploy2Server2

echo 'deploy msip_bhu_backend_online to ...@'$Deploy2Server0
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_online/lib/spring*.RELEASE.jar    root@$Deploy2Server0:/BHUData/apps/msip_bhu_backend_online/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_online/lib/msip_*.jar   root@$Deploy2Server0:/BHUData/apps/msip_bhu_backend_online/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_online/classes/com/   root@$Deploy2Server0:/BHUData/apps/msip_bhu_backend_online/bin/com/
echo 'deploy msip_bhu_backend_online successfully @'$Deploy2Server0

echo 'deploy msip_bhu_backend_async_online to ...@'$Deploy2Server0
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_async_online/lib/spring*.RELEASE.jar    root@$Deploy2Server0:/BHUData/apps/msip_bhu_backend_async_online/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_async_online/lib/msip_*.jar   root@$Deploy2Server0:/BHUData/apps/msip_bhu_backend_async_online/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_async_online/classes/com/   root@$Deploy2Server0:/BHUData/apps/msip_bhu_backend_async_online/bin/com/
echo 'deploy msip_bhu_backend_async_online successfully @'$Deploy2Server0

echo 'deploy msip_bhu_backend_commdity to ...@'$Deploy2Server1
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_commdity/lib/spring*.RELEASE.jar    root@$Deploy2Server1:/BHUData/apps/msip_bhu_backend_commdity/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_commdity/lib/msip_*.jar   root@$Deploy2Server1:/BHUData/apps/msip_bhu_backend_commdity/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_commdity/classes/com/   root@$Deploy2Server1:/BHUData/apps/msip_bhu_backend_commdity/bin/com/
echo 'deploy msip_bhu_backend_commdity successfully @'$Deploy2Server1

echo 'deploy msip_bhu_backend_task_applies_notify to ...@'$Deploy2Server1
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_task_applies_notify/lib/spring*.RELEASE.jar    root@$Deploy2Server1:/BHUData/apps/msip_bhu_backend_task_applies_notify/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_task_applies_notify/lib/msip_*.jar   root@$Deploy2Server1:/BHUData/apps/msip_bhu_backend_task_applies_notify/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_task_applies_notify/classes/com/     root@$Deploy2Server1:/BHUData/apps/msip_bhu_backend_task_applies_notify/bin/com/
echo 'deploy msip_bhu_backend_task_applies_notify successfully @'$Deploy2Server1

#echo 'deploy msip_bhu_spark_task successfully @'$Deploy2Server0
echo '发布其他服务成功'


echo 'deploy msip_bhu_rest to ...@'$Deploy2ServerWeb
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_rest/WEB-INF/lib/spring*.RELEASE.jar    root@$Deploy2ServerWeb:/BHUData/apps/msip_bhu_rest/WEB-INF/lib/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_rest/WEB-INF/lib/msip_*.jar   root@$Deploy2ServerWeb:/BHUData/apps/msip_bhu_rest/WEB-INF/lib/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_rest/WEB-INF/classes/com/     root@$Deploy2ServerWeb:/BHUData/apps/msip_bhu_rest/WEB-INF/classes/com/
echo 'deploy msip_bhu_rest successfully @'$Deploy2ServerWeb

#echo 'deploy msip_bhu_agent_rest to ...@'$Deploy2ServerWeb
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_agent_rest/WEB-INF/lib/spring*.RELEASE.jar    root@$Deploy2ServerWeb:/BHUData/apps/msip_bhu_agent_rest/WEB-INF/lib/
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_agent_rest/WEB-INF/lib/msip_*.jar   root@$Deploy2ServerWeb:/BHUData/apps/msip_bhu_agent_rest/WEB-INF/lib/
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_agent_rest/WEB-INF/classes/com/     root@$Deploy2ServerWeb:/BHUData/apps/msip_bhu_agent_rest/WEB-INF/classes/com/
#echo 'deploy msip_bhu_agent_rest successfully @'$Deploy2ServerWeb

echo 'deploy msip_bhu_commdity_rest to ...@'$Deploy2ServerWeb
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_commdity_rest/WEB-INF/lib/spring*.RELEASE.jar    root@$Deploy2ServerWeb:/BHUData/apps/msip_bhu_commdity_rest/WEB-INF/lib/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_commdity_rest/WEB-INF/lib/msip_*.jar   root@$Deploy2ServerWeb:/BHUData/apps/msip_bhu_commdity_rest/WEB-INF/lib/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_commdity_rest/WEB-INF/classes/com/     root@$Deploy2ServerWeb:/BHUData/apps/msip_bhu_commdity_rest/WEB-INF/classes/com/
echo 'deploy msip_bhu_commdity_rest successfully @'$Deploy2ServerWeb

echo '发布rest api服务成功'

