
cd `dirname $0`
Current_DIR=`pwd`
echo $Current_DIR

Deploy2ComponentServer1='123.56.121.220'
Deploy2ComponentServer2='182.92.231.58'
Deploy2ComponentServer3='123.56.137.28'
DeployUser=$1

read -n1 -p "Do you want to deploy 2 $Deploy2ComponentServer1(Remote Production) [Y/N]?"
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

echo "starting deploy2=>"$Deploy2ComponentServer1
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
echo '拷贝文件 msip_bhu_unit_daemon_processor-bin.zip到'$CuDateDir
cp ../../msip-bhu-unit/msip_bhu_unit_daemon_processor/target/msip_bhu_unit_daemon_processor-bin.zip ./$CuDateDir
echo '拷贝文件 msip_bhu_unit_devices-bin.zip到'$CuDateDir
cp ../../msip-bhu-unit/msip_bhu_unit_devices/target/msip_bhu_unit_devices-bin.zip ./$CuDateDir
echo '拷贝文件 msip_bhu_unit_vas-bin.zip到'$CuDateDir
cp ../../msip-bhu-unit/msip_bhu_unit_vas/target/msip_bhu_unit_vas-bin.zip ./$CuDateDir
echo '拷贝文件 msip_bhu_unit_agent-bin.zip到'$CuDateDir
cp ../../msip-bhu-unit/msip_bhu_unit_agent/target/msip_bhu_unit_agent-bin.zip ./$CuDateDir


echo '拷贝文件 msip_bhu_spark_task-bin.zip到'$CuDateDir
cp ../../msip-bhu-business-impl/msip_bhu_spark_task/target/msip_bhu_spark_task-bin.zip ./$CuDateDir
echo '拷贝文件 msip_bhu_backend_online-bin.zip到'$CuDateDir
cp ../../msip-bhu-business-impl/msip_bhu_backend_online/target/msip_bhu_backend_online-bin.zip ./$CuDateDir
echo '拷贝文件 msip_bhu_backend_task-bin.zip到'$CuDateDir
cp ../../msip-bhu-business-impl/msip_bhu_backend_task/target/msip_bhu_backend_task-bin.zip ./$CuDateDir
echo '拷贝文件 msip_bhu_backend_wifistasniffer-bin.zip到'$CuDateDir
cp ../../msip-bhu-business-impl/msip_bhu_backend_wifistasniffer/target/msip_bhu_backend_wifistasniffer-bin.zip ./$CuDateDir
echo '拷贝文件 msip_bhu_dataimport-bin.zip到'$CuDateDir
cp ../../msip-bhu-business-impl/msip_bhu_dataimport/target/msip_bhu_dataimport-bin.zip ./$CuDateDir

echo '拷贝文件 msip_bhu_rest.war到'$CuDateDir
cp ../../msip-bhu-api/msip_bhu_rest/target/msip_bhu_rest.war ./$CuDateDir

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
unzip -q msip_bhu_unit_agent-bin.zip
unzip -qo msip_bhu_unit_agent/bin/msip_bhu_unit_agent.jar -d msip_bhu_unit_agent/classes/

unzip -q msip_bhu_spark_task-bin.zip
unzip -qo msip_bhu_spark_task/bin/msip_bhu_spark_task.jar -d msip_bhu_spark_task/classes/

unzip -q msip_bhu_backend_online-bin.zip
unzip -qo msip_bhu_backend_online/bin/msip_bhu_backend_online.jar -d msip_bhu_backend_online/classes/

unzip -q msip_bhu_backend_task-bin.zip
unzip -qo msip_bhu_backend_task/bin/msip_bhu_backend_task.jar -d msip_bhu_backend_task/classes/

unzip -q msip_bhu_backend_wifistasniffer-bin.zip
unzip -qo msip_bhu_backend_wifistasniffer/bin/msip_bhu_backend_wifistasniffer.jar -d msip_bhu_backend_wifistasniffer/classes/

unzip -q msip_bhu_dataimport-bin.zip
unzip -qo msip_bhu_dataimport/bin/msip_bhu_dataimport.jar -d msip_bhu_dataimport/classes/

unzip -qo msip_bhu_rest.war -d msip_bhu_rest
echo '文件解压过程成功'


echo '准备发布业务组件到'$Deploy2ComponentServer1

echo 'deploy msip_bhu_unit_input_processor to ...@'$Deploy2ComponentServer1
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_input_processor/lib/spring*.RELEASE.jar 	root@$Deploy2ComponentServer1:/BHUData/apps/msip_bhu_unit_input_processor/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_input_processor/lib/msip_*.jar  	root@$Deploy2ComponentServer1:/BHUData/apps/msip_bhu_unit_input_processor/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_input_processor/classes/com/ 	root@$Deploy2ComponentServer1:/BHUData/apps/msip_bhu_unit_input_processor/classes/com/
echo 'deploy msip_bhu_unit_input_processor successfully @'$Deploy2ComponentServer1

