package com.bhu.vas.validate;

//import com.bhu.vas.business.bucache.redis.serviceimpl.unique.facade.UniqueFacadeService;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.helper.phone.PhoneHelper;
import com.smartwork.msip.cores.plugins.reservedwordfilter.ReservedWordFilterHelper;
import com.smartwork.msip.cores.plugins.wordfilter.WordFilterHelper;
//import com.smartwork.msip.cores.plugins.reservedwordfilter.ReservedWordFilterHelper;
//import com.smartwork.msip.cores.plugins.wordfilter.WordFilterHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;

//@Service
public class ValidateService {
	
	public static final int ValidateType_Email = 0;
	public static final int ValidateType_Mobileno = 1;
	public static final int ValidateType_Nick = 2;
	
	/*private static boolean isCmbtNameExist(String name){
		if(name == null) return true;
		name = name.trim();
		if(StringUtils.isEmpty(name)) return true;
		String simple = JChineseConvertor.convertToZhs(name);//.getInstance().t2s(name);
		return BlurCmbtParserFilterHelper.filterMatchedTagsSimpleFullText(simple);
	}*/
	
	/*public static boolean checkEmailExist(String email){
		ResponseError error = validateEmail(email);
		if(error == null) return false;
		else return true;
	}
	
	public static boolean checkNickExist(String nick){
		ResponseError error = validateNick(nick);
		if(error == null) return false;
		else return true;
	}*/
	
	public static boolean checkNickValidate(String nick){
		ResponseError error = validateNick(nick);
		if(error == null) return false;
		else return true;
	}
	
	public static boolean checkMobilenoValidate(int countryCode,String mobileno){
		ResponseError error = validateMobileno(countryCode,mobileno);
		if(error == null) return false;
		else return true;
	}
	
	/*public static ResponseError validateEmail(String email){//,UserService userService){
		if(StringUtils.isEmpty(email)){
			return ResponseError.embed(ResponseErrorCode.AUTH_EMAIL_INVALID_FORMAT, new String[]{email});
		}
		if(!StringHelper.isValidEmailCharacter(email)){
			return ResponseError.embed(ResponseErrorCode.AUTH_EMAIL_INVALID_FORMAT, new String[]{email});
		}
		
		if(UniqueFacadeService.checkEmailExist(email)){//userService.isEmailExist(email)){
			return ResponseError.embed(ResponseErrorCode.AUTH_EMAIL_DATA_EXIST, new String[]{email});
		}
		return null;
	}*/
	
	/*public static ResponseError validateSnsNick(String nickname){//,UserService userService){
		String tmpnick = StringHelper.replaceBlankAndLowercase(nickname);
		if(ReservedWordFilterHelper.isWordAllMatch(tmpnick)){
			return ResponseError.embed(ResponseErrorCode.AUTH_NICKNAME_DATA_EXIST);
		}
		if(WordFilterHelper.isFilterHtmlHits(tmpnick)){
			return ResponseError.embed(ResponseErrorCode.AUTH_NICKNAME_DATA_EXIST);//,new String[]{nickname}));//renderHtml(response, html, headers)
		}
		if(isCmbtNameExist(nickname)){
			return ResponseError.embed(ResponseErrorCode.AUTH_NICKNAME_DATA_EXIST);//,new String[]{nickname}));//renderHtml(response, html, headers)
		}
		if(UniqueFacadeService.checkNickExist(nickname)){//userService.isNicknameExist(nickname)){
			return ResponseError.embed(ResponseErrorCode.AUTH_NICKNAME_DATA_EXIST);//, new String[]{nickname}));
		}
		return null;
	}*/

	//只对nick进行非法字符串的验证，不进行唯一性的验证
	public static ResponseError validateNick(String nickname){//,UserService userService){
		int charlen = nickname.length();//StringHelper.realStringCharlength(nickname);
		if(charlen < 2 || charlen > 14){
			return ResponseError.embed(ResponseErrorCode.AUTH_NICKNAME_INVALID_LENGTH,new String[]{"2","14"});//renderHtml(response, html, headers)
		}
		
		if(!StringHelper.isValidNicknameCharacter(nickname)){
			return ResponseError.embed(ResponseErrorCode.AUTH_NICKNAME_INVALID_FORMAT,new String[]{nickname});//renderHtml(response, html, headers)
		}
		String tmpnick = StringHelper.replaceBlankAndLowercase(nickname);
		if(ReservedWordFilterHelper.isWordAllMatch(tmpnick)){
			return ResponseError.embed(ResponseErrorCode.AUTH_NICKNAME_DATA_EXIST);
		}
		if(WordFilterHelper.isFilterHtmlHits(tmpnick)){
			return ResponseError.embed(ResponseErrorCode.AUTH_NICKNAME_DATA_EXIST);//,new String[]{nickname}));//renderHtml(response, html, headers)
		}
		/*if(isCmbtNameExist(nickname)){
			return ResponseError.embed(ResponseErrorCode.AUTH_NICKNAME_DATA_EXIST);//,new String[]{nickname}));//renderHtml(response, html, headers)
		}
		if(UniqueFacadeService.checkNickExist(nickname)){//userService.isNicknameExist(nickname)){
			return ResponseError.embed(ResponseErrorCode.AUTH_NICKNAME_DATA_EXIST);//, new String[]{nickname}));
		}*/
		return null;
	}
	
