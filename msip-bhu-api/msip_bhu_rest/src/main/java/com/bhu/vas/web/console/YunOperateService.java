package com.bhu.vas.web.console;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

@Service
public class YunOperateService {

	// 七牛云参数
	static final String QN_ACCESS_KEY = "p6XNq4joNqiFtqJ9EFWdyvnZ6ZBnuwISxvVGdHZg";
	static final String QN_SECRET_KEY = "edcDVKq1YESjRCk_h5aBx2jqb-rtmcrmwBEBH8-z";
	static final String QN_BUCKET_URL_FW = "http://7xq32u.com2.z0.glb.qiniucdn.com/@";
	static final String QN_BUCKET_URL_OM = "http://7xq3nw.com2.z0.glb.qiniucdn.com/@";
	static final String QN_BUCKET_NAME_FW = "devicefw";
	static final String QN_BUCKET_NAME_OM = "deviceom";
	// 阿里云参数
	static final String AL_ACCESS_KEY = "stYL3FtcjOTmvyA4";
	static final String AL_SECRET_KEY = "aicFwcLeEx397kfVQB7OelSV4iqSON";
	static final String AL_BUCKET_NAME_FW = "devicefw";
	static final String AL_BUCKET_NAME_OM = "deviceom";
	static final String AL_END_POINT = "oss-cn-beijing.aliyuncs.com";
	
	/**
	 * 七牛云上传
	 * 
	 * @param file
	 * @param remoteName
	 *            上传到七牛云之后的文件名称
	 * @param bucketName
	 *            库名字
	 * @throws QiniuException
	 */
	public void uploadFile2QN(byte[] bs, String dut, String fileName, boolean fw,String JsFilePath) throws Exception {

			Auth auth = Auth.create(QN_ACCESS_KEY, QN_SECRET_KEY);
			UploadManager uploadManager = new UploadManager();
			if (fw) {
				uploadManager.put(bs, "/" + dut + "/build/" + fileName, auth.uploadToken(QN_BUCKET_NAME_FW));
			}
			if (!fw) {
				uploadManager.put(bs, "/" + getRemoteName(fileName)+"/" + fileName, auth.uploadToken(QN_BUCKET_NAME_OM));
				uploadManager.put(JsFilePath, "/" + getRemoteName(fileName)+"/" +"version.js", auth.uploadToken(QN_BUCKET_NAME_OM));
				
			}
			System.out.println("七牛云上传成功。");
	}

	/**
	 * 阿里云上传
	 * 
	 * @param bs
	 * @param remotePath
	 * @return
	 * @throws Exception
	 */
	public void uploadFile2AL(byte[] bs, String dut, String fileName, boolean fw,String JsFilePath) throws Exception {
		
			ByteArrayInputStream in = new ByteArrayInputStream(bs);
			OSSClient ossClient = new OSSClient(AL_END_POINT, AL_ACCESS_KEY, AL_SECRET_KEY);
			// 创建上传Object的Metadata
			ObjectMetadata objectMetadata = new ObjectMetadata();
			// 上传文件
			if (fw) {
				ossClient.putObject(AL_BUCKET_NAME_FW, dut + "/build/" + fileName, in, objectMetadata);
			}
			if (!fw) {
				ossClient.putObject(AL_BUCKET_NAME_OM, getRemoteName(fileName)+"/" +fileName, in, objectMetadata);
				File file = new File(JsFilePath);
				ossClient.putObject(AL_BUCKET_NAME_OM, getRemoteName(fileName)+"/" +"version.js", file, objectMetadata);
			}
			System.out.println("阿里云上传成功");
			//删除临时缓存文件夹
			deleteCatchBucket(JsFilePath);
		}

	/**
	 * 删除云备份文件
	 * @param fileName 文件名
	 * @param dut 版本号
	 * @param fw  判断是固件版本还是模块版本
	 */
	public  void deleteFile(String fileName,String dut,boolean fw) {
		try {

			OSSClient ossClient = new OSSClient(AL_END_POINT, AL_ACCESS_KEY, AL_SECRET_KEY);
			Auth auth = Auth.create(QN_ACCESS_KEY, QN_SECRET_KEY);
			BucketManager bucketManager = new BucketManager(auth);
			if(fw){
				//阿里云删除			
				ossClient.deleteObject(AL_BUCKET_NAME_FW, dut + "/build/" + fileName);
				System.out.println("阿里云删除成功:"+dut + "/build/" + fileName);
				//七牛云删除
				bucketManager.delete(QN_BUCKET_NAME_FW,"/" + dut + "/build/" + fileName);
				System.out.println("七牛云删除成功:"+"/" + dut + "/build/" + fileName);
			}
			if(!fw){
				//阿里云删除			
				ossClient.deleteObject(AL_BUCKET_NAME_OM, getRemoteName(fileName)+"/" +fileName);
				ossClient.deleteObject(AL_BUCKET_NAME_OM, getRemoteName(fileName)+"/" +"version.js");
				//七牛云删除
				bucketManager.delete(QN_BUCKET_NAME_OM,"/" + getRemoteName(fileName)+"/" + fileName);	
				bucketManager.delete(QN_BUCKET_NAME_OM,"/" + getRemoteName(fileName)+"/" +"version.js");	
			}
			System.out.println("删除成功。");
		} catch (Exception e) {
			System.out.println("删除失败。");
			e.printStackTrace();
		}
	}

	/**
	 * 上传OM文件时，本地创建version.js文件
	 * @param fileFullName 加后缀的全名
	 * @return js文件路径
	 */
	 String getFilePath(String fileFullName) {
		
		String fileName = fileFullName.substring(0, fileFullName.lastIndexOf("."));
		String str = "jcb(\n\t\t{\"name\":\""+fileName+"\", \"url\":\""+fileFullName+"\"}\n)";
		String filePath = null;
		try{
			String CacheBucketPath = "/BHUData/uploadOMCache/" + System.currentTimeMillis();
			File fileBucket = new File(CacheBucketPath);
			fileBucket.mkdir();
			filePath = CacheBucketPath + "/version.js";
			File file = new File(filePath);
			FileWriter fw = new FileWriter(file);
			fw.write(str);
			fw.flush();
			fw.close();
		}catch(IOException e){
			System.out.println("创建文件夹错误");
			e.printStackTrace();
		}
		return filePath;
	}
	
	
	/**
	 * 根据文件名获取remoteName
	 * @param fileFullName
	 * @return remoteName
	 */
	static String getRemoteName(String fileFullName){
		
		String str01 = fileFullName.substring(0,4);
		String str02 = fileFullName.substring(5,10);
		String remoteName = str01+"/"+str02;
		//flag
		System.out.println("remoteName:"+str01+"/"+str02+"/");
		return remoteName;
	}
	
	/**
	 * 删除临时缓存文件和文件夹
	 * @param jsFilePath
	 */
	 void deleteCatchBucket(String jsFilePath){
		File file = new File(jsFilePath);
		file.delete();
		
		String fileBucketPath = jsFilePath.substring(0,jsFilePath.lastIndexOf("/"));
		file = new File(fileBucketPath);
		file.delete();
	}
}
