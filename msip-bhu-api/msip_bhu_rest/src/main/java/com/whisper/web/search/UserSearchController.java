package com.whisper.web.search;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.es.request.QueryResponse;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;
import com.whisper.api.frd.dto.UserFrdDTO;
import com.whisper.api.frd.model.UserFrdPK;
import com.whisper.api.frd.model.UserFrdRelation;
import com.whisper.api.user.dto.UserDTO;
import com.whisper.api.user.model.User;
import com.whisper.business.facade.ucenter.UserFrdRelationFacadeService;
import com.whisper.business.search.service.user.UserSearchService;
import com.whisper.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.whisper.msip.web.AsynMessageController;

@Controller
@RequestMapping("/search")
public class UserSearchController extends AsynMessageController{
	@Resource
	private UserSearchService userSearchService;
	
	@Resource
	private UserFrdRelationFacadeService userFrdRelationFacadeService;
	/**
	 * 根据nick或者手机号搜索用户
	 * @param request
	 * @param response
	 * @param uid
	 * @param keyword 手机号或nick或拼音
	 * @param start
	 * @param size
	 */
	@ResponseBody()
	@RequestMapping(value="/account",method={RequestMethod.GET,RequestMethod.POST})
	public void create(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true, value="q") String keyword,
			@RequestParam(required = false, defaultValue = "0", value = "st") int start,
			@RequestParam(required = false, defaultValue = "10", value = "ps") int size
			) {
		
		User user = this.userService.getById(uid);
		validateUserNotNull(user);
		
		try{
			long start_ts = System.currentTimeMillis();
			QueryResponse<List<UserFrdDTO>> searchResponse = null;
			//以用户标识进行搜索
			if(keyword.contains(StringHelper.WELL_STRING_GAP)){
				searchResponse = userSearchService.symbolSearchUsers(uid, keyword, start, size);
			}else{
				searchResponse = userSearchService.prefixSearchUsers(uid, keyword, start, size);
			}
			
			long used_ts = System.currentTimeMillis() - start_ts;
			System.out.println("--------------------------------/search/account : " + keyword + " uts : " + used_ts + " ms");
			this.markFrdshps(uid, searchResponse.getResult());
			int total = searchResponse.getTotal();
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(JsonHelper.getPartialData(searchResponse.getResult(), start, size, total)));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}finally{
			
		}
	}
	
	/**
	 * 标记是否是好友
	 * @param uid
	 * @param dtos
	 */
	protected void markFrdshps(int uid, List<UserFrdDTO> dtos){
		if(dtos == null || dtos.isEmpty()) return;
		
		List<Integer> uids = new ArrayList<Integer>();
		for(UserFrdDTO dto : dtos){
			uids.add(dto.getId());
		}
		//List<Integer> frdids = userFrdRelationFacadeService.getVisibleFrdidsByIds(uid, uids);
		List<UserFrdRelation> frd_entitys = userFrdRelationFacadeService.getVisibleFrdRelationByIds(uid, uids);
		if(frd_entitys.isEmpty()) return;
		
		UserFrdRelation match_frd_entity = new UserFrdRelation(new UserFrdPK());
		UserFrdRelation index_frd_entity = null;
		for(UserFrdDTO dto : dtos){
			match_frd_entity.getId().setUid(uid);
			match_frd_entity.getId().setFrdid(dto.getId());
			int index = frd_entitys.indexOf(match_frd_entity);
			if(index != -1){
				dto.setOw(true);
				index_frd_entity = frd_entitys.get(index);
				dto.setFrdshp(index_frd_entity.isFrdshp());
				dto.setFrd_ts(index_frd_entity.getCreated_at().getTime());
				dto.setFrom(index_frd_entity.getFrdfrom());
				dto.setAdn(index_frd_entity.getAddress_nick_decode());
				continue;
			}
		}
	}
}

