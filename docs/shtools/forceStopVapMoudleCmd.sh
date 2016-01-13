#!/bin/bash
TargetFile=$1
while read line 
do
  echo "正在下发增值模板关闭指令...$line"
  curl -H "A-Token-Header: JTNGUlJXAUNKGxFdWlYM" -d "mac=$line&uid=3&&opt=151&subopt=26&channel=VAS" "http://vap.bhunetworks.com/bhu_api/v1/cmd/generate"
  echo "$line send ok!"
  sleep 1m
done < $TargetFile

#Mass Ap from sql
#select * from t_wifi_devices where hdtype in ('H103','H110','H201','H303') and orig_vap_module is not null and orig_swver like '%1.3.2%' and orig_swver like '%TS'

#uRouter from sql
#select * from t_wifi_devices where orig_model = 'uRouter' and orig_vap_module is not null and orig_swver like '%TU'
