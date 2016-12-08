package com.bhu.vas.api.rpc.message.dto;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * 腾讯im批量导入用户接口DTO
 * @author fengshibo
 * 单个用户名长度不超过32字节
 *	单次最多导入100个用户名
 */
@SuppressWarnings("serial")
public class TimMulImportAccountDTO implements java.io.Serializable{
	@JsonProperty("Accounts")
	private List<String> accounts;

	public List<String> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<String> accounts) {
		this.accounts = accounts;
	}
	
	public static TimMulImportAccountDTO  buildTimMULImportAccountDTO(String accounts){
		if (StringUtils.isEmpty(accounts))
			return null;
		TimMulImportAccountDTO dto = new TimMulImportAccountDTO();
		dto.setAccounts(Arrays.asList(accounts.split(",")));
		return dto;
	}
}
