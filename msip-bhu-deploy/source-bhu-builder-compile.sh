cd ..

mvn package -Dmaven.test.skip

cd msip-bhu-deploy
cd deploy
CuDateDir=`date +%Y%m%d`

mkdirs $CuDateDir

cp ../msip-bhu-unit/msip_bhu_unit_input_processor/target/msip_bhu_unit_input_processor-bin.zip .





