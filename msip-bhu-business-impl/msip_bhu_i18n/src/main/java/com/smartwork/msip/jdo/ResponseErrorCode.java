package com.smartwork.msip.jdo;

import java.util.HashMap;
import java.util.Map;

public enum ResponseErrorCode {    
	/*
	validate.Code.notEquals = 验证码不匹配
	validate.userOrpwd.error = 用户名密码错误
	validate.request.post.method = 只支持post请求*/
	RPC_PARAMS_VALIDATE_ILLEGAL("10000", "rpc.params.validate.illegal"),
	RPC_PARAMS_VALIDATE_EMPTY("10001", "rpc.params.validate.empty"),
	RPC_MESSAGE_UNSUPPORT("10002", "rpc.message.unsupport"),
	
	/*STORE_ITEM_NOTEXIST("10001", "store.item.notexist"),
	STORE_ITEM_NOTPUBLISHED("10002", "store.item.notpublished"),
	STORE_ITEM_ALREADY_PUCHASED("10003", "store.item.already.purchased"),*/
	
	
	VALIDATE_CODE_NOTEQUALS("1001","validate.code.notequals"),
	VALIDATE_USERORPWD_ERROR("1002","validate.userorpwd.error"),
	VALIDATE_REQUEST_POST_METHOD("1003","validate.request.post.method"),
	
	COMMON_SYSTEM_UNKOWN_ERROR("1000", "common.system.unknow.error"),
	COMMON_BUSINESS_ERROR("999", "common.business.error"),
	
	//COMMON_UNKOWN_ERROR("1000001", "common.unkown.error"),
	
	COMMON_LOGIN("998", "common.login"),
	COMMON_DATA_NOTEXIST("997", "common.data.notexist"),
	COMMON_DATA_VALIDATE_EMPTY("996", "common.data.validate.empty"),
	COMMON_DATA_VALIDATE_ILEGAL("995", "common.data.validate.ilegal"),
	COMMON_DATA_PARAM_ERROR("994", "common.data.param.error"),
	COMMON_DATA_PARAM_MISSING("993", "common.data.param.missing"),
	COMMON_CONNECT_TIMEOUT_ERROR("992", "common.connect.timeout"),
	COMMON_DATA_ALREADYEXIST("991", "common.data.alreadyexist"),
	COMMON_DATA_ALREADYDONE("990", "common.data.alreadydone"),
	COMMON_DATA_OPERATION_CANNOT_EXECUTE("989", "common.data.operation.cannot.execute"),
	COMMON_DATA_PARAM_RANGE_ERROR("988", "common.data.param.range.error"),
	COMMON_DATA_PARAM_FLOAT_DECIMAL_PART_ERROR("987", "common.data.param.float.decimal.part.error"),
	
	COMMON_DATA_VALIDATE_LENGTH_ILEGAL("900","common.data.validate.length.ilegal"),
		
	
	AGENT_SETTLEMENT_ACTION_FINANCE_SEARCH_SORTFIELD_PARAM_ERROR("800", "agent.settlement.action.finance.search.sortfield.param.error"),
	AGENT_SETTLEMENT_ACTION_SETTLEBILLS_PRINCE_MUST_LARGERTHENZERO("801", "agent.settlement.action.settlebills.price.must.largerthenzero"), 
	AGENT_SETTLEMENT_ACTION_SETTLEBILLS_PRINCE_MUST_EQUALORLESSTHENSUB("802", "agent.settlement.action.settlebills.price.must.equalorlessthensub"),
	AGENT_SETTLEMENT_ACTION_SETTLEBILLS_AGENT_ALREADY_SETTLED_DONE("802", "agent.settlement.action.settlebills.agent.already.settled.done"),
	
	SEARCH_CONDITION_TYPE_NOTEXIST("851","search.condition.type.notexist"),
	
//	SEARCH_FAVSONG_KEYWORDS_PARSER_ATTARGET_EMPTY("799","search.friendfavsong.keywords.parser.attarget.empty"),
//	SEARCH_COMMON_KEYWORDS_PARSER_ILLEGAL("798","search.keywords.parser.illegal"),
//	SEARCH_NATURALWORD_RESPONSE("797","search.naturalword.reponse"),
	
