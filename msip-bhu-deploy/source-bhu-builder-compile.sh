cd `dirname $0`
Current_DIR=`pwd`
echo $Current_DIR
#提取最新代码
./source-java-git.pull.sh
#进入bhu business repository 进行编译
cd ..
mvn clean
mvn package -Dmaven.test.skip
echo '代码编译并打包成功'
#unzip -o test.zip -d tmp/
#unzip -o msip_bhu_unit_input_processor/bin/msip_bhu_unit_input_processor.jar -d msip_bhu_unit_input_processor/classes/



