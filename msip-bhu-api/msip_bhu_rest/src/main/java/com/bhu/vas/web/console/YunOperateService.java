package com.bhu.vas.web.console;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
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
	static final String QN_REMATE_NAME = "/device/build/";
	static final String QN_BUCKET_URL = "http://7xp7a0.com2.z0.glb.qiniucdn.com/@";
	static final String QN_bucket_name = "test";

	// 阿里云参数
	static final String AL_ACCESS_KEY = "stYL3FtcjOTmvyA4";
	static final String AL_SECRET_KEY = "aicFwcLeEx397kfVQB7OelSV4iqSON";
	static final String AL_REMATE_NAME = "test/";
	static final String AL_BUCKET_NAME = "bhudemo";
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
	public void uploadFile(byte[] bs, String remoteName, String bucketName) throws QiniuException {
		try {

			Auth auth = Auth.create(QN_ACCESS_KEY, QN_SECRET_KEY);
			UploadManager uploadManager = new UploadManager();
			Response res = uploadManager.put(bs, remoteName, auth.uploadToken(bucketName));
			
		} catch (Exception e) {
			System.out.println("七牛yun上传错误");
			e.printStackTrace();
		}
	}

	/**
	 * 阿里云上传
	 * 
	 * @param bs
	 * @param remotePath
	 * @return
	 * @throws Exception
	 */
	public void uploadFile(byte[] bs, String remotePath) throws Exception {
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(bs);
			OSSClient ossClient = new OSSClient(AL_END_POINT, AL_ACCESS_KEY, AL_SECRET_KEY);
			String remoteFilePath = remotePath.substring(0, remotePath.length()).replaceAll("\\\\", "/");
			// 创建上传Object的Metadata
			ObjectMetadata objectMetadata = new ObjectMetadata();
			// 上传文件
			ossClient.putObject(AL_BUCKET_NAME, remoteFilePath, in, objectMetadata);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("阿里云上传出错了");
			e.printStackTrace();
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
			System.out.println("阿里,即将删除:" + fileName);
			OSSClient ossClient = new OSSClient(AL_END_POINT, AL_ACCESS_KEY, AL_SECRET_KEY);
			System.out.println("阿里,开始删除:");
			ossClient.deleteObject(AL_BUCKET_NAME, AL_REMATE_NAME + fileName);

			System.out.println("阿里,删除成功。开始删除七牛云");

			Auth auth = Auth.create(QN_ACCESS_KEY, QN_SECRET_KEY);
			System.out.println("七牛云开始删除:");
			BucketManager bucketManager = new BucketManager(auth);
			System.out.println("七牛云正在删除：");
			bucketManager.delete(QN_bucket_name, QN_REMATE_NAME+fileName);
			System.out.println("七牛yun删除成功。");
		} catch (QiniuException e) {
			System.out.println("七牛云删除失败。");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("删除失败。");
			e.printStackTrace();
		}
	}
}
