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
	public void uploadFile2QN(byte[] bs, String dut, String fileName, boolean fw,String JsFilePath) throws QiniuException {
		try {

			Auth auth = Auth.create(QN_ACCESS_KEY, QN_SECRET_KEY);
			UploadManager uploadManager = new UploadManager();
			if (fw) {
				uploadManager.put(bs, "/" + dut + "/build/" + fileName, auth.uploadToken(QN_BUCKET_NAME_FW));
			}
			if (!fw) {
				uploadManager.put(bs, "/" + getRemoteName(fileName) + fileName, auth.uploadToken(QN_BUCKET_NAME_OM));
				uploadManager.put(JsFilePath, "/" + getRemoteName(fileName) +"version.js", auth.uploadToken(QN_BUCKET_NAME_OM));
				
			}
			System.out.println("七牛云上传成功。");
		} catch (Exception e) {
			System.out.println("七牛yun上传错误");
			e.printStackTrace();
		}
		//
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
		try {
			// dut + yunOperateService.AL_REMATE_NAME + fileName

			ByteArrayInputStream in = new ByteArrayInputStream(bs);
			OSSClient ossClient = new OSSClient(AL_END_POINT, AL_ACCESS_KEY, AL_SECRET_KEY);
			// 创建上传Object的Metadata
			ObjectMetadata objectMetadata = new ObjectMetadata();
			// 上传文件
			if (fw) {
				ossClient.putObject(AL_BUCKET_NAME_FW, dut + "/build/" + fileName, in, objectMetadata);
			}
			if (!fw) {
				ossClient.putObject(AL_BUCKET_NAME_OM, getRemoteName(fileName)+fileName, in, objectMetadata);
				File file = new File(JsFilePath);
				ossClient.putObject(AL_BUCKET_NAME_OM, getRemoteName(fileName)+"version.js", file, objectMetadata);
			}
			System.out.println("阿里云上传成功");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("阿里云上传出错了");
			e.printStackTrace();
		}finally{
			deleteCatchBucket(JsFilePath);
		}
	}

	/**
	 * 阿里云删除文件
	 * 
	 * @param ossConfigure
	 * @param filePath
	 *            文件夹+文件名
	 * @return
	 */
	public static void deleteFile(String fileName) {
		try {
			OSSClient ossClient = new OSSClient(AL_END_POINT, AL_ACCESS_KEY, AL_SECRET_KEY);
			ossClient.deleteObject(AL_BUCKET_NAME_FW, "/build/" + fileName);
			Auth auth = Auth.create(QN_ACCESS_KEY, QN_SECRET_KEY);
			BucketManager bucketManager = new BucketManager(auth);
			bucketManager.delete(QN_BUCKET_NAME_FW, "/build/" + fileName);
			System.out.println("删除成功。");
		} catch (QiniuException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("删除失败。");
			e.printStackTrace();
		}
	}

	/**
	 * 上传OM文件时，本体创建version.js文件
	 * @param fileFullName 加后缀的全名
	 * @return js文件路径
	 */
	static String getFilePath(String fileFullName) {
		
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
	static void deleteCatchBucket(String jsFilePath){
		File file = new File(jsFilePath);
		file.delete();
		
		String fileBucketPath = jsFilePath.substring(0,jsFilePath.lastIndexOf("/"));
		file = new File(fileBucketPath);
		file.delete();
	}
}
