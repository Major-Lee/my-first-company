
cd `dirname $0`
Current_DIR=`pwd`
echo $Current_DIR

Deploy2Server=$1
#回到msip-bhu-deploy目录进入deploy目录，并且创建每日的预发布文件存储目录
#cd msip-bhu-deploy
if [ ! -d `deploy` ]; then
	mkdir `deploy`
fi
cd deploy

CuDateDir=`date +%Y%m%d%H`

if [ ! -d $CuDateDir ]; then
	mkdir $CuDateDir
fi
echo '清除目录'$CuDateDir'下所有的文件'
rm -rf ./$CuDateDir/*
echo '清除目录'$CuDateDir'下所有的文件成功'
#拷贝生成的zip包到发布目录中。。。
echo '拷贝文件 msip_bhu_dataimport-bin.zip到'$CuDateDir
cp ../../msip-bhu-business-impl/msip_bhu_dataimport/target/msip_bhu_dataimport-bin.zip ./$CuDateDir


cd $CuDateDir
echo '进行文件解压过程'


unzip -q msip_bhu_dataimport-bin.zip
unzip -qo msip_bhu_dataimport/bin/msip_bhu_dataimport.jar -d msip_bhu_dataimport/classes/

echo '文件解压过程成功'

echo '准备发布业务组件到'$Deploy2Server

echo 'deploy msip_bhu_dataimport to ...@'$Deploy2Server
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_dataimport/lib/spring*-4.1.2.RELEASE.jar  		root@$Deploy2Server:/BHUData/apps/msip_bhu_dataimport/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_dataimport/lib/msip_*.jar  		root@$Deploy2Server:/BHUData/apps/msip_bhu_dataimport/libs/
rsync -avz -progress -e 'ssh -p 22'  ./msip_bhu_dataimport/classes/com/ 		root@$Deploy2Server:/BHUData/apps/msip_bhu_dataimport/bin/com/
echo 'deploy msip_bhu_dataimport successfully @'$Deploy2Server
