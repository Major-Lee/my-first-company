package com.bhu.vas.business.ds.device.facade;
import java.io.ByteArrayInputStream;

import org.springframework.stereotype.Service;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

@Service
public class YunUploadService {

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
	public void uploadFile(byte[] bs, String remoteName, String bucketName) throws QiniuException  {
		try {
			Auth auth = Auth.create(QN_ACCESS_KEY, QN_SECRET_KEY);
			UploadManager uploadManager = new UploadManager();
			Response res = uploadManager.put(bs, remoteName, auth.uploadToken(bucketName));
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("七牛云上传出错。");
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
	public void uploadFile(byte[] bs, String remotePath){
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
		}
	}
}
