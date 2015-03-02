package com.bhu.vas.api.subject.model;

import com.smartwork.msip.business.abstractmsd.click.multifieldclick.model.AbstractPKMultiFieldClick;

@SuppressWarnings("serial")
public class DailySubjectClick extends AbstractPKMultiFieldClick<TimeFragmentPK> {
	public static final String Field_Up = "up";
	public static final String Field_Down = "down";
	private long up;
	private long down;

	public DailySubjectClick() {
		super();
	}
	public DailySubjectClick(TimeFragmentPK id) {
		super(id);
	}
/*
	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}*/
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
