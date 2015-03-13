package com.whisper.web.user;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smartwork.msip.cores.fsstorerule.FSIndentifyVerHelper;
import com.smartwork.msip.cores.fsstorerule.image.ImageException;
import com.smartwork.msip.cores.fsstorerule.image.ImageHelper;
import com.smartwork.msip.cores.fsstorerule.image.ImageService;
import com.smartwork.msip.cores.fsstorerule.image.ImageType;
import com.smartwork.msip.cores.fsstorerule.image.ThumbType;
import com.smartwork.msip.cores.fsstorerule.zip.ZipService;
import com.smartwork.msip.cores.fsstorerule.zip.ZipType;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;
import com.whisper.api.user.model.User;
import com.whisper.api.user.model.UserAvatarState;
import com.whisper.business.asyn.web.builder.DeliverMessageType;
import com.whisper.business.asyn.web.model.IDTO;
import com.whisper.business.asyn.web.service.DeliverMessageService;
import com.whisper.business.ucenter.service.UserAvatarStateService;
import com.whisper.business.ucenter.service.UserService;
import com.whisper.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.whisper.msip.web.ValidateUserCheckController;

@Controller
@RequestMapping("/user")
public class UserUploadController extends ValidateUserCheckController{

	@Resource
	private UserService userService;
	
	@Resource
	private UserAvatarStateService userAvatarStateService;
	
	@Resource
	private DeliverMessageService deliverMessageService;
	
	@Resource(name="avatarImageService")
	private ImageService avatarImageService;
	
	@Resource(name="coverImageService")
	private ImageService coverImageService;
	
