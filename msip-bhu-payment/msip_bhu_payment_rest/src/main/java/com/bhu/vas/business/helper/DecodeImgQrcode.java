package com.bhu.vas.business.helper;


import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import jp.sourceforge.qrcode.QRCodeDecoder;
import jp.sourceforge.qrcode.data.QRCodeImage;

/**
 * 通过qrcode实现二维码
 * @author tskk
 * @version 2015-6-28 23:48:13
 * 注：带有logo的二维码解析，可能出现乱码
 * */
public final class DecodeImgQrcode {
	
	public static String decodeImg(File imgFile){
		BufferedImage image = null;
		String content = null;
		try {
			image = ImageIO.read(imgFile);
			QRCodeDecoder decoder = new QRCodeDecoder();
			content = new String(decoder.decode(new CodeImg(image)),"UTF-8");
		} catch (Exception e) {
			System.out.println("二维码解析失败!"+e.getMessage());
			e.printStackTrace();
		}
		return content;
	}
}
final class CodeImg implements QRCodeImage{
	private BufferedImage image;
	public CodeImg(BufferedImage image) {
		super();
		this.image = image;
	}
	@Override
	public int getHeight() {
		return image.getHeight();
	}
	@Override
	public int getPixel(int x, int y) {
		return image.getRGB(x, y);
	}
	@Override
	public int getWidth() {
		return image.getWidth();
	}
	public BufferedImage getImage() {
		return image;
	}
	public void setImage(BufferedImage image) {
		this.image = image;
	}
}