echo 'deploy msip_bhu_unit_daemon_processor to ...@'$Deploy2ComponentServer1
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_daemon_processor/lib/spring*.RELEASE.jar	root@$Deploy2ComponentServer1:/BHUData/apps/msip_bhu_unit_daemon_processor/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_daemon_processor/lib/msip_*.jar	root@$Deploy2ComponentServer1:/BHUData/apps/msip_bhu_unit_daemon_processor/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_daemon_processor/classes/com/ 	root@$Deploy2ComponentServer1:/BHUData/apps/msip_bhu_unit_daemon_processor/classes/com/
echo 'deploy msip_bhu_unit_daemon_processor successfully @'$Deploy2ComponentServer1

echo 'deploy msip_bhu_unit_devices to ...@'$Deploy2ComponentServer1
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_devices/lib/spring*.RELEASE.jar  			root@$Deploy2ComponentServer1:/BHUData/apps/msip_bhu_unit_devices/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_devices/lib/msip_*.jar  		root@$Deploy2ComponentServer1:/BHUData/apps/msip_bhu_unit_devices/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_devices/classes/com/ 		root@$Deploy2ComponentServer1:/BHUData/apps/msip_bhu_unit_devices/classes/com/
echo 'deploy msip_bhu_unit_devices successfully @'$Deploy2ComponentServer1

echo 'deploy msip_bhu_unit_vas to ...@'$Deploy2ComponentServer1
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_vas/lib/spring*.RELEASE.jar  		root@$Deploy2ComponentServer1:/BHUData/apps/msip_bhu_unit_vas/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_vas/lib/msip_*.jar  			root@$Deploy2ComponentServer1:/BHUData/apps/msip_bhu_unit_vas/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_vas/classes/com/ 			root@$Deploy2ComponentServer1:/BHUData/apps/msip_bhu_unit_vas/classes/com/
echo 'deploy msip_bhu_unit_vas successfully @'$Deploy2ComponentServer1

echo 'deploy msip_bhu_unit_agent to ...@'$Deploy2ComponentServer2
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_agent/lib/spring*.RELEASE.jar      root@$Deploy2ComponentServer2:/BHUData/apps/msip_bhu_unit_agent/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_agent/lib/msip_*.jar       root@$Deploy2ComponentServer2:/BHUData/apps/msip_bhu_unit_agent/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_agent/classes/com/         root@$Deploy2ComponentServer2:/BHUData/apps/msip_bhu_unit_agent/classes/com/
echo 'deploy msip_bhu_unit_agent successfully @'$Deploy2ComponentServer2

echo '发布业务组件成功'$Deploy2ComponentServer1

echo '准备发布其他服务到'$Deploy2ComponentServer1

echo 'deploy msip_bhu_backend_online to ...@'$Deploy2ComponentServer1
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_online/lib/spring*.RELEASE.jar  	root@$Deploy2ComponentServer1:/BHUData/apps/msip_bhu_backend_online/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_online/lib/msip_*.jar  	root@$Deploy2ComponentServer1:/BHUData/apps/msip_bhu_backend_online/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_online/classes/com/ 		root@$Deploy2ComponentServer1:/BHUData/apps/msip_bhu_backend_online/bin/com/
echo 'deploy msip_bhu_backend_online successfully @'$Deploy2ComponentServer1

echo 'deploy msip_bhu_backend_task to ...@'$Deploy2ComponentServer1
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_task/lib/spring*.RELEASE.jar		root@$Deploy2ComponentServer1:/BHUData/apps/msip_bhu_backend_task/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_task/lib/msip_*.jar		root@$Deploy2ComponentServer1:/BHUData/apps/msip_bhu_backend_task/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_task/classes/com/ 		root@$Deploy2ComponentServer1:/BHUData/apps/msip_bhu_backend_task/bin/com/
echo 'deploy msip_bhu_backend_task successfully @'$Deploy2ComponentServer1

echo 'deploy msip_bhu_dataimport to ...@'$Deploy2ComponentServer1
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_dataimport/lib/spring*.RELEASE.jar  		root@$Deploy2ComponentServer1:/BHUData/apps/msip_bhu_dataimport/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_dataimport/lib/msip_*.jar  		root@$Deploy2ComponentServer1:/BHUData/apps/msip_bhu_dataimport/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_dataimport/classes/com/ 			root@$Deploy2ComponentServer1:/BHUData/apps/msip_bhu_dataimport/bin/com/
echo 'deploy msip_bhu_dataimport successfully @'$Deploy2ComponentServer1
echo '发布其他服务成功'$Deploy2ComponentServer1


#echo '准备发布业务组件到'$Deploy2ComponentServer2

