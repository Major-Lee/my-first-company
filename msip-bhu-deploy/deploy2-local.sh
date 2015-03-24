
echo newworld123.
CuDateDir=`date +%Y%m%d`
echo $CuDateDir
BusinessDir="whisper"
echo $BusinessDir
echo deploy to luffy:192.168.1.106 22 server msip_whisper_rest ...
rm -rf /VCData/apps/msip_whisper_rest/WEB-INF/lib/msip_*.jar
rm -rf /VCData/apps/msip_whisper_rest/WEB-INF/classes/com
cp -f -a ./deploy/$CuDateDir/$BusinessDir/msip_whisper_rest/WEB-INF/lib/msip_*.jar /VCData/apps/msip_whisper_rest/WEB-INF/lib/
cp -f -a ./deploy/$CuDateDir/$BusinessDir/msip_whisper_rest/WEB-INF/classes/com/ /VCData/apps/msip_whisper_rest/WEB-INF/classes/com/
#rsync -avz -progress -e 'ssh -p 22'  ./deploy/$CuDateDir/msip_naola_rest/WEB-INF/lib/msip_*.jar  	root@192.168.1.2:/VCData/apps/msip_naola_rest/WEB-INF/lib/
#rsync -avz -progress -e 'ssh -p 22'  ./deploy/$CuDateDir/msip_naola_rest/WEB-INF/classes/com/ 		root@192.168.1.2:/VCData/apps/msip_naola_rest/WEB-INF/classes/com/

echo deploy to luffy:192.168.1.106 server  msip_whisper_rest successfully

echo deploy to luffy:192.168.1.106 22 server msip_whisper_dataimport ...
rm -rf /VCData/apps/msip_whisper_dataimport/libs/msip_*.jar
rm -rf /VCData/apps/msip_whisper_dataimport/bin/com/
cp -f -a ./deploy/$CuDateDir/$BusinessDir/msip_whisper_dataimport/libs/msip_*.jar  /VCData/apps/msip_whisper_dataimport/libs/
cp -f -a ./deploy/$CuDateDir/$BusinessDir/msip_whisper_dataimport/bin/com/ /VCData/apps/msip_whisper_dataimport/bin/com/
#rsync -avz -progress -e 'ssh -p 22'  ./deploy/$CuDateDir/msip_dataimport/libs/msip_*.jar   root@192.168.1.2:/VCData/apps/msip_dataimport/libs/
#rsync -avz -progress -e 'ssh -p 22'  ./deploy/$CuDateDir/msip_dataimport/bin/com/          root@192.168.1.2:/VCData/apps/msip_dataimport/bin/com/
echo deploy to luffy:192.168.1.106 22 server msip_whisper_dataimport successfully


echo deploy to luffy:192.168.1.106 22 server msip_whisper_backend_online ...
rm -rf /VCData/apps/msip_whisper_backend_online/libs/msip_*.jar
rm -rf /VCData/apps/msip_whisper_backend_online/bin/com/
cp -f -a ./deploy/$CuDateDir/$BusinessDir/msip_whisper_backend_online/libs/msip_*.jar  /VCData/apps/msip_whisper_backend_online/libs/
cp -f -a ./deploy/$CuDateDir/$BusinessDir/msip_whisper_backend_online/bin/com/ /VCData/apps/msip_whisper_backend_online/bin/com/
#rsync -avz -progress -e 'ssh -p 22'  ./deploy/$CuDateDir/msip_backendapp/libs/msip_*.jar   root@192.168.1.2:/VCData/apps/msip_backendapp/libs/
#rsync -avz -progress -e 'ssh -p 22'  ./deploy/$CuDateDir/msip_backendapp/bin/com/          root@192.168.1.2:/VCData/apps/msip_backendapp/bin/com/
echo deploy to luffy:192.168.1.106 22 server msip_backendapp successfully

