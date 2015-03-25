cd `dirname $0`
Current_DIR=`pwd`
echo $Current_DIR
#提取最新代码
./source-java-git.pull.sh
#进入bhu business repository 进行编译
cd ..
mvn package -Dmaven.test.skip
#回到msip-bhu-deploy目录进入deploy目录，并且创建每日的预发布文件存储目录
cd msip-bhu-deploy
cd deploy
CuDateDir=`date +%Y%m%d`

if [ ! -d $CuDateDir ]; then
	mkdir $CuDateDir
fi
#清除目录$CuDateDir下所有的文件
rm -rf ./$CuDateDir/*
#拷贝生成的zip包到发布目录中。。。
cp ../../msip-bhu-unit/msip_bhu_unit_input_processor/target/msip_bhu_unit_input_processor-bin.zip ./$CuDateDir
cp ../../msip-bhu-unit/msip_bhu_unit_daemon_processor/target/msip_bhu_unit_daemon_processor-bin.zip ./$CuDateDir
cp ../../msip-bhu-unit/msip_bhu_unit_devices/target/msip_bhu_unit_devices-bin.zip ./$CuDateDir


cd $CuDateDir

unzip msip_bhu_unit_input_processor-bin.zip
unzip msip_bhu_unit_daemon_processor-bin.zip
unzip msip_bhu_unit_devices-bin.zip

#unzip -o test.zip -d tmp/
unzip -o msip_bhu_unit_input_processor/bin/msip_bhu_unit_input_processor.jar -d msip_bhu_unit_input_processor/classes/