	TASK_ALREADY_ACCEPTED_OR_COMPLETED("699","task.already.acceptedorcompleted"),
	TASK_UNDEFINED("698","task.undefined"),
	TASK_VALIDATE_PRETASKUNCOMPLETED("697","task.validate.pretaskuncompleted"),
	TASK_PARAMS_VALIDATE_ILLEGAL("696", "task.params.validate.illegal"),
	TASK_PARAMS_CHANNELTASKID_ILLEGAL("695", "task.params.channeltaskid.illegal"),
	TASK_VALIDATE_ILEGAL("694", "task.validate.ilegal"),
	TASK_ALREADY_COMPLETED("693","task.already.completed"),
	TASK_ALREADY_EXIST("692","task.already.exist"),
	TASK_NOT_EXIST("691","task.not.exist"),
	TASK_FORCE_INTERRUPT("690","task.force.interrupt"),
	
	FUNC_UNLOCK("650","func.unlock"),
	
//	FILTER_TAG_NOTEXIST("701", "filter.tag.notexist"),
//	FILTER_TAG_EXIST("702", "filter.tag.exist"),
//	FILTER_TAG_ARTIST("703", "filter.tag.artist"),
	WIFIDEVICE_SETTING_MODIFY_VALIDATE_ILEGAL("700","wifidevice.setting.modify.validate.ilegal"),
	WIFIDEVICE_SETTING_NOTEXIST("701","wifidevice.setting.notexist"),
	WIFIDEVICE_SETTING_SEQUENCE_NOTEXIST("702","wifidevice.setting.sequence.notexist"),
	WIFIDEVICE_SETTING_WORKMODE_NOCHANGED("703","wifidevice.setting.workmode.nochanged"),
	WIFIDEVICE_SETTING_WORKMODE_ROUTER2BRIDGE_DHCPC("704","wifidevice.setting.workmode.bridge.dhcpc"),
	WIFIDEVICE_SETTING_ERROR("710","wifidevice.setting.error"),
	WIFIDEVICE_SETTING_ACLMAC_MAX_MEMBER("711","wifidevice.setting.aclmac.maxmember"),
	WIFIDEVICE_SETTING_RATECONTROL_MAX_MEMBER("712","wifidevice.setting.ratecontrol.maxmember"),
	
	WIFIDEVICE_VAP_WORKMODE_NOT_SUPPORTED("730","wifidevice.vap.workmode.notsupported"),
	WIFIDEVICE_VAP_MODULE_NOT_SUPPORTED("731","wifidevice.vap.module.notsupported"),
	WIFIDEVICE_VAP_MODULE_NOT_ONLINE("732","wifidevice.vap.module.notonline"),
	WIFIDEVICE_VAP_MODULE_CMD_NOT_DEFINED("733","wifidevice.vap.module.cmd.not.defined"),
	WIFIDEVICE_VAP_MODULE_EXCEPT("734","wifidevice.vap.module.except"),
	
	WIFIDEVICE_GROUP_NOTEXIST("750","wifidevice.group.notexist"),
	WIFIDEVICE_GROUP_CURRENT_DEVICES_EMPTY("751","wifidevice.group.current.devices.empty"),
	WIFIDEVICE_GROUP_DEPENDENCY_DEVICES_EMPTY("752","wifidevice.group.dependency.devices.empty"),
	WIFIDEVICE_GROUP_ONLINE_DEVICES_EMPTY("753","wifidevice.group.devices.online.empty"),
	WIFIDEVICE_GROUP_TOO_LONG("754","wifidevice.group.devices.too.long"),
	WIFIDEVICE_GROUP_EXIST_CHILDREN("755", "wifidevice.group.devices.exist.children"),
	WIFIDEVICE_GROUP_NOTEXIST_PARENT("756", "wifidevice.group.devices.notexist.parent"),

	WIFIDEVICE_VERSION_TOO_HIGH("760", "wifidevice.version.too.high"),
	WIFIDEVICE_VERSION_INVALID_FORMAT("761", "wifidevice.version.invalid.format"),
	WIFIDEVICE_GRAY_DeviceUnitType_NOTMATCHED("770", "wifidevice.gray.deviceunittype.notmatched"),
	
	WIFISTASNIFFER_NICK_LENGTH_INVALID("781", "wifistasniffer.nick.length.invalid"),

	
	
	//WIFIDEVICE_GROUP_REMOVE_NOTEXIST("711","wifidevice.group.notexist"),
//	WIFIDEVICE_NOTEXIST("702","wifidevice.notexist"),
//	WIFIDEVICE_OFFLINE("703","wifidevice.offline"),
	
//	USER_FRD_EXIST("720","user.frd.exist"),
//	USER_FRD_APPLY_EXIST("721","user.frd.apply.exist"),
//	USER_FRD_APPLY_EXPIRE("722","user.frd.apply.expire"),
//	USER_FRD_STATE_NO_CHANGE("730","user.frd.state.no.change"),
//	USER_FRD_COUNT_LIMIT("723","user.frd.count.max"),
//	USER_ARTIFICIAL_FRDADD_LIMIT("725","user.artificial.add.limit"),
	
