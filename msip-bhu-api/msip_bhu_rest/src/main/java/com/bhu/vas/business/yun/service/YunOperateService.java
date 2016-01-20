package com.bhu.vas.business.yun.service;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.bhu.vas.api.rpc.vap.iservice.IVapRpcService;
import com.bhu.vas.business.yun.iservice.IYunUploadService;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

@Service
public class YunOperateService implements IYunUploadService {

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
	public void uploadFile2QN(byte[] bs, String dut, String versionId, boolean fw, String JsFilePath) throws Exception {

		Auth auth = Auth.create(QN_ACCESS_KEY, QN_SECRET_KEY);
		UploadManager uploadManager = new UploadManager();
		if (fw) {
			uploadManager.put(bs, String.format("/%s/%s/%s", dut, "build", versionId),
					auth.uploadToken(QN_BUCKET_NAME_FW));
		}
		if (!fw) {
			uploadManager.put(bs,String.format("/%s/%s", getRemoteName(versionId),versionId) ,
					auth.uploadToken(QN_BUCKET_NAME_OM));
			uploadManager.put(JsFilePath, String.format("/%s/%s", getRemoteName(versionId),"version.js"),
					auth.uploadToken(QN_BUCKET_NAME_OM));

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
	public void uploadFile2AL(byte[] bs, String dut, String versionId, boolean fw, String JsFilePath) throws Exception {

		ByteArrayInputStream in = new ByteArrayInputStream(bs);
		OSSClient ossClient = new OSSClient(AL_END_POINT, AL_ACCESS_KEY, AL_SECRET_KEY);
		// 创建上传Object的Metadata
		ObjectMetadata objectMetadata = new ObjectMetadata();
		// 上传文件
		if (fw) {
			ossClient.putObject(AL_BUCKET_NAME_FW, String.format("%s/%s/%s", dut, "build", versionId), in,
					objectMetadata);
		}
		if (!fw) {
			String remateName = getRemoteName(versionId);
			ossClient.putObject(AL_BUCKET_NAME_OM, remateName + "/" + versionId, in, objectMetadata);
			File file = new File(JsFilePath);
			ossClient.putObject(AL_BUCKET_NAME_OM, remateName + "/" + "version.js", file, objectMetadata);
			// 删除临时缓存文件夹
			deleteCatchBucket(JsFilePath);
		}
		System.out.println("阿里云上传成功");
	}

	/**
	 * 删除云备份文件
	 * 
	 * @param fileName
	 *            文件名
	 * @param dut
	 *            版本号
	 * @param fw
	 *            判断是固件版本还是模块版本
	 */
	public void deleteFile(String versionId, String dut, boolean fw) {
		try {

			OSSClient ossClient = new OSSClient(AL_END_POINT, AL_ACCESS_KEY, AL_SECRET_KEY);
			Auth auth = Auth.create(QN_ACCESS_KEY, QN_SECRET_KEY);
			BucketManager bucketManager = new BucketManager(auth);
			if (fw) {
				// 阿里云删除
				ossClient.deleteObject(AL_BUCKET_NAME_FW, dut + "/build/" + versionId);
				System.out.println("阿里云删除成功");
				// 七牛云删除
				bucketManager.delete(QN_BUCKET_NAME_FW, "/" + dut + "/build/" + versionId);
				System.out.println("七牛云删除成功");
			} else {
				String remoteName = getRemoteName(versionId);
				// 阿里云删除
				ossClient.deleteObject(AL_BUCKET_NAME_OM, remoteName + "/" + versionId);
				ossClient.deleteObject(AL_BUCKET_NAME_OM, remoteName + "/" + "version.js");
				// 七牛云删除
				bucketManager.delete(QN_BUCKET_NAME_OM, "/" + remoteName + "/" + versionId);
				bucketManager.delete(QN_BUCKET_NAME_OM, "/" + remoteName + "/" + "version.js");
			}
			System.out.println("删除成功。");
		} catch (Exception e) {
			System.out.println("删除失败。");
			e.printStackTrace();
		}
	}

	/**
	 * 上传OM文件时，本地创建version.js文件
	 * 
	 * @param fileFullName
	 *            加后缀的全名
	 * @return js文件路径
	 */
	static String getFilePath(String versionId) {

		String fileName = versionId.substring(0, versionId.lastIndexOf("."));
		String str = "jcb(\n\t\t{\"name\":\"" + fileName + "\", \"url\":\"" + versionId + "\"}\n)";
		String filePath = null;
		try {
			String CacheBucketPath = "/BHUData/uploadOMCache/" + System.currentTimeMillis();
			File fileBucket = new File(CacheBucketPath);
			fileBucket.mkdir();
			filePath = CacheBucketPath + "/version.js";
			File file = new File(filePath);
			BufferedWriter bw = new BufferedWriter (new OutputStreamWriter (new FileOutputStream (file), "UTF-8"));
			bw.write(str);
			bw.flush ();
			bw.close ();
		} catch (IOException e) {
			System.out.println("创建文件夹错误");
			e.printStackTrace();
		}
		return filePath;
	}

	/**
	 * 根据文件名获取remoteName
	 * 
	 * @param fileFullName
	 * @return remoteName
	 */
	static String getRemoteName(String versionId) {

		String str01 = versionId.substring(0, 4);
		String str02 = versionId.substring(5, 10);
	 	String str03 = versionId.substring(10,16);
		String remoteName = str03+"/"+str01 + "/" + str02;
		// flag
		System.out.println("remoteName:" + str03+"/"+str01 + "/" + str02);
		return remoteName;
	}

	/**
	 * 删除临时缓存文件和文件夹
	 * 
	 * @param jsFilePath
	 */
	static void deleteCatchBucket(String jsFilePath) {
		File file = new File(jsFilePath);
		file.delete();

		String fileBucketPath = jsFilePath.substring(0, jsFilePath.lastIndexOf("/"));
		file = new File(fileBucketPath);
		file.delete();
	}

	public String[] getURL(boolean fw, String versionId, String dut) {
		String QNurl = null;
		String ALurl = null;
		String url[] = new String[2];
		if (fw) {

			QNurl = String.format("%s/%s/%s/%s", YunOperateService.QN_BUCKET_URL_FW, dut, "build", versionId);
			ALurl = String.format("%s://%s.%s/%s/%s/%s","http",YunOperateService.AL_BUCKET_NAME_FW, YunOperateService.AL_END_POINT,
					dut, "build", versionId);
		}
		if (!fw) {
			QNurl = YunOperateService.QN_BUCKET_URL_OM+"/"+versionId.substring(10,16);
			ALurl = String.format("%s://%s.%s/%s","http", YunOperateService.AL_BUCKET_NAME_OM, YunOperateService.AL_END_POINT,versionId.substring(10,16));
		}
		url[0] = QNurl;
		url[1] = ALurl;
		return url;
	}

	private static ExecutorService exec = Executors.newFixedThreadPool(5);

	// 异步的上传至阿里云、七牛云
	public void uploadYun(final byte[] bs, final int uid, final String dut, final boolean fw, final String versionId,
			final IVapRpcService rpcService) {
		exec.submit((new Runnable() {
			@Override
			public void run() {
				try {
					String JsFilePath = null;
					if (!fw) {
						JsFilePath = getFilePath(versionId);
					}
					// 七牛云
					uploadFile2QN(bs, dut, versionId, fw, JsFilePath);
					// 阿里云
					uploadFile2AL(bs, dut, versionId, fw, JsFilePath);

				} catch (Exception e) {
					System.out.println("uploadYun:fail");
					rpcService.addDeviceVersionUploadFailCallback(uid, fw, versionId);
					e.printStackTrace();
				}
			}
		}));
	}
}
