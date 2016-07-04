
cd `dirname $0`
Current_DIR=`pwd`
echo $Current_DIR

#Deploy2Server=$1
Deploy2input0=192.168.66.147
Deploy2input1=192.168.66.123
Deploy2daemon0=192.168.66.191
##if read -t 5 -p "Confirm to deploy 2 $Deploy2Server(Local Test):(yes/no)"
#then
#  echo "hello $REPLY, welcome to come back here"
#else
#  echo "sorry , you are too slow "
#  exit
#fi

read -n1 -p "Do you want to deploy 2 $Deploy2Server(Local Kafka Test) [Y/N]?"
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


echo "starting deploy2=>"$Deploy2input0
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



cd $CuDateDir
echo '进行文件解压过程'
unzip -q msip_bhu_unit_input_processor-bin.zip
unzip -qo msip_bhu_unit_input_processor/bin/msip_bhu_unit_input_processor.jar -d msip_bhu_unit_input_processor/classes/
unzip -q msip_bhu_unit_daemon_processor-bin.zip
unzip -qo msip_bhu_unit_daemon_processor/bin/msip_bhu_unit_daemon_processor.jar -d msip_bhu_unit_daemon_processor/classes/

echo '文件解压过程成功'

echo '准备发布业务组件到'$Deploy2input0

#echo 'deploy msip_bhu_unit_input_processor to ...@'$Deploy2Server
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_input_processor/lib/spring*.RELEASE.jar   root@$Deploy2Server:/BHUData/apps/msip_bhu_unit_input_processor/libs/
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_input_processor/lib/msip_*.jar    root@$Deploy2Server:/BHUData/apps/msip_bhu_unit_input_processor/libs/
#rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_input_processor/classes/com/    root@$Deploy2Server:/BHUData/apps/msip_bhu_unit_input_processor/classes/com/
#echo 'deploy msip_bhu_unit_input_processor successfully @'$Deploy2Server

echo 'deploy msip_bhu_unit_input_processor to ...@'$Deploy2input0
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_input_processor/lib/spring*.RELEASE.jar   root@$Deploy2input0:/BHUData/apps/msip_bhu_unit_input_processor/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_input_processor/lib/msip_*.jar    root@$Deploy2input0:/BHUData/apps/msip_bhu_unit_input_processor/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_input_processor/classes/com/    root@$Deploy2input0:/BHUData/apps/msip_bhu_unit_input_processor/classes/com/
echo 'deploy msip_bhu_unit_input_processor successfully @'$Deploy2input0


echo '准备发布业务组件到'$Deploy2input1

echo 'deploy msip_bhu_unit_input_processor to ...@'$Deploy2input1
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_input_processor/lib/spring*.RELEASE.jar   root@$Deploy2input1:/BHUData/apps/msip_bhu_unit_input_processor/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_input_processor/lib/msip_*.jar    root@$Deploy2input1:/BHUData/apps/msip_bhu_unit_input_processor/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_input_processor/classes/com/    root@$Deploy2input1:/BHUData/apps/msip_bhu_unit_input_processor/classes/com/
echo 'deploy msip_bhu_unit_input_processor successfully @'$Deploy2input1

echo '准备发布业务组件到'$Deploy2daemon0

echo 'deploy msip_bhu_unit_daemon_processor to ...@'$Deploy2daemon0
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_daemon_processor/lib/spring*.RELEASE.jar root@$Deploy2daemon0:/BHUData/apps/msip_bhu_unit_daemon_processor/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_daemon_processor/lib/msip_*.jar  root@$Deploy2daemon0:/BHUData/apps/msip_bhu_unit_daemon_processor/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_unit_daemon_processor/classes/com/    root@$Deploy2daemon0:/BHUData/apps/msip_bhu_unit_daemon_processor/classes/com/
echo 'deploy msip_bhu_unit_daemon_processor successfully @'$Deploy2daemon0


