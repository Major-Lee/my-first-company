
cd `dirname $0`
Current_DIR=`pwd`
echo $Current_DIR

Deploy2WebServerRest='182.92.229.26'

Deploy2ComponentServerInputFranky='123.57.82.6'
Deploy2ComponentServerInputRobin='123.57.13.158'

#Sanji
Deploy2ComponentServerSanji='123.56.121.220'
#Brook
Deploy2ComponentServerBrook='182.92.231.58'
#Usoop
Deploy2ComponentServerUsoop='123.56.137.28'
#Law
Deploy2ComponentServerLaw='123.57.31.54'

DeployUser=$1

read -n1 -p "Do you want to deploy 2 $Deploy2ComponentServerSanji(Remote Production) [Y/N]?"
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

echo "starting deploy2=>"$Deploy2ComponentServerSanji
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
echo '拷贝文件 msip_bhu_unit_uass-bin.zip到'$CuDateDir
cp ../../msip-bhu-unit/msip_bhu_unit_uass/target/msip_bhu_unit_uass-bin.zip ./$CuDateDir
#commdity 
echo '拷贝文件 msip_bhu_unit_commdity-bin.zip到'$CuDateDir
cp ../../msip-bhu-unit/msip_bhu_unit_commdity/target/msip_bhu_unit_commdity-bin.zip ./$CuDateDir

echo '拷贝文件 msip_bhu_backend_commdity-bin.zip到'$CuDateDir
cp ../../msip-bhu-business-impl/msip_bhu_backend_commdity/target/msip_bhu_backend_commdity-bin.zip ./$CuDateDir

echo '拷贝文件 msip_bhu_backend_task_applies_notify-bin.zip到'$CuDateDir
cp ../../msip-bhu-business-impl/msip_bhu_backend_task_applies_notify/target/msip_bhu_backend_task_applies_notify-bin.zip ./$CuDateDir

echo '拷贝文件 msip_bhu_commdity_rest.war到'$CuDateDir
cp ../../msip-bhu-api/msip_bhu_commdity_rest/target/msip_bhu_commdity_rest.war ./$CuDateDir

echo '拷贝文件 msip_bhu_rest.war到'$CuDateDir
cp ../../msip-bhu-api/msip_bhu_rest/target/msip_bhu_rest.war ./$CuDateDir

cd $CuDateDir
echo '进行文件解压过程'

unzip -q msip_bhu_unit_uass-bin.zip
unzip -qo msip_bhu_unit_uass/bin/msip_bhu_unit_uass.jar -d msip_bhu_unit_uass/classes/
#commdity 
unzip -q msip_bhu_unit_commdity-bin.zip
unzip -qo msip_bhu_unit_commdity/bin/msip_bhu_unit_commdity.jar -d msip_bhu_unit_commdity/classes/

unzip -q msip_bhu_backend_commdity-bin.zip
unzip -qo msip_bhu_backend_commdity/bin/msip_bhu_backend_commdity.jar -d msip_bhu_backend_commdity/classes/

unzip -q msip_bhu_backend_task_applies_notify-bin.zip
unzip -qo msip_bhu_backend_task_applies_notify/bin/msip_bhu_backend_task_applies_notify.jar -d msip_bhu_backend_task_applies_notify/classes/

unzip -qo msip_bhu_commdity_rest.war -d msip_bhu_commdity_rest

unzip -qo msip_bhu_rest.war -d msip_bhu_rest
echo '文件解压过程成功'

echo 'deploy msip_bhu_unit_uass to ...@'$Deploy2ComponentServerSanji
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_uass/lib/spring*.RELEASE.jar  root@$Deploy2ComponentServerSanji:/BHUData/apps/msip_bhu_unit_uass/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_uass/lib/msip_*.jar           root@$Deploy2ComponentServerSanji:/BHUData/apps/msip_bhu_unit_uass/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_uass/classes/com/             root@$Deploy2ComponentServerSanji:/BHUData/apps/msip_bhu_unit_uass/classes/com/
echo 'deploy msip_bhu_unit_uass successfully @'$Deploy2ComponentServerSanji