	LOGIN_UNAME_OR_PWD_INVALID("201","login.usernameOrpwd.invalid"),
	LOGIN_PWD_INVALID("202","login.pwd.invalid"),
	//LOGIN_COOKIE_TOKEN_EXPIRED("203","login.cookie.token.expired"),
	//LOGIN_COOKIE_VALIDATE_ACTION_EXCEPTION("204","login.cookie.token.validate.exception"),
	LOGIN_USER_DATA_NOTEXIST("205","login.user.data.notexist"),
    
	AUTH_CRON_EXPRESSION_INVALID("245","auth.cron.expression.invalid"),
	AUTH_CRON_EXPRESSION_EMPTY("246","auth.cron.expression.empty"),

	AUTH_TOKEN_INVALID("250","auth.token.invalid"),
	AUTH_TOKEN_REMOTEUID_NOTMATCH("251","auth.token.remoteuid.notmatch"),
	AUTH_TOKEN_TIMEOUT("252","auth.token.timeout"),
	AUTH_TOKEN_EMPTY("253","auth.token.empty"),
	AUTH_TOKEN_VALIDATE_EXCEPTION("254","auth.token.validate.exception"),
	AUTH_UID_EMPTY("255","auth.uid.empty"),
	
	
	AUTH_PASSWORD_EMPTY("259","auth.password.empty"),
	AUTH_EMAIL_INVALID_FORMAT("261","auth.email.format.invalid"),
	AUTH_EMAIL_DATA_EXIST("262","auth.email.data.exist"),
	AUTH_EMAIL_EMPTY("263","auth.email.empty"),
	AUTH_ORG_DATA_EXIST("264","auth.org.data.exist"),
	AUTH_UUID_INVALID_FORMAT("265","auth.uuid.format.invalid"),
	AUTH_UUID_DATA_EXIST("266","auth.uuid.data.exist"),
	AUTH_UUID_INVALID_LENGTH("267","auth.uuid.length.invalid"),
	AUTH_UUID_VALID_SELFCURRENT_HANDSET_CHANGED("268","auth.uuid.valid.selfcurrent.handsetchanged"),
	AUTH_UUID_VALID_SELFOTHER_HANDSET_CHANGED("269","auth.uuid.valid.selfother.handsetchanged"),
	AUTH_MOBILENO_INVALID_FORMAT("271","auth.mobileno.format.invalid"),
	AUTH_MOBILENO_DATA_EXIST("272","auth.mobileno.data.exist"),
	AUTH_MOBILENO_INVALID_LENGTH("273","auth.mobileno.length.invalid"),
	AUTH_MOBILENO_DATA_NOTEXIST("274","auth.mobileno.data.notexist"),
	
	AUTH_CAPTCHA_EMPTY("275","auth.captcha.empty"),
	AUTH_CAPTCHA_DATA_NOTEXIST("276","auth.captcha.data.notexist"),
	AUTH_CAPTCHA_DATA_EXPIRED("277","auth.captcha.data.expired"),
	AUTH_CAPTCHA_TIMES_NOENOUGH("278","auth.captcha.times.noenough"),
	AUTH_CAPTCHA_PATIENT_WAITING("279","auth.captcha.patient.waiting"),
	
	AUTH_NICKNAME_INVALID_FORMAT("281","auth.nickname.format.invalid"),
	AUTH_NICKNAME_DATA_EXIST("282","auth.nickname.data.exist"),
	AUTH_NICKNAME_INVALID_LENGTH("283","auth.nickname.length.invalid"),
	
	AUTH_PARAM_USERTYPE_APIGEN_FORBIDDEN("290","auth.param.usertype.apigen.forbidden"),
	//AUTH_TOPIC_TITLE_INVALID_LENGTH("231","auth.topic.title.length.invalid"),
	//AUTH_REMOTECOOKIES_EMPTY("255","auth.remotecookies.empty"),
	//AUTH_REMOTECOOKIES_INVALID("256","auth.remotecookies.invalid"),
	AUTH_PARAM_SYSTEM_WORD_RESERVED("291","auth.param.system.wordreserved"),
	AUTH_PARAM_SYSTEM_WORD_FORBIDDEN("292","auth.param.system.wordforbidden"),
	AUTH_COMMON_DATA_PARAM_NOTSUPPORTED("293", "auth.common.data.param.notsupported"),
	AUTH_MAC_INVALID_FORMAT("294","auth.mac.format.invalid"),
	
