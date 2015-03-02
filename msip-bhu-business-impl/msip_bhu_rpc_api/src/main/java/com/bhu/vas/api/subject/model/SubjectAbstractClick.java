package com.bhu.vas.api.subject.model;

import com.smartwork.msip.business.abstractmsd.click.multifieldclick.model.AbstractPKMultiFieldClick;

@SuppressWarnings("serial")
public class SubjectAbstractClick extends AbstractPKMultiFieldClick<Integer> {
	public static final String Field_Up = "up";
	public static final String Field_Down = "down";
	private long up;
	private long down;
	private int subjectid;
	
	public SubjectAbstractClick() {
		super();
	}
	public SubjectAbstractClick(Integer id) {
		super(id);
	}
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
	public int getSubjectid() {
		return subjectid;
	}
	public void setSubjectid(int subjectid) {
		this.subjectid = subjectid;
	}
	public long getEvaluate(){
		long eva = up - down;
		if(eva >= 0){
			return eva;
		}
		return 0;
	}
	@Override
	protected Class<Integer> getPKClass() {
		return Integer.class;
	}
	@Override
	public Integer getId() {
		return super.getId();
	}
	@Override
	public void setId(Integer id) {
		super.setId(id);
	}
}
