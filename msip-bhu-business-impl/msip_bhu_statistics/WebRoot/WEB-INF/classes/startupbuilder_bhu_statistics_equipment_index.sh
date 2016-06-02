
echo STARTUP App
echo 设置环境变量,循环当前目录下的lib目录下所有jar文件,并设置CLASSPATH

export LIB_PATH=/BHUData/apps/msip_bhu_statistics/WebRoot/WEB-INF/lib;

TMPCLASSPAT='';

for i in $(ls $LIB_PATH/*.jar) ; do
	TMPCLASSPATH=$i:$TMPCLASSPATH
done
echo $TMPCLASSPATH

echo 当前目录下的bin目录为class文件存放目录,设置bin目录到CLASSPATH中

export CLASSPATH=$TMPCLASSPATH:./classes:

echo 显示CLASSPATH
echo $CLASSPATH
echo 运行应用程序
java  -Xms1024m  -Xmx1024m com.bhu.statistics.util.EquipmentStatics |/usr/local/sbin/cronolog /BHUData/logs/statistics/builder_bhu_statistics_index.out.%Y-%m-%d >> /dev/null &