	USER_DATA_NOT_EXIST("301","user.data.notexist"),
	USER_OAUTH_DATA_ALREADY_EXIST("302","user.oauth.data.already.exist"),
	USER_TOKENS_GEN_ALREADY_FULL("310","user.tokens.gen.already.full"),
	USER_TOKENS_INVALID("311","user.tokens.invalid"),
	USER_AVATAR_UPLOAD_IMAGE_FILEFORMAT_INVALID("312","user.avatar.upload.image.fileformat.invalid"),
	USER_OPERATION_UPDPWD_NOTMATCH("313","user.operation.updpwd.notmatch"),
	USER_NICK_ALREADY_BEUSED("314","user.nick.already.beused"),
	USER_MOBILE_TOKEN_NOT_EXIST("315", "user.mobile.token.notexist"),
	USER_TYPE_WASNOT_AGENT("316", "user.type.wasnot.agent"),
	USER_TYPE_WASNOT_CONSOLE("317", "user.type.wasnot.console"),
	USER_TYPE_WASNOT_CONSOLEORAGENT("318", "user.type.wasnot.consoleoragent"),
	USER_TYPE_PARAM_VALIDATE_EMPTY("319", "user.type.param.empty"),
	USER_TYPE_NOTMATCHED("320","user.type.notmatched"),
	USER_TYPE_WASNOT_NORMAL("321", "user.type.wasnot.normal"),
	USER_MOBILE_ALREADY_BEUSED("322","user.mobile.already.beused"),
	USER_MOBILE_ATTRIBUTE_NOTEXIST("323","user.mobile.attribute.notexist"),
	
	
	USER_WALLET_WITHDRAW_OPER_DOING("324", "user.wallet.withdraw.doing"),
	USER_WALLET_CASH_LOWERTHAN_WITHDRAW_MINLIMIT("325", "user.wallet.cash.lowerthan.withdraw.minlimit"),
	USER_WALLET_CASH_NOTREACHED_MAXLIMIT("326", "user.wallet.cash.notreached.maxlimit"),
	USER_WALLET_CASH_NOT_SUFFICIENT("327", "user.wallet.cash.not.sufficient"),
	USER_WALLET_WITHDRAWPWD_SETOPER_NEEDED("328", "user.wallet.withdraw.pwd.setoper.needed"),
	USER_WALLET_VALIDATEPWD_FAILED("329","user.wallet.validatepwd.failed"),
	USER_WALLET_WITHDRAW_APPLY_STATUS_NOTMATCHED("330","user.wallet.withdraw.apply.status.notmatched"),
	
	USER_WALLET_PAYMENT_PARAM_EMPTY("331", "user.wallet.payment.param.empty"),
	USER_WALLET_PAYMENT_WASEMPTY("332", "user.wallet.payment.wasempty"),
	USER_WALLET_PAYMENT_NOTDEFINED("333", "user.wallet.payment.notdefined"),
	
	DEVICE_DATA_NOT_EXIST("340","device.data.notexist"),
	DEVICE_DATA_NOT_ONLINE("341","device.data.notonline"),
	DEVICE_ALREADY_BEBINDED("342","device.already.bebinded"),
	DEVICE_OWNER_REACHLIMIT("343","device.owner.reachlimit"),
	DEVICE_ALREADY_BEBINDED_OTHER("344","device.already.bebinded.other"),
	DEVICE_NOT_BINDED("345","device.not.binded"),
	DEVICE_NOT_MATCHED("346", "device.not.matched"),
	DEVICE_NOT_YOURBINDED("347","device.not.yourbinded"),
	DEVICE_TYPE_NOT_SUPPORTED("350","device.type.notsupported"),
	
