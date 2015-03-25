cd `dirname $0`
Current_DIR=`pwd`
echo $Current_DIR

cd ..
mvn package -Dmaven.test.skip

cd msip-bhu-deploy
cd deploy
CuDateDir=`date +%Y%m%d`

if [ ! -d $CuDateDir ]; then
	mkdir $CuDateDir
fi
#拷贝生成的zip包到发布目录中。。。
cp ../../msip-bhu-unit/msip_bhu_unit_input_processor/target/msip_bhu_unit_input_processor-bin.zip ./$CuDateDir
cp ../../msip-bhu-unit/msip_bhu_unit_daemon_processor/target/msip_bhu_unit_daemon_processor-bin.zip ./$CuDateDir
cp ../../msip-bhu-unit/msip_bhu_unit_devices/target/msip_bhu_unit_devices-bin.zip ./$CuDateDir


cd $CuDateDir

unzip msip_bhu_unit_input_processor-bin.zip
unzip msip_bhu_unit_daemon_processor-bin.zip
unzip msip_bhu_unit_devices-bin.zip

unzip msip_bhu_unit_input_processor/bin/msip_bhu_unit_input_processor.jar