#echo 'deploy msip_bhu_unit_daemon_processor to ...@'$Deploy2ComponentServer2
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_daemon_processor/lib/msip_*.jar	$DeployUser@$Deploy2ComponentServer2:/BHUData/apps/msip_bhu_unit_daemon_processor/libs/
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_daemon_processor/classes/com/ 	$DeployUser@$Deploy2ComponentServer2:/BHUData/apps/msip_bhu_unit_daemon_processor/classes/com/
#echo 'deploy msip_bhu_unit_daemon_processor successfully @'$Deploy2ComponentServer2

#echo 'deploy msip_bhu_unit_devices to ...@'$Deploy2ComponentServer2
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_devices/lib/msip_*.jar  		$DeployUser@$Deploy2ComponentServer2:/BHUData/apps/msip_bhu_unit_devices/libs/
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_devices/classes/com/ 		$DeployUser@$Deploy2ComponentServer2:/BHUData/apps/msip_bhu_unit_devices/classes/com/
#echo 'deploy msip_bhu_unit_devices successfully @'$Deploy2ComponentServer2

#echo 'deploy msip_bhu_unit_vas to ...@'$Deploy2ComponentServer2
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_vas/lib/msip_*.jar  			$DeployUser@$Deploy2ComponentServer2:/BHUData/apps/msip_bhu_unit_vas/libs/
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_vas/classes/com/ 			$DeployUser@$Deploy2ComponentServer2:/BHUData/apps/msip_bhu_unit_vas/classes/com/
#echo 'deploy msip_bhu_unit_vas successfully @'$Deploy2ComponentServer2
#echo '发布业务组件成功'$Deploy2ComponentServer2


#echo '准备发布其他服务到'$Deploy2ComponentServer2

#echo 'deploy msip_bhu_backend_online to ...@'$Deploy2ComponentServer2
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_online/lib/msip_*.jar  	$DeployUser@$Deploy2ComponentServer2:/BHUData/apps/msip_bhu_backend_online/libs/
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_online/classes/com/ 		$DeployUser@$Deploy2ComponentServer2:/BHUData/apps/msip_bhu_backend_online/bin/com/
#echo 'deploy msip_bhu_backend_online successfully @'$Deploy2ComponentServer2

#echo 'deploy msip_bhu_spark_task to ...@'$Deploy2ComponentServer2
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_spark_task/lib/msip_*.jar     $DeployUser@$Deploy2ComponentServer2:/BHUData/apps/msip_bhu_spark_task/libs/
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_spark_task/classes/com/     $DeployUser@$Deploy2ComponentServer2:/BHUData/apps/msip_bhu_spark_task/bin/com/
#echo 'deploy msip_bhu_spark_task successfully @'$Deploy2ComponentServer2

#echo 'deploy msip_bhu_backend_wifistasniffer to ...@'$Deploy2ComponentServer2
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_wifistasniffer/lib/spring*-4.1.2.RELEASE.jar    root@$Deploy2ComponentServer1:/BHUData/apps/msip_bhu_backend_wifistasniffer/libs/
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_wifistasniffer/lib/msip_*.jar   root@$Deploy2ComponentServer2:/BHUData/apps/msip_bhu_backend_wifistasniffer/libs/
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_wifistasniffer/classes/com/     root@$Deploy2ComponentServer2:/BHUData/apps/msip_bhu_backend_wifistasniffer/bin/com/
#echo 'deploy msip_bhu_backend_wifistasniffer successfully @'$Deploy2ComponentServer2

#echo '发布其他服务成功'$Deploy2ComponentServer2

echo '准备发布其他服务到'$Deploy2ComponentServer3

echo 'deploy msip_bhu_backend_wifistasniffer to ...@'$Deploy2ComponentServer3
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_wifistasniffer/lib/spring*.RELEASE.jar    root@$Deploy2ComponentServer3:/BHUData/apps/msip_bhu_backend_wifistasniffer/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_wifistasniffer/lib/msip_*.jar   root@$Deploy2ComponentServer3:/BHUData/apps/msip_bhu_backend_wifistasniffer/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_wifistasniffer/classes/com/     root@$Deploy2ComponentServer3:/BHUData/apps/msip_bhu_backend_wifistasniffer/bin/com/
echo 'deploy msip_bhu_backend_wifistasniffer successfully @'$Deploy2ComponentServer3

echo '发布其他服务成功'$Deploy2ComponentServer3


echo 'deploy msip_bhu_rest to ...@'182.92.229.26
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_rest/WEB-INF/lib/spring*.RELEASE.jar  	root@182.92.229.26:/BHUData/apps/msip_bhu_rest/WEB-INF/lib/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_rest/WEB-INF/lib/msip_*.jar  	root@182.92.229.26:/BHUData/apps/msip_bhu_rest/WEB-INF/lib/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_rest/WEB-INF/classes/com/ 		root@182.92.229.26:/BHUData/apps/msip_bhu_rest/WEB-INF/classes/com/
echo 'deploy msip_bhu_rest successfully @'182.92.229.26
echo '发布其他服务成功'

