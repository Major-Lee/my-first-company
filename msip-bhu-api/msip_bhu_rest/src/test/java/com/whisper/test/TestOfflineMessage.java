package com.whisper.test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.smartwork.im.message.Message;
import com.smartwork.im.message.MessageBuilder;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.whisper.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.whisper.business.bucache.redis.serviceimpl.offline.notify.UserOfflineMessageService;

public class TestOfflineMessage {
	public static void main(String[] argv){
		//UserOfflineMessageService.getInstance().
		/*Set<String> notInSessionUsers = new HashSet<String>();
		notInSessionUsers.add("542010");
		notInSessionUsers.add("542013");
		notInSessionUsers.add("542016");
		notInSessionUsers.add("542020");*/
//		
//		Message message = MessageBuilder.builderNotifyTopicAnswerMessage("541981", "542020", "1530","test", "杜娜理峰娜晓金广理范周鹏","","","", true);
//		Set<String> notInSessionUsers = new HashSet<String>();
//		notInSessionUsers.add("542020");
//		UserOfflineMessageService.getInstance().userOffline_lpush_pipeline_samevalue(notInSessionUsers,BusinessKeyDefine.Present.UserOfflineType_TopicPrefixKey,
//				JsonHelper.getJSONString(message));
//		List<String> rets = UserOfflineMessageService.getInstance().userOfflineAll("541919", BusinessKeyDefine.Present.UserOfflineType_TopicPrefixKey);
//		
//		System.out.println(rets);
	}
}
