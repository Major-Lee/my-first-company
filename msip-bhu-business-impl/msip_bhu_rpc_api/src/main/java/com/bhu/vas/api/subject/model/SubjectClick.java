package com.bhu.vas.api.subject.model;

import com.smartwork.msip.business.abstractmsd.click.multifieldclick.model.AbstractPKMultiFieldClick;

@SuppressWarnings("serial")
public class SubjectClick extends AbstractPKMultiFieldClick<Integer> {
	public static final String Field_Up = "up";
	public static final String Field_Down = "down";
	public static final String Field_Estimatetimes = "estimatetimes";
	public static final String Field_Abstracts = "abstracts";
	private long up;
	private long down;
	//评价次数
	private long estimatetimes;
	private long abstracts;

	public SubjectClick() {
		super();
	}
	public SubjectClick(Integer id) {
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
	public long getEstimatetimes() {
		return estimatetimes;
	}
	public void setEstimatetimes(long estimatetimes) {
		this.estimatetimes = estimatetimes;
	}
	public long getEvaluate(){
		long eva = up - down;
		if(eva >= 0){
			return eva;
		}
		return 0;
	}
	
	public long getAbstracts() {
		return abstracts;
	}
	public void setAbstracts(long abstracts) {
		this.abstracts = abstracts;
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
