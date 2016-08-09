package com.bhu.vas.business.helper;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

import sun.misc.BASE64Encoder;

public class EncodeImgQrcodeTest {

	@Test
	public void testWriteToFile() {
		String contents = "weixin://wxpay/bizpayurl?pr=oaSXdvn";
		String format = "png";
		File logoImg = new File("E:"+File.separator+"logo.png");
		File img = new File("E:"+File.separator+"picture.png");
		BufferedImage bufferImgage = null;
		byte[] result = null;
		ByteArrayOutputStream output = null;
		//生成二维码
		EncodeImgQrcode.writeToFile(contents, format, img);
		//添加logo图片
		//File img1 = new File("E:"+File.separator+"picturelogo.png");
		//EncodeImgZingLogo.writeToFile(img, logoImg, format, img1);
		bufferImgage = EncodeImgZingLogo.encodeImgLogo(img, logoImg);
		output = new ByteArrayOutputStream();
		try {
			ImageIO.write(bufferImgage, "png", output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = output.toByteArray();
		 BASE64Encoder encoder = new BASE64Encoder();
		System.out.println("data:image/png;base64,"+encoder.encode(result));
		
		//解析二维码
//		String content = DecodeImgQrcode.decodeImg(img);
//		System.out.println("1:"+content);
//		String content1 = DecodeImgQrcode.decodeImg(img1);
//		System.out.println("2:"+content1);
	}

}
