package com.bhu.vas.business.ds.agent.mdto;

import java.util.ArrayList;
import java.util.List;

import com.bhu.vas.api.dto.charging.ActionBuilder.Hint;

public class LineRecords {
	private List<LineRecord> records = new ArrayList<>();
	private LineRecord current;
	public List<LineRecord> getRecords() {
		return records;
	}
	public void setRecords(List<LineRecord> records) {
		this.records = records;
	}
	public LineRecord getCurrent() {
		return current;
	}
	public void setCurrent(LineRecord current) {
		this.current = current;
	}
	
	
	public boolean hasCurrent(){
		return current != null;
	}
	
	public boolean currentHasUp(){
		return current != null && current.getUts() > 0;
	}
	
	public boolean currentHasDown(){
		return current != null && current.getDts() > 0;
	}
	
	public boolean wasEmptyStatus(){
		return records.isEmpty() && current == null;
	}
	
	public boolean wasWaittingUpStatus(){
		return current == null;
	}
	
	public boolean wasWaittingDownStatus(){
		return current != null;
	}
	
	public void forceCurrentDownOper(long down_ts,Hint hint){
		this.getCurrent().setDts(down_ts);
		this.getCurrent().appendHint(hint);//"日志截尾，补齐到当天最后时间");
		this.getRecords().add(this.getCurrent());
		this.setCurrent(null);
	}
}
