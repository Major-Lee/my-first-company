
cd `dirname $0`
Current_DIR=`pwd`
echo $Current_DIR

Deploy2WebAgentServerRest='123.57.70.183'

DeployUser=$1

read -n1 -p "Do you want to deploy 2 $Deploy2WebAgentServerRest(Remote Production) [Y/N]?"
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

echo "starting deploy2=>"$Deploy2WebAgentServerRest
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
echo '拷贝文件 msip_bhu_unit_agent-bin.zip到'$CuDateDir
cp ../../msip-bhu-unit/msip_bhu_unit_agent/target/msip_bhu_unit_agent-bin.zip ./$CuDateDir

echo '拷贝文件 msip_bhu_backend_online-bin.zip到'$CuDateDir
cp ../../msip-bhu-business-impl/msip_bhu_backend_online/target/msip_bhu_backend_online-bin.zip ./$CuDateDir
echo '拷贝文件 msip_bhu_dataimport-bin.zip到'$CuDateDir
cp ../../msip-bhu-business-impl/msip_bhu_dataimport/target/msip_bhu_dataimport-bin.zip ./$CuDateDir

echo '拷贝文件 msip_bhu_agent_rest.war到'$CuDateDir
cp ../../msip-bhu-api/msip_bhu_agent_rest/target/msip_bhu_agent_rest.war ./$CuDateDir

cd $CuDateDir
echo '进行文件解压过程'
unzip -q msip_bhu_unit_agent-bin.zip
unzip -qo msip_bhu_unit_agent/bin/msip_bhu_unit_agent.jar -d msip_bhu_unit_agent/classes/

unzip -q msip_bhu_backend_online-bin.zip
unzip -qo msip_bhu_backend_online/bin/msip_bhu_backend_online.jar -d msip_bhu_backend_online/classes/

unzip -q msip_bhu_dataimport-bin.zip
unzip -qo msip_bhu_dataimport/bin/msip_bhu_dataimport.jar -d msip_bhu_dataimport/classes/

unzip -qo msip_bhu_agent_rest.war -d msip_bhu_agent_rest
echo '文件解压过程成功'

echo 'deploy msip_bhu_unit_agent to ...@'$Deploy2WebAgentServerRest
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_agent/lib/spring*.RELEASE.jar      root@$Deploy2WebAgentServerRest:/BHUData/apps/msip_bhu_unit_agent/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_agent/lib/msip_*.jar       root@$Deploy2WebAgentServerRest:/BHUData/apps/msip_bhu_unit_agent/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_agent/classes/com/         root@$Deploy2WebAgentServerRest:/BHUData/apps/msip_bhu_unit_agent/classes/com/
echo 'deploy msip_bhu_unit_agent successfully @'$Deploy2WebAgentServerRest

echo '发布业务组件成功'$Deploy2WebAgentServerRest


echo '准备发布其他服务到'$Deploy2WebAgentServerRest

echo 'deploy msip_bhu_backend_online to ...@'$Deploy2WebAgentServerRest
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_online/lib/spring*.RELEASE.jar  	root@$Deploy2WebAgentServerRest:/BHUData/apps/msip_bhu_backend_online/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_online/lib/msip_*.jar  	root@$Deploy2WebAgentServerRest:/BHUData/apps/msip_bhu_backend_online/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_backend_online/classes/com/ 		root@$Deploy2WebAgentServerRest:/BHUData/apps/msip_bhu_backend_online/bin/com/
echo 'deploy msip_bhu_backend_online successfully @'$Deploy2WebAgentServerRest

echo 'deploy msip_bhu_dataimport to ...@'$Deploy2WebAgentServerRest
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_dataimport/lib/spring*.RELEASE.jar  		root@$Deploy2WebAgentServerRest:/BHUData/apps/msip_bhu_dataimport/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_dataimport/lib/msip_*.jar  		root@$Deploy2WebAgentServerRest:/BHUData/apps/msip_bhu_dataimport/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_dataimport/classes/com/ 			root@$Deploy2WebAgentServerRest:/BHUData/apps/msip_bhu_dataimport/bin/com/
echo 'deploy msip_bhu_dataimport successfully @'$Deploy2WebAgentServerRest
echo '发布其他服务成功'$Deploy2WebAgentServerRest

echo '发布RestAPI'$Deploy2WebAgentServerRest
echo 'deploy msip_bhu_agent_rest to ...@'$Deploy2WebAgentServerRest
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_agent_rest/WEB-INF/lib/spring*.RELEASE.jar    root@$Deploy2WebAgentServerRest:/BHUData/apps/msip_bhu_agent_rest/WEB-INF/lib/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_agent_rest/WEB-INF/lib/msip_*.jar   root@$Deploy2WebAgentServerRest:/BHUData/apps/msip_bhu_agent_rest/WEB-INF/lib/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_agent_rest/WEB-INF/classes/com/     root@$Deploy2WebAgentServerRest:/BHUData/apps/msip_bhu_agent_rest/WEB-INF/classes/com/
echo 'deploy msip_bhu_agent_rest successfully @'$Deploy2WebAgentServerRest

echo '发布RestAPI'

