echo "开始拷贝文件"
sh /BHUData/appsh/prepareRemoteLogs.sh
echo "开始拷贝文件结束"
cd "/BHUData/apps/msip_bhu_dataimport"
echo `pwd`
YesterdayDate=`date -d yesterday +%Y-%m-%d`
Chargingsimulogs="/BHUData/bulogs/copylogs/%s/chargingsimulogs/"
Charginglogs="/BHUData/bulogs/copylogs/%s/charginglogs/"
echo $YesterdayDate
echo $Chargingsimulogs
echo $Charginglogs
sh startupbuilder_agentlog_import.sh $YesterdayDate $Chargingsimulogs $Charginglogs
