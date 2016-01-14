#!/bin/bash
TargetFile=$1
while read line 
do
  echo "正在下发增值模板下发指令...$line"
  curl -H "A-Token-Header: JTNGUlJXAUNKGxFdWlYM" -d "mac=$line&uid=3&&opt=201&subopt=00&extparams=<dev><sys><config><ITEM sequence=\"-1\" /></config></sys><wifi><vap><ITEM name=\"wlan0\" auth=\"WPA2-PSK\"/></vap></wifi></dev>&channel=VAS" "http://vap.bhunetworks.com/bhu_api/v1/cmd/generate"
  echo "$line send ok!"
  sleep 2m
done < $TargetFile

#Mass Ap from sql
#select * from t_wifi_devices where hdtype in ('H103','H110','H201','H303') and orig_vap_module is not null and orig_swver like '%1.3.2%' and orig_swver like '%TS'

#uRouter from sql
#select * from t_wifi_devices where orig_model = 'uRouter' and orig_vap_module is not null and orig_swver like '%TU'
