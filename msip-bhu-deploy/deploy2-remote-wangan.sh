
cd `dirname $0`
Current_DIR=`pwd`
echo $Current_DIR

Deploy2Server=123.56.79.210

read -n1 -p "Do you want to deploy 2 $Deploy2Server(Local WangAn Test) [Y/N]?"
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
echo '拷贝文件 msip_bhu_backend_wangan-bin.zip到'$CuDateDir
cp ../../msip-bhu-business-impl/msip_bhu_backend_wangan/target/msip_bhu_backend_wangan-bin.zip ./$CuDateDir

cd $CuDateDir
echo '进行文件解压过程'
unzip -q msip_bhu_backend_wangan-bin.zip
unzip -qo msip_bhu_backend_wangan/bin/msip_bhu_backend_wangan.jar -d msip_bhu_backend_wangan/classes/

echo '文件解压过程成功'

echo '准备发布业务组件到'$Deploy2Server


echo 'deploy msip_bhu_backend_wangan to ...@'$Deploy2Server
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_backend_wangan/lib/spring*.RELEASE.jar   root@$Deploy2Server:/BHUData/apps/msip_bhu_backend_wangan/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_backend_wangan/lib/msip_*.jar    root@$Deploy2Server:/BHUData/apps/msip_bhu_backend_wangan/libs/
rsync -avz -progress -e 'ssh -p 65008'  ./msip_bhu_backend_wangan/classes/com/    root@$Deploy2Server:/BHUData/apps/msip_bhu_backend_wangan/bin/com/
echo 'deploy msip_bhu_backend_wangan successfully @'$Deploy2Server