	public static ResponseError validateMobileno(int countryCode,String mobileno){
		return validateMobileno(countryCode,mobileno,null);
	}
	
	public static ResponseError validateMobilenoRegx(int countryCode,String mobileno){
		int charlen = mobileno.length();
		if(charlen < 6 || charlen > 16){
			return ResponseError.embed(ResponseErrorCode.AUTH_MOBILENO_INVALID_LENGTH,new String[]{"6","16"});//renderHtml(response, html, headers)
		}
		
		//if(!StringHelper.isValidMobilenoCharacter(mobileno)){
		if(!PhoneHelper.isValidPhoneCharacter(countryCode, mobileno)){
			return ResponseError.embed(ResponseErrorCode.AUTH_MOBILENO_INVALID_FORMAT);//renderHtml(response, html, headers)
		}
		return null;
	}
	
	/*public static boolean validateValidMobilenoRegx(int countryCode,String mobileno){
		int charlen = mobileno.length();
		if(charlen < 6 || charlen > 16){
			return false;
		}
		if(!PhoneHelper.isValidPhoneCharacter(countryCode, mobileno)){//.isValidMobilenoCharacter(mobileno)){
			return false;
		}
		if(!StringHelper.isValidMobilenoCharacter(mobileno)){
			return false;
		}
		return true;
	}*/
	
	public static ResponseError validateMobileno(int countryCode,String mobileno,String oldmobileno){//,UserService userService){
		/*//判断已经修改过一次,就不能修改
		if(StringUtils.isNotEmpty(oldmobileno)){
			if(oldmobileno.equals(mobileno)){
				return ResponseError.embed(ResponseErrorCode.AUTH_MOBILENO_DATA_EXIST);
			}
		}*/
		
		ResponseError error = validateMobilenoRegx(countryCode,mobileno);
		if(error != null) return error;
		/*int charlen = mobileno.length();
		if(charlen < 6 || charlen > 16){
			return ResponseError.embed(ResponseErrorCode.AUTH_MOBILENO_INVALID_LENGTH,new String[]{"6","16"});//renderHtml(response, html, headers)
		}
		
		if(!StringHelper.isValidMobilenoCharacter(mobileno)){
			return ResponseError.embed(ResponseErrorCode.AUTH_MOBILENO_INVALID_FORMAT);//renderHtml(response, html, headers)
		}*/
		
		/*if(UniqueFacadeService.checkMobilenoExist(countryCode,mobileno)){//userService.isPermalinkExist(permalink)){
			return ResponseError.embed(ResponseErrorCode.AUTH_MOBILENO_DATA_EXIST);
		}*/
		return null;
	}
	
	/**
	 * IOS openUDID 在uuid上追加8为随机数保持了40位的UDID
	 * @param duuid
	 * @return
	 */
	
	public static ResponseError validateDeviceUUID(String duuid){
		String lowuuid = duuid.toLowerCase();
		int charlen = lowuuid.length();
		if(charlen < 32 || charlen > 40){
			return ResponseError.embed(ResponseErrorCode.AUTH_UUID_INVALID_LENGTH,new String[]{"32","40"});//renderHtml(response, html, headers)
		}
		
		/*if(!StringHelper.isValidUUIDCharacter(lowuuid)){
			return ResponseError.embed(ResponseErrorCode.AUTH_UUID_INVALID_FORMAT,new String[]{lowuuid});//renderHtml(response, html, headers)
		}*/
		return null;
	}
	
	
/*	public boolean isEmailExist(String email){
		if(StringUtils.isEmpty(email)) return true;
		if(email.toLowerCase().endsWith(UserService.FORMAL_USER_MAIL_SUFFIX)) return true;
		user
		Integer userid = this.findUniqueIdByProperty("email", email.toLowerCase());
		return (userid != null);
	}
	
	public boolean isNicknameExist(String nickname){
		if(nickname.toLowerCase().endsWith(UserService.FORMAL_USER_NICKNAME_SUFFIX)) return true;
		String lstnick = StringHelper.replaceBlankAndLowercase(JChineseConvertor.getInstance().t2s(nickname));
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("lstnick", lstnick);//.andColumnIsNull("truename");////.andSimpleCaulse(" 1=1 ");
		int count =  this.countByModelCriteria(mc);
		return count >0;
	}
	
	public boolean isPermalinkExist(String permalink){
		if(StringUtils.isEmpty(permalink)) return true;
		Integer userid = this.findUniqueIdByProperty("permalink", permalink.toLowerCase());
		return (userid != null);
	}*/
	
/*	public User findUserByEmail(String email){
		if(StringUtils.isEmpty(email)) return null;
		if(email.toLowerCase().endsWith(UserService.FORMAL_USER_MAIL_SUFFIX)) return null;
		Integer userid = this.findUniqueIdByProperty("email", email.toLowerCase());
		if(userid == null) return null;
		else return this.getById(userid);
	}	
	
	public User findUserByPermalink(String permalink){
		if(StringUtils.isEmpty(permalink)) return null;
		Integer userid = this.findUniqueIdByProperty("permalink", permalink.toLowerCase());
		if(userid == null) return null;
		else return this.getById(userid);
	}*/
}
