package com.bhu.vas.api.subject.model;

import java.io.Serializable;

import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.StringHelper;

@SuppressWarnings("serial")
public class TimeFragmentPK implements Serializable {
	private String currentid;
	private int subjectid;
	
	public TimeFragmentPK(){
	}
	
	public TimeFragmentPK(int subjectid){
		this.currentid = DateTimeHelper.formatDate(DateTimeHelper.FormatPattern5);
		this.subjectid = subjectid;
	}
	
	public TimeFragmentPK(String currentid,int subjectid){
		this.currentid = currentid;
		this.subjectid = subjectid;
	}
	
	public String getCurrentid() {
		return currentid;
	}
	public void setCurrentid(String currentid) {
		this.currentid = currentid;
	}
	public int getSubjectid() {
		return subjectid;
	}
	public void setSubjectid(int subjectid) {
		this.subjectid = subjectid;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(currentid).append(StringHelper.MINUS_CHAR_GAP).append(subjectid);
		return sb.toString();
	}
	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(o instanceof TimeFragmentPK){
			TimeFragmentPK oo = (TimeFragmentPK)o;
			return (this.currentid.equals(oo.currentid) && this.subjectid==oo.subjectid);
		}
		return false;
	}
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
}
