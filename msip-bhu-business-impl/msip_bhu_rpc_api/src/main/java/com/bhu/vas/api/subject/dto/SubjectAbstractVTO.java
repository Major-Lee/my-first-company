package com.bhu.vas.api.subject.dto;

/**
 * 用于显示文章的摘要列表实体
 * @author lawliet
 *
 */
public class SubjectAbstractVTO {
	private int id; //文章摘要id
	private int subjectid; //文章id
	private int uid; //摘要分享用户id
	private String abt; //摘要内容
	
	private long evaluate; //顶和踩的评价值
	private long ts; //摘要上传时间

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getAbt() {
		return abt;
	}

	public void setAbt(String abt) {
		this.abt = abt;
	}

	public long getEvaluate() {
		return evaluate;
	}

	public void setEvaluate(long evaluate) {
		this.evaluate = evaluate;
	}

	public long getTs() {
		return ts;
	}

	public void setTs(long ts) {
		this.ts = ts;
	}

	public int getSubjectid() {
		return subjectid;
	}

	public void setSubjectid(int subjectid) {
		this.subjectid = subjectid;
	}


}
