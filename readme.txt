描述：
msip-core		core代码库
	msip_core 				核心代码
	msip_core_cachedext		缓存机制代码
msip-business	业务代码库
	msip-wecite  wecite业务工程代码
		msip-wecite-business
			msip_wecite_rpc_api
			msip_wecite_i18n
			...
		msip-wecite-api
			msip_wecite_rest
local_repo		本地使用jar库
msip-plugins	基于core扩展使用的插件

A、编译步骤msip-core：
cd ${code.path}/msip-core
step 1: mvn package -Dmaven.test.skip
step 2: mvn eclipse:eclipse

B、编译步骤msip-business：
cd ${code.path}/msip-business/msip-wecite
step 1: mvn package -Dmaven.test.skip
step 2: mvn eclipse:eclipse


当本地core代码有更新后的操作步骤：
1、如果msip_core代码修改后，需要重新编译，进行步骤A，然后找到msip_core生成的相关jar，覆盖到指定的local_repo中
2、删除.m2的相关jar文件或目录
3、进行步骤B

以下步骤忽略
#mvn install -Dmaven.test.skip=true (把生成的jar打包到本地 .m2/repository/{groupId})
#拷贝 .m2/repository/{groupId}的内容到local_repo
#把local_repo中的{groupId}内容更新，上传到git中

或者：通过
mvn install:install-file -DgroupId=com.smartwork -DartifactId=msip_core -Dversion=3.0-SNAPSHOT -Dfile=./target/msip_core-3.0-SNAPSHOT.jar -Dpackaging=jar -DgeneratePom=true