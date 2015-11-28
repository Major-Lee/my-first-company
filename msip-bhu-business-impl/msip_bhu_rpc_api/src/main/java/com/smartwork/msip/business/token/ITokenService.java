package com.smartwork.msip.business.token;


public interface ITokenService {
	public static final int Access_Token_Matched = 1;
	public static final int Access_Token_Illegal_Format= 2;
	public static final int Access_Token_Expired = 3;
	public static final int Access_Token_NotExist = 4;
	public static final int Access_Token_NotMatch = 5;
	
	UserTokenDTO generateUserAccessToken(Integer uid,boolean ifExpiredThenReplaced,boolean ifExistThenReplaced);
	UserTokenDTO validateUserAccessToken(String accessToken);
	UserTokenDTO doRefreshUserAccessToken(String refreshToken);
}
