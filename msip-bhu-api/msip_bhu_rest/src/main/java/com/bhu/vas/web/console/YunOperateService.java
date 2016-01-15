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
	static final String QN_REMATE_NAME = "/build/";
	static final String QN_BUCKET_URL_FW = "http://7xq32u.com2.z0.glb.qiniucdn.com/@";
	static final String QN_BUCKET_URL_OM = "http://7xq3nw.com2.z0.glb.qiniucdn.com/@";
	static final String QN_BUCKET_NAME_FW = "devicefw";
	static final String QN_BUCKET_NAME_OM = "deviceom";
	// 阿里云参数
	static final String AL_ACCESS_KEY = "stYL3FtcjOTmvyA4";
	static final String AL_SECRET_KEY = "aicFwcLeEx397kfVQB7OelSV4iqSON";
	static final String AL_REMATE_NAME = "/build/";
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
	public void uploadFile(byte[] bs, String remoteName, String bucketName) throws QiniuException {
		try {

			Auth auth = Auth.create(QN_ACCESS_KEY, QN_SECRET_KEY);
			UploadManager uploadManager = new UploadManager();
			Response res = uploadManager.put(bs, remoteName, auth.uploadToken(bucketName));
			System.out.println("七牛云上传成功。");
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
			ossClient.putObject(AL_BUCKET_NAME_FW, remoteFilePath, in, objectMetadata);
			System.out.println("阿里云上传成功");
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
			OSSClient ossClient = new OSSClient(AL_END_POINT, AL_ACCESS_KEY, AL_SECRET_KEY);
			ossClient.deleteObject(AL_BUCKET_NAME_FW, AL_REMATE_NAME + fileName);
			Auth auth = Auth.create(QN_ACCESS_KEY, QN_SECRET_KEY);
			BucketManager bucketManager = new BucketManager(auth);
			bucketManager.delete(QN_BUCKET_NAME_FW, QN_REMATE_NAME+fileName);
			System.out.println("删除成功。");
		} catch (QiniuException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("删除失败。");
			e.printStackTrace();
		}
	}
}
