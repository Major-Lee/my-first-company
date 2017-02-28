
cd `dirname $0`
Current_DIR=`pwd`
echo $Current_DIR

Deploy2Server=123.56.79.210
Deploy2ServerWeb=123.56.79.210

read -n1 -p "Do you want to deploy 2 $Deploy2Server(Local Gome Test) [Y/N]?"
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


echo "starting deploy2=>"$Deploy2Server
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
echo '拷贝文件 msip_bhu_unit_thirdparty-bin.zip到'$CuDateDir
cp ../../msip-bhu-unit/msip_bhu_unit_thirdparty/target/msip_bhu_unit_thirdparty-bin.zip ./$CuDateDir


echo '拷贝文件 msip_bhu_thirdparty_rest.war到'$CuDateDir
cp ../../msip-bhu-api/msip_bhu_thirdparty_rest/target/msip_bhu_thirdparty_rest.war ./$CuDateDir


cd $CuDateDir
echo '进行文件解压过程'
unzip -q msip_bhu_unit_thirdparty-bin.zip
unzip -qo msip_bhu_unit_thirdparty/bin/msip_bhu_unit_thirdparty.jar -d msip_bhu_unit_thirdparty/classes/

unzip -qo msip_bhu_thirdparty_rest.war -d msip_bhu_thirdparty_rest

echo '文件解压过程成功'

echo '准备发布业务组件到'$Deploy2Server


echo 'deploy msip_bhu_unit_thirdparty to ...@'$Deploy2Server
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_thirdparty/lib/spring*.RELEASE.jar   root@$Deploy2Server:/BHUData/apps/msip_bhu_unit_thirdparty/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_thirdparty/lib/msip_*.jar    root@$Deploy2Server:/BHUData/apps/msip_bhu_unit_thirdparty/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_thirdparty/classes/com/    root@$Deploy2Server:/BHUData/apps/msip_bhu_unit_thirdparty/classes/com/
echo 'deploy msip_bhu_unit_thirdparty successfully @'$Deploy2Server


echo 'deploy msip_bhu_thirdparty_rest to ...@'$Deploy2ServerWeb
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_thirdparty_rest/WEB-INF/lib/spring*.RELEASE.jar    root@$Deploy2ServerWeb:/BHUData/apps/msip_bhu_thirdparty_rest/WEB-INF/lib/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_thirdparty_rest/WEB-INF/lib/msip_*.jar   root@$Deploy2ServerWeb:/BHUData/apps/msip_bhu_thirdparty_rest/WEB-INF/lib/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_thirdparty_rest/WEB-INF/classes/com/     root@$Deploy2ServerWeb:/BHUData/apps/msip_bhu_thirdparty_rest/WEB-INF/classes/com/
echo 'deploy msip_bhu_thirdparty_rest successfully @'$Deploy2ServerWeb

