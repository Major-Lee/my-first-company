
cd /BHUData/ftp/static

echo 'deploy msip_bhu_rest to ...@'182.92.229.26
rsync -avz -progress -e 'ssh -p 22'  ./*  	root@182.92.229.26:/BHUData/static/*
echo 'deploy msip_bhu_rest successfully @'182.92.229.26
echo '发布其他服务成功'

