
cd `dirname $0`
Current_DIR=`pwd`
echo $Current_DIR

Deploy2WebServerRest='123.56.76.163'
DeployUser=$1

read -n1 -p "Do you want to deploy 2 $Deploy2ComponentServerSabo(Remote Payment Production) [Y/N]?"
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

echo "starting deploy2=>"$Deploy2WebServerRest
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
echo '拷贝文件 msip_bhu_rest.war到'$CuDateDir
cp ../../msip-bhu-payment/msip_bhu_payment_rest/target/msip_bhu_payment_rest.war ./$CuDateDir

cd $CuDateDir
echo '进行文件解压过程'
unzip -qo msip_bhu_payment_rest.war -d msip_bhu_payment_rest

echo '文件解压过程成功'

echo 'deploy msip_bhu_payment_rest to ...@'$Deploy2WebServerRest
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_payment_rest/WEB-INF/lib/spring*.RELEASE.jar  	root@$Deploy2WebServerRest:/BHUData/apps/msip_bhu_payment_rest/WEB-INF/lib/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_payment_rest/WEB-INF/lib/msip_*.jar  	root@$Deploy2WebServerRest:/BHUData/apps/msip_bhu_payment_rest/WEB-INF/lib/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_payment_rest/WEB-INF/classes/com/ 		root@$Deploy2WebServerRest:/BHUData/apps/msip_bhu_payment_rest/WEB-INF/classes/com/
echo 'deploy msip_bhu_payment_rest successfully @'$Deploy2WebServerRest
echo '发布其他服务成功'