	UPLOAD_FILE_FORMAT_INVALID("360","upload.file.format.invalid"),
	UPLOAD_FILE_FID_GEN_ERROR("361","upload.file.fid.gen.error"),
	UPLOAD_FILE_UNKNOW_ERROR("362","upload.file.unknow.error"),
	UPLOAD_FILE_CERTAIN_REFID_NOTEXIST("363","upload.file.certain.refid.notexist"),
	UPLOAD_FILE_CERTAIN_ALBUMID_NOTEXIST("364","upload.file.certain.albumid.notexist"),
	UPLOAD_FILE_CERTAIN_ARTISTID_NOTEXIST("365","upload.file.certain.artistid.notexist"),
	UPLOAD_FILE_CERTAIN_CID_NOTEXIST("366","upload.file.certain.cid.notexist"),
	UPLOAD_FILE_CERTAIN_MEDIA_CATEGORY_NOTSUPPORTED("371","upload.file.certain.media.category.notsupported"),
	UPLOAD_FILE_CERTAIN_MEDIA_SEQUENCETYPE_NOTSUPPORTED("372","upload.file.certain.media.sequencetype.notsupported"),
	SMS_COUNTRYCODE_NOTSUPPORTED("373", "sms.countrycode.notsupported"),
	
	
	/******************    Commdity    ******************/
	VALIDATE_ORDER_UMAC_UID_ILLEGAL("410","validate.order.umac.uid.illegal"),
	VALIDATE_ORDER_MAC_UMAC_ILLEGAL("411","validate.order.mac.umac.illegal"),
	VALIDATE_ORDER_DATA_NOTEXIST("412","validate.order.data.notexist"),
	VALIDATE_ORDER_STATUS_INVALID("413","validate.order.status.invalid"),
	VALIDATE_APPID_INVALID("414","validate.appid.invalid"),
	VALIDATE_ORDER_APPID_INVALID("415","validate.order.appid.invalid"),
	VALIDATE_ORDER_UMAC_INVALID("416","validate.order.umac.invalid"),
	
	
	VALIDATE_COMMDITY_DATA_NOTEXIST("450","validate.commdity.data.notexist"),
	VALIDATE_COMMDITY_NOT_ONSALE("451","validate.commdity.not.onsale"),
	VALIDATE_COMMDITY_AMOUNT_ILLEGAL("452","validate.commdity.amount.illegal"),
	VALIDATE_COMMDITY_AMOUNT_INVALID("453","validate.commdity.amount.invalid"),
	
	ORDER_PAYMENT_STATUS_PAYSUCCESSED("460","order.payment.status.paysuccessed"),
	
	//INTERNAL_COMMUNICATION_PAYMENTURL_FAILED("490","internal.communication.paymenturl.failed"),
	INTERNAL_COMMUNICATION_PAYMENTURL_RESPONSE_INVALID("491","internal.communication.paymenturl.response.invalid"),
	INTERNAL_COMMUNICATION_PAYMENTURL_RESPONSE_FALSE("492","internal.communication.paymenturl.response.false"),
	
	INTERNAL_COMMUNICATION_WITHDRAWURL_RESPONSE_INVALID("496","internal.communication.withdrawurl.response.invalid"),
	INTERNAL_COMMUNICATION_WITHDRAWURL_RESPONSE_FALSE("497","internal.communication.withdrawurl.response.false"),
	
	
	
	REQUEST_404_ERROR("404","404.pagerequest.error"),
	REQUEST_403_ERROR("403","403.pagerequest.error"),
	REQUEST_401_ERROR("401","401.unauthorized.error"),
	REQUEST_500_ERROR("500","500.pagerequest.error"),
	REQUEST_IMAGE_UPLOAD_EXCEPTION("501","501.image.upload.exception"),
	REQUEST_UNKNOW_ERROR("599","unknow.pagerequest.error");
    private String code;
    private String i18n;

    private static Map<String, ResponseErrorCode> mapKeyErrorCodes;
    
    static {
    	mapKeyErrorCodes = new HashMap<String, ResponseErrorCode>();
    	ResponseErrorCode[] codes = values();//new ThumbType[] {SMALL, MIDDLE, LARGE, ORIGINAL};
		for (ResponseErrorCode code : codes){
			mapKeyErrorCodes.put(code.code, code);
		}
	}
    
    ResponseErrorCode(String code, String i18n) {
        this.code = code;
        this.i18n = i18n;
    }

    public String code() {
        return code;
    }

    public String i18n() {
        return this.i18n;
    }

    public static ResponseErrorCode getResponseErrorCodeByCode(String code){
    	ResponseErrorCode errorcode = mapKeyErrorCodes.get(code);
    	if(errorcode == null){
    		return COMMON_BUSINESS_ERROR;
    	}	
    	return errorcode;
    }

	public static void main(String[] args) {
		System.out.println(DEVICE_DATA_NOT_EXIST.code  + ":::" + DEVICE_DATA_NOT_EXIST.i18n());
	}
}
