echo newworld123.
CuDateDir=`date +%Y%m%d`
echo $CuDateDir

echo "stop 192.168.66.188 input components"
ssh -p 22 root@192.168.66.188 '/BHUData/appsh/stop.sh'

echo "stop 192.168.66.155 input components"
ssh -p 22 root@192.168.66.155 '/BHUData/appsh/stop.sh'

echo "restart 192.168.66.162 components"
ssh -p 22 root@192.168.66.162 '/BHUData/appsh/restart.sh'
sleep 5

echo "stop 192.168.66.188 components"
ssh -p 22 root@192.168.66.188 '/BHUData/appsh/restart.sh'
sleep 5

echo "stop 192.168.66.155 components"
ssh -p 22 root@192.168.66.155 '/BHUData/appsh/restart.sh'
sleep 5

echo "restart 192.168.66.188 input components"
ssh -p 22 root@192.168.66.188 '/BHUData/appsh/start.sh'

echo "restart 192.168.66.155 input components"
ssh -p 22 root@192.168.66.155 '/BHUData/appsh/start.sh'



sleep 5
echo "restart 192.168.66.7 web"
ssh -p 22 root@192.168.66.7 '/usr/local/tomcats/stop_tomcats.sh'
ssh -p 22 root@192.168.66.7 '/usr/local/tomcats/startup_tomcats.sh'

