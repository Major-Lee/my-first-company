package com.bhu.vas.api.statistics.model;

import com.bhu.vas.api.subject.model.TimeFragmentPK;
import com.smartwork.msip.cores.orm.model.BasePKModel;

@SuppressWarnings("serial")
public class SubjectStatisticsDaily extends BasePKModel<TimeFragmentPK> {
	private long up;
	private long down;

	public long getUp() {
		return up;
	}
	public void setUp(long up) {
		this.up = up;
	}
	public long getDown() {
		return down;
	}
	public void setDown(long down) {
		this.down = down;
	}

	public long getEvaluate(){
		long eva = up - down;
		if(eva >= 0){
			return eva;
		}
		return 0;
	}
	
	@Override
	protected Class<TimeFragmentPK> getPKClass() {
		return TimeFragmentPK.class;
	}
	
	public String getCurrentid() {
		if(this.getId() == null) return null;
		return this.getId().getCurrentid();
	}
	public void setCurrentid(String currentid) {
		if(this.getId() == null) this.setId(new TimeFragmentPK());
		this.getId().setCurrentid(currentid);
	}
	
	public int getSubjectid() {
		if(this.getId() == null) return 0;
		return this.getId().getSubjectid();
	}
	public void setSubjectid(int subjectid) {
		if(this.getId() == null) this.setId(new TimeFragmentPK());
		this.getId().setSubjectid(subjectid);
	}
}