echo deploy to luffy:192.168.1.106 22 server msip_whisper_backend_push ...
rm -rf /VCData/apps/msip_whisper_backend_push/libs/msip_*.jar
rm -rf /VCData/apps/msip_whisper_backend_push/bin/com/
cp -f -a ./deploy/$CuDateDir/$BusinessDir/msip_whisper_backend_push/libs/msip_*.jar  /VCData/apps/msip_whisper_backend_push/libs/
cp -f -a ./deploy/$CuDateDir/$BusinessDir/msip_whisper_backend_push/bin/com/ /VCData/apps/msip_whisper_backend_push/bin/com/
#rsync -avz -progress -e 'ssh -p 22'  ./deploy/$CuDateDir/msip_backendapp/libs/msip_*.jar   root@192.168.1.2:/VCData/apps/msip_backendapp/libs/
#rsync -avz -progress -e 'ssh -p 22'  ./deploy/$CuDateDir/msip_backendapp/bin/com/          root@192.168.1.2:/VCData/apps/msip_backendapp/bin/com/
echo deploy to luffy:192.168.1.106 22 server msip_whisper_backend_push successfully

echo deploy to luffy:192.168.1.106 22 server msip_whisper_backend_incrementindex ...
rm -rf /VCData/apps/msip_whisper_backend_incrementindex/libs/msip_*.jar
rm -rf /VCData/apps/msip_whisper_backend_incrementindex/bin/com/
cp -f -a ./deploy/$CuDateDir/$BusinessDir/msip_whisper_backend_incrementindex/libs/msip_*.jar  /VCData/apps/msip_whisper_backend_incrementindex/libs/
cp -f -a ./deploy/$CuDateDir/$BusinessDir/msip_whisper_backend_incrementindex/bin/com/ /VCData/apps/msip_whisper_backend_incrementindex/bin/com/
#rsync -avz -progress -e 'ssh -p 22'  ./deploy/$CuDateDir/msip_backendapp/libs/msip_*.jar   root@192.168.1.2:/VCData/apps/msip_backendapp/libs/
#rsync -avz -progress -e 'ssh -p 22'  ./deploy/$CuDateDir/msip_backendapp/bin/com/          root@192.168.1.2:/VCData/apps/msip_backendapp/bin/com/
echo deploy to luffy:192.168.1.106 22 server msip_whisper_backend_incrementindex successfully


echo deploy to luffy:192.168.1.106 22 server msip_im_cm ...
rm -rf /VCData/apps/msip_im_cm/libs/msip_*.jar
rm -rf /VCData/apps/msip_im_cm/bin/com/
cp -f -a ./deploy/$CuDateDir/$BusinessDir/msip_core_plugins_im_cm/libs/msip_*.jar  /VCData/apps/msip_im_cm/libs/
cp -f -a ./deploy/$CuDateDir/$BusinessDir/msip_core_plugins_im_cm/bin/com/ /VCData/apps/msip_im_cm/bin/com/
echo deploy to luffy:192.168.1.106 22 server msip_im_cm successfully


echo deploy to luffy:192.168.1.106 22 server msip_im_dispatcher ...
rm -rf /VCData/apps/msip_im_dispatcher/libs/msip_*.jar
rm -rf /VCData/apps/msip_im_dispatcher/bin/com/
cp -f -a ./deploy/$CuDateDir/$BusinessDir/msip_core_plugins_im_dispatcher/libs/msip_*.jar  /VCData/apps/msip_im_dispatcher/libs/
cp -f -a ./deploy/$CuDateDir/$BusinessDir/msip_core_plugins_im_dispatcher/bin/com/ /VCData/apps/msip_im_dispatcher/bin/com/
echo deploy to luffy:192.168.1.106 22 server msip_im_dispatcher successfully

#echo "stop tomcat"
#ps aux |grep java |grep tomcat |grep -v grep |grep -v tail |awk '{print $2}' |xargs kill -9

#echo "stop memcached"
#ps aux |grep mem |grep -v grep |grep -v tail |awk '{print $2}' |xargs kill -9

#echo "5.1 Restart remote memcached"
#/usr/local/memcached/bin/memcached -d -m 64 -u root -l 127.0.0.1 -p 11211 -c 1024 -P /usr/local/memcached/memcached_11211.pid
#/usr/local/memcached/bin/memcached -d -m 64 -u root -l 127.0.0.1 -p 11212 -c 1024 -P /usr/local/memcached/memcached_11212.pid

#echo "5.2 Restart remote tomcat"
#/usr/local/apache-tomcat-6.0.39/bin/startup.sh