	@Resource(name="zipService")
	private ZipService zipService;
	
	
	/**
	 * 头像原图上传，缺省依旧会生成NO,NL,PL,PM,PS
	 * @param request
	 * @param response
	 * @param myfile
	 * @param fileid
	 * @param pictureResource
	 * @throws IOException
	 * @throws ImageException
	 */
	@RequestMapping(value="/avatar/upload", method = RequestMethod.POST)
	public void avatarupload(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value="file", 	required = true) MultipartFile myfile, 
			@RequestParam(value="js",  		required = false, defaultValue = "false") boolean jsclient,
			@RequestParam(value="functype", required = false, defaultValue = "") String functype,
			@RequestParam(required = true) Integer uid) throws IOException, ImageException{
		/*System.out.println("--------upload:"+myfile);
		System.out.println("--------upload:"+myfile.getOriginalFilename());
		System.out.println("--------upload:"+myfile.getName());*/
		//System.out.println("--------upload:"+myfile.getSize());
		
		long filesize = myfile.getSize();
		
		if(filesize == 1393){
			SpringMVCHelper.renderJson(response, ResponseError.ERROR);
			//render4UploadFail(response,ResponseErrorCode.REQUEST_IMAGE_UPLOAD_EXCEPTION ,jsclient,functype);
			return;
		}
		
		System.out.println("~~~~~~~~~~~~ starting:avatarupload");
		User user = this.userService.getById(uid);
		validateUserNotNull(user);
		try{
			ImageType type = avatarImageService.getImageRule().getImageType(myfile.getOriginalFilename());
			if(type==null || type.equals(ImageType.GIF)){
				//String ret = "<script>document.domain='jing.fm';window.parent.Core.uploadCallback('"+JsonHelper.getJSONString(ResponseError.embed(ResponseErrorCode.USER_AVATAR_UPLOAD_IMAGE_FILEFORMAT_INVALID))+"')</script>";
				//SpringMVCHelper.renderHtml(response, ret);
				//SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.USER_AVATAR_UPLOAD_IMAGE_FILEFORMAT_INVALID));
				render4UploadFail(response,ResponseErrorCode.USER_AVATAR_UPLOAD_IMAGE_FILEFORMAT_INVALID,jsclient,functype);
				return;
			}
			type = ImageHelper.getImageFileFormatType(myfile.getInputStream());
			if(type == null || type.equals(ImageType.GIF)){
				//String ret = "<script>document.domain='jing.fm';window.parent.Core.uploadCallback('"+JsonHelper.getJSONString(ResponseError.embed(ResponseErrorCode.USER_AVATAR_UPLOAD_IMAGE_FILEFORMAT_INVALID))+"')</script>";
				//SpringMVCHelper.renderHtml(response, ret);
				//SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.USER_AVATAR_UPLOAD_IMAGE_FILEFORMAT_INVALID));
				render4UploadFail(response,ResponseErrorCode.USER_AVATAR_UPLOAD_IMAGE_FILEFORMAT_INVALID,jsclient,functype);
				return;
			}
			
			boolean fileid_create = false; 
			UserAvatarState userAvatarState = this.userAvatarStateService.getOrCreateById(uid);
			String vfileid = userAvatarState.getLocalAvatarTag();
			
			String real_fid = FSIndentifyVerHelper.distillRealfid(vfileid);
			
			if(StringUtils.isEmpty(real_fid)){
				fileid_create = true;
				real_fid = avatarImageService.createImage(myfile.getInputStream(), avatarImageService.getImageRule().getImageType(myfile.getOriginalFilename()));
			}else{
				real_fid = avatarImageService.createImageWithFileid(myfile.getInputStream(), real_fid);
			}
			
			//用户头像截取中央正方形图
		    BufferedImage image = ImageIO.read(avatarImageService.getImageFile(real_fid,ThumbType.NORMALORIGINAL));
			int width = image.getWidth();
			int height = image.getHeight();
			boolean needcrop = false;
			if(width == height){
				needcrop = false;
			}else{
				if(width < height){
					height = width;
				}else{
					width = height;
				}
				needcrop = true;
			}
			if(needcrop)
				real_fid = avatarImageService.createCropAreaImage(real_fid, ThumbType.NORMALORIGINAL.getDisplay(),real_fid, width, height, 0, 0);
			//用户头像缩略图
			avatarImageService.createThumnail(real_fid,ThumbType.USERLARGE,false);
			//avatarImageService.createThumnails(real_fid, ThumbType.USERSMALL,ThumbType.USERMIDDLE);//,ThumbType.USERTHUMB,ThumbType.USERTINY);
			/*avatarImageService.createThumnail(real_fid,ThumbType.USERPERSONAL2X,false);
			avatarImageService.createThumnail(real_fid,ThumbType.USERPERSONAL1X,false);
			{//生成圆形cover图片
				String userMiddlePath = avatarImageService.getImageFile(real_fid, ThumbType.USERPERSONAL2X).getAbsolutePath();
				avatarImageService.watermarkCoverImage2Image(userMiddlePath, userMiddlePath+".png", RuntimeConfiguration.JingWatermarkAvatar2xCoverPngPath);//(fileid,ThumbType.USERMIDDLE,false);
				
				String userThumbPath = avatarImageService.getImageFile(real_fid, ThumbType.USERPERSONAL1X).getAbsolutePath();
				avatarImageService.watermarkCoverImage2Image(userThumbPath, userThumbPath+".png", RuntimeConfiguration.JingWatermarkAvatar1xCoverPngPath);//(fileid,ThumbType.USERMIDDLE,false);
			}*/
			//增加portraitid到userext表中
			//UserAvatarState userAvatarState = this.userAvatarStateService.getOrCreateById(uid);
			user = this.userService.getById(uid);
			if(fileid_create){
				userAvatarState.setLocalAvatarTag(real_fid);
				user.setAvatar(real_fid);
			}else{
				vfileid = FSIndentifyVerHelper.appendAndIncreaseVer(vfileid);
				userAvatarState.setLocalAvatarTag(vfileid);
				user.setAvatar(vfileid);
			}
			/*if(fileid_create){
				userAvatarState.setLocalAvatarTag(real_fid);
				this.userAvatarStateService.update(userAvatarState);
				//发消息avatar add
				deliverMessageService.sendUserAvatarUpdActionMessage(DeliverMessageType.AC.getPrefix(), user.getId(), fileid, IDTO.ACT_ADD);//sendUserRegisteredActionMessage(DeliverMessageType.AC.getPrefix(), user.getId(), itoken);
			}else{
				if(user.getAvatar().equals(fileid)){
					;//不发消息，减少不必要的数据库操作
				}else{
					//发消息avatar update
					deliverMessageService.sendUserAvatarUpdActionMessage(DeliverMessageType.AC.getPrefix(), user.getId(), fileid, IDTO.ACT_UPDATE);
				}
			}*/
			this.userAvatarStateService.update(userAvatarState);
			this.userService.update(user);

			System.out.println("上传成功");
			if(fileid_create){
				//发消息avatar add
				deliverMessageService.sendUserAvatarUpdActionMessage(DeliverMessageType.AC.getPrefix(), user.getId(), real_fid, IDTO.ACT_ADD);//sendUserRegisteredActionMessage(DeliverMessageType.AC.getPrefix(), user.getId(), itoken);
				render4UploadSuccess(response,real_fid,jsclient,functype);
			}else{
				//发消息avatar update
				deliverMessageService.sendUserAvatarUpdActionMessage(DeliverMessageType.AC.getPrefix(), user.getId(), vfileid, IDTO.ACT_UPDATE);
				render4UploadSuccess(response,vfileid ,jsclient,functype);
			}
		}catch(Exception ex){
			ex.printStackTrace();
			render4UploadFail(response,ResponseErrorCode.REQUEST_IMAGE_UPLOAD_EXCEPTION ,jsclient,functype);
			//SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}finally{
			
		}
	}
	/**
	 * 个人封面上传，缺省会生成NO,CM,CL
	 * @param request
	 * @param response
	 * @param myfile
	 * @param fileid
	 * @param uid
	 * @throws IOException
	 * @throws ImageException
	 */
	@RequestMapping(value="/cover/upload", method = RequestMethod.POST)
	public void coverupload(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value="file", required = true) MultipartFile myfile, 
			//@RequestParam(value="fid",  required = false) String fileid,
			@RequestParam(value="js",  		required = false, defaultValue = "false") boolean jsclient,
			@RequestParam(value="functype", required = false, defaultValue = "") String functype,
			@RequestParam(required = true) Integer uid) throws IOException, ImageException{
		/*System.out.println("--------upload:"+myfile);
		System.out.println("--------upload:"+myfile.getOriginalFilename());
		System.out.println("--------upload:"+myfile.getName());
		System.out.println("--------upload:"+myfile.getSize());*/
		System.out.println("~~~~~~~~~~~~ starting:coverupload:" + uid);
		if(uid.intValue() <=0) {
			SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
			return;
		}
		User user = this.userService.getById(uid);
		validateUserNotNull(user);
		try{
			ImageType type = coverImageService.getImageRule().getImageType(myfile.getOriginalFilename());
			if(type.equals(ImageType.GIF)){
				render4UploadFail(response,ResponseErrorCode.USER_AVATAR_UPLOAD_IMAGE_FILEFORMAT_INVALID,jsclient,functype);
				//SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.USER_AVATAR_UPLOAD_IMAGE_FILEFORMAT_INVALID));
				return;
			}
			type = ImageHelper.getImageFileFormatType(myfile.getInputStream());
			if(type == null || type.equals(ImageType.GIF)){
				render4UploadFail(response,ResponseErrorCode.USER_AVATAR_UPLOAD_IMAGE_FILEFORMAT_INVALID,jsclient,functype);
				//SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.USER_AVATAR_UPLOAD_IMAGE_FILEFORMAT_INVALID));
				return;
			}
			System.out.println("~~~~~~~~~~~~ starting:coverupload:" + 1);
			//boolean fileid_create = false; 
			String vdbfileid = user.getCover();
			String real_fid = null;
			boolean isFidFormat = FSIndentifyVerHelper.isFidFormat(vdbfileid);
			if(isFidFormat){
				real_fid = FSIndentifyVerHelper.distillRealfid(vdbfileid);
			}else{
			}
			
			if(!isFidFormat){//cover不存在或者cover是defaultcover
				//fileid_create = true;
				real_fid = coverImageService.createImage(myfile.getInputStream(), coverImageService.getImageRule().getImageType(myfile.getOriginalFilename()));
			}else
				real_fid = coverImageService.createImageWithFileid(myfile.getInputStream(), real_fid);
			//用户头像截取中央正方形图
		    BufferedImage image = ImageIO.read(coverImageService.getImageFile(real_fid,ThumbType.NORMALORIGINAL));
			int width = image.getWidth();
			int height = image.getHeight();
			boolean needcrop = false;
			if(width == height){
				needcrop = false;
			}else{
				if(width < height){
					height = width;
				}else{
					width = height;
				}
				needcrop = true;
			}
			//System.out.println("~~~~~~~~~~~~ starting:coverupload:" + 2);
			if(needcrop)
				real_fid = coverImageService.createCropAreaImage(real_fid, ThumbType.NORMALORIGINAL.getDisplay(),real_fid, width, height, 0, 0);
			//System.out.println("~~~~~~~~~~~~ starting:coverupload:" + 3);
			coverImageService.createThumnail(real_fid,ThumbType.COVERLARGE,false);
			//System.out.println("~~~~~~~~~~~~ starting:coverupload:" + 4);
			//coverImageService.createThumnail(real_fid,ThumbType.COVERMIPMIDDLE,false);
			//System.out.println("~~~~~~~~~~~~ starting:coverupload:" + 5);
			//用户封面缩略图
			//coverImageService.createThumnails(real_fid, ThumbType.COVERMIDDLE, ThumbType.COVERSMALL);//, ThumbType.COVERSUPERSMALL);
			/*//用户Opa半透明封面缩略图
			coverImageService.createThumnailsForceRegion(real_fid, ThumbType.COVERLARGEOpa,false);
			String userMiddlePath = coverImageService.getImageFile(real_fid, ThumbType.COVERLARGEOpa).getAbsolutePath();
			coverImageService.watermarkCoverImage2Image(userMiddlePath, userMiddlePath+".png", RuntimeConfiguration.JingWatermarkCoverPngPath,false);//(fileid,ThumbType.USERMIDDLE,false);
			*/
			
			
			//System.out.println("~~~~~~~~~~~~ starting:coverupload:" + 6);
			/*coverImageService.createCropAreaImage(fileid, ThumbType.COVERRECTMIDDLE.getDisplay(),fileid, 
					ThumbType.COVERRECTMIDDLE.getMaxWidth(), ThumbType.COVERRECTMIDDLE.getMaxHeight(), 0, 0);*/
			/*coverImageService.createCropAreaImage(fileid, ThumbType.COVERMIDDLE, 
					fileid, ThumbType.COVERRECTMIDDLE, ThumbType.COVERRECTMIDDLE.getMaxWidth(),
					ThumbType.COVERRECTMIDDLE.getMaxHeight(), 0, 0);*/
			//coverImageService.createThumnail(fileid,ThumbType.USERMIDDLE,false);
			//coverImageService.createThumnail(fileid,ThumbType.USERTHUMB,false);
			/*{//生成圆形cover图片
				String userMiddlePath = avatarImageService.getImageFile(fileid, ThumbType.USERMIDDLE).getAbsolutePath();
				avatarImageService.watermarkCoverImage2Image(userMiddlePath, userMiddlePath+".png", RuntimeConfiguration.JingWatermarkAvatar2xCoverPngPath);//(fileid,ThumbType.USERMIDDLE,false);
				
				String userThumbPath = avatarImageService.getImageFile(fileid, ThumbType.USERTHUMB).getAbsolutePath();
				avatarImageService.watermarkCoverImage2Image(userThumbPath, userThumbPath+".png", RuntimeConfiguration.JingWatermarkAvatar1xCoverPngPath);//(fileid,ThumbType.USERMIDDLE,false);
			}*/
				//UserAvatarState userAvatarState = this.userAvatarStateService.getOrCreateById(uid);
				//user = this.userService.getById(uid);
			if(!isFidFormat){
				user.setCover(real_fid);
				deliverMessageService.sendUserCoverUpdActionMessage(DeliverMessageType.AC.getPrefix(), user.getId(), real_fid, IDTO.ACT_ADD);
			}else{
				vdbfileid = FSIndentifyVerHelper.appendAndIncreaseVer(vdbfileid);
				user.setCover(vdbfileid);
				deliverMessageService.sendUserCoverUpdActionMessage(DeliverMessageType.AC.getPrefix(), user.getId(), vdbfileid, IDTO.ACT_UPDATE);
			}
			this.userService.update(user);
			//发消息avatar add
			System.out.println("============================+上传成功");
			//Response res = new Response(true,"正常",new ResourceRet(fileid,imageService.getImageRule().ID2URL(fileid,ThumbType.PORTRAITSMALL)));
			//<script>window.parent.Setting.uploadCallback("JSON")</script>
			//String ret = "<script>document.domain='ling.com';window.parent.Core.uploadCallback('"+JsonHelper.getJSONString(ResponseSuccess.embed(fileid))+"')</script>";
			//String ret = "<script>document.domain='jing.fm';window.parent.Core.uploadCallback('"+JsonHelper.getJSONString(ResponseSuccess.embed(fileid))+"')</script>";
			//SpringMVCHelper.renderHtml(response, ret);
			if(!isFidFormat){
				render4UploadSuccess(response,real_fid,jsclient,functype);
				//SpringMVCHelper.renderJson(response, ResponseSuccess.embed(real_fid));
			}else{
				render4UploadSuccess(response,vdbfileid,jsclient,functype);
				//SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vdbfileid));
			}
		}catch(Exception ex){
			ex.printStackTrace();
			render4UploadFail(response,ResponseErrorCode.REQUEST_IMAGE_UPLOAD_EXCEPTION ,jsclient,functype);
		}finally{
			
		}
	}
	
	public static void render4UploadSuccess(HttpServletResponse response,Object payload,boolean jsclient,String fuctype){
		System.out.println(String.format("render4UploadSuccess %s %s ", jsclient,fuctype));
		if(jsclient){
			String ret = "<script>document.domain='jing.fm';window.parent.Core.uploadCallback('"+JsonHelper.getJSONString(ResponseSuccess.embed(payload))+"','"+fuctype+"')</script>";
			SpringMVCHelper.renderHtml(response, ret);
		}else{
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(payload));
		}
	}
	public static void render4UploadFail(HttpServletResponse response,ResponseErrorCode errorCode,boolean jsclient,String fuctype){
		render4UploadFail(response,errorCode,null,jsclient,fuctype);
	}
	public static void render4UploadFail(HttpServletResponse response,ResponseErrorCode errorCode,String[] msg,boolean jsclient,String fuctype){
		System.out.println(String.format("render4UploadFail %s %s ", jsclient,fuctype));
		if(jsclient){
			String ret = "<script>document.domain='jing.fm';window.parent.Core.uploadCallback('"+JsonHelper.getJSONString(ResponseError.embed(errorCode))+"','"+fuctype+"')</script>";
			SpringMVCHelper.renderHtml(response, ret);
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(errorCode));
		}
	}
	
	/**
	 * video jpg audio的文件 zip上传，缺省依旧会生成NO
	 * @param request
	 * @param response
	 * @param myfile
	 * @param jsclient
	 * @param functype
	 * @param to视频的接收方 如果是点对点的方式 则是对方用户的uid 如果是群组的方式 则为group的id
	 * @param type 视频是群组的视频片段还是点对点聊天的视频片段 P 点对点 G 群组 AbstractMediaRule中定义
	 * @param pictureResource
	 * @throws IOException
	 * @throws ImageException
	 */
	@RequestMapping(value="/zip/upload", method = RequestMethod.POST)
	public void zipupload(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value="file", 	required = true) MultipartFile myfile, 
			@RequestParam(value="js",  		required = false, defaultValue = "false") boolean jsclient,
			@RequestParam(value="functype", required = false, defaultValue = "") String functype,
			@RequestParam(required = true) String to,
			@RequestParam(required = true) String type,
			@RequestParam(required = true) Integer uid) throws IOException, ImageException{
		
		if(StringHelper.isEmpty(to)){
			render4UploadFail(response,ResponseErrorCode.REQUEST_IMAGE_UPLOAD_EXCEPTION ,jsclient,functype);
			return;
		}
		
		System.out.println(String.format("name[%s] size[%s]", myfile.getOriginalFilename(),myfile.getSize()));
		/*System.out.println("--------upload:"+myfile);
		System.out.println("--------upload:"+myfile.getOriginalFilename());
		System.out.println("--------upload:"+myfile.getName());
		System.out.println("--------upload:"+myfile.getSize());*/
		
		//long filesize = myfile.getSize();
		//System.out.println("~~~~~~~~~~~~ starting:zip upload filesize:"+filesize);
		User user = this.userService.getById(uid);
		validateUserNotNull(user);
		
		try{
			ZipType ziptype = zipService.getZipRule().getZipType(myfile.getOriginalFilename());
			String businessid = zipService.getZipRule().generateBusinessid(type.charAt(0), String.valueOf(uid), to);
			/*String businessid = (AbstractMediaRule.Group_Business_MediaChat_Type== type.charAt(0))?
					zipService.getZipRule().generateBusinessid_Group(to):
						zipService.getZipRule().generateBusinessid_Peer2Peer(String.valueOf(uid), to);*/
			
			String fileid = zipService.createZip(myfile.getInputStream(), new Date(), ziptype, businessid);
			if(StringUtils.isNotEmpty(fileid))
				render4UploadSuccess(response,fileid ,jsclient,functype);
			else
				render4UploadFail(response,ResponseErrorCode.REQUEST_IMAGE_UPLOAD_EXCEPTION ,jsclient,functype);
		}catch(Exception ex){
			ex.printStackTrace();
			render4UploadFail(response,ResponseErrorCode.REQUEST_IMAGE_UPLOAD_EXCEPTION ,jsclient,functype);
			//SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}finally{
			
		}
	}
}