echo '准备发布其他服务到'$Deploy2ComponentServerLaw

echo 'deploy msip_bhu_unit_commdity to ...@'$Deploy2ComponentServerLaw
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_commdity/lib/spring*.RELEASE.jar  root@$Deploy2ComponentServerLaw:/BHUData/apps/msip_bhu_unit_commdity/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_commdity/lib/msip_*.jar           root@$Deploy2ComponentServerLaw:/BHUData/apps/msip_bhu_unit_commdity/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_commdity/classes/com/             root@$Deploy2ComponentServerLaw:/BHUData/apps/msip_bhu_unit_commdity/classes/com/
echo 'deploy msip_bhu_unit_commdity successfully @'$Deploy2ComponentServerLaw

echo 'deploy msip_bhu_backend_commdity to ...@'$Deploy2ComponentServerLaw
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_commdity/lib/spring*.RELEASE.jar    root@$Deploy2ComponentServerLaw:/BHUData/apps/msip_bhu_backend_commdity/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_commdity/lib/msip_*.jar   root@$Deploy2ComponentServerLaw:/BHUData/apps/msip_bhu_backend_commdity/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_commdity/classes/com/   root@$Deploy2ComponentServerLaw:/BHUData/apps/msip_bhu_backend_commdity/bin/com/
echo 'deploy msip_bhu_backend_commdity successfully @'$Deploy2ComponentServerLaw

echo 'deploy msip_bhu_backend_task_applies_notify to ...@'$Deploy2ComponentServerLaw
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_task_applies_notify/lib/spring*.RELEASE.jar    root@$Deploy2ComponentServerLaw:/BHUData/apps/msip_bhu_backend_task_applies_notify/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_task_applies_notify/lib/msip_*.jar   root@$Deploy2ComponentServerLaw:/BHUData/apps/msip_bhu_backend_task_applies_notify/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_task_applies_notify/classes/com/     root@$Deploy2ComponentServerLaw:/BHUData/apps/msip_bhu_backend_task_applies_notify/bin/com/
echo 'deploy msip_bhu_backend_task_applies_notify successfully @'$Deploy2ComponentServerLaw

echo 'deploy msip_bhu_commdity_rest to ...@'$Deploy2ComponentServerLaw
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_commdity_rest/WEB-INF/lib/spring*.RELEASE.jar    root@$Deploy2ComponentServerLaw:/BHUData/apps/msip_bhu_commdity_rest/WEB-INF/lib/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_commdity_rest/WEB-INF/lib/msip_*.jar   root@$Deploy2ComponentServerLaw:/BHUData/apps/msip_bhu_commdity_rest/WEB-INF/lib/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_commdity_rest/WEB-INF/classes/com/     root@$Deploy2ComponentServerLaw:/BHUData/apps/msip_bhu_commdity_rest/WEB-INF/classes/com/
echo 'deploy msip_bhu_commdity_rest successfully @'$Deploy2ComponentServerLaw

echo '发布其他服务成功'$Deploy2ComponentServerLaw


echo 'deploy msip_bhu_rest to ...@'$Deploy2WebServerRest
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_rest/WEB-INF/lib/spring*.RELEASE.jar  	root@$Deploy2WebServerRest:/BHUData/apps/msip_bhu_rest/WEB-INF/lib/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_rest/WEB-INF/lib/msip_*.jar  	root@$Deploy2WebServerRest:/BHUData/apps/msip_bhu_rest/WEB-INF/lib/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_rest/WEB-INF/classes/com/ 		root@$Deploy2WebServerRest:/BHUData/apps/msip_bhu_rest/WEB-INF/classes/com/
echo 'deploy msip_bhu_rest successfully @'$Deploy2WebServerRest
echo '发布其他服务成功'

