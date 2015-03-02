package com.smartwork.msip.jdo;

import com.smartwork.msip.cores.helper.JsonHelper;

public class Test {
	public static void main(String[] argv){
		/*String json = "{\"original\":\"王菲 - 红豆\","+
				"\"timeDot\": {"+
				"210:[\"钢琴\", \"跳音\"],"+
				"250:[\"民谣吉他\", \"分解和弦\"],"+
				"280:[\"民谣吉他\",\"扫弦\"],"+
				"300:[\"提琴\",\"震奏\"]}}";*/
		/*String json = new TrackDetailTest().genDetailJson();
		System.out.println(json);
		
		DetailDTO dto = new TrackDetailTest().genDetailDTO(json);
		System.out.println(dto.getOriginal());
		dto.getTimeDot().entrySet();
		for (Map.Entry<String, List<String>> entry : dto.getTimeDot().entrySet()) {
			String key = entry.getKey();
			List<String> list = entry.getValue();
			StringBuilder sb = new StringBuilder(key);
			for(String str : list){
				sb.append(StringHelper.WHITESPACE_CHAR_GAP).append(str);
			}
			System.out.println(sb.toString());
		}*/
		String ret = "{\"original\":\"刘德华-笨小孩\",\"timeDot\":{\"210\":[\"钢琴\",\"跳音\"],\"250\":[\"民谣吉他\",\"分解和弦\"]}}";
		System.out.println(JsonHelper.getJSONString(ResponseSuccess.embed(ret), false));
	}
}
