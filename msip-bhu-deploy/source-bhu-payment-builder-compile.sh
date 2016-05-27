cd `dirname $0`
Current_DIR=`pwd`
echo $Current_DIR
#提取最新代码
./source-java-git.pull.sh
#进入bhu business repository 进行编译
cd ..
#mvn clean
mvn clean package -f pom-payment.xml -Dmaven.test.skip  
echo '代码编译并打包成功'




