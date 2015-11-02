
echo STARTUP App
echo 设置环境变量,循环当前目录下的lib目录下所有jar文件,并设置CLASSPATH

export LIB_PATH=./libs;

TMPCLASSPAT='';

for i in $(ls $LIB_PATH/*.jar) ; do
	TMPCLASSPATH=$i:$TMPCLASSPATH
done
echo $TMPCLASSPATH

echo 当前目录下的bin目录为class文件存放目录,设置bin目录到CLASSPATH中

export CLASSPATH=$TMPCLASSPATH:./bin:

echo 显示CLASSPATH
echo $CLASSPATH
echo 运行应用程序
java  -Xms2g  -Xmx2g com.bhu.vas.di.op.charging.MonthlyChargingSettlementGen4AgentOper $1 | cronolog /BHUData/logs/dataimport/builder_bhu_agent_settlement_gen.out.%Y-%m-%d >> /dev/null &
