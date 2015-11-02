cd "/BHUData/apps/msip_bhu_dataimport"
echo `pwd`
PreviousMonthDate=`date -d '-1 month' +%Y-%m`
echo $PreviousMonthDate
sh startupbuilder_agent_settlement_gen.sh.sh $PreviousMonthDate
