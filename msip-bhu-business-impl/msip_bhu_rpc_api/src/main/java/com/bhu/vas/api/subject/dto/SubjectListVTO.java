package com.bhu.vas.api.subject.dto;

import java.util.List;

import com.bhu.vas.api.tag.dto.TagDTO;

/**
 * 用于在列表页显示文章的数据
 * @author lawliet
 *
 */
public class SubjectListVTO {
	private int id; //文章id
	private String uuid; //文章uuid
	private String title; //文章title
	private String source; //文章来自
	private String url; //原文url
	private String image; //文章包含的图片地址
	private String authors;//原文作者
	private long vc; //view count
	private long sc; //share count
	private long ac; //abstract count
	private long cc; //comment count
	private List<TagDTO> tags;//系统内部tags
	private String third_tags; //第三方自带的tags
	private String estimate; //星级
	private String u_estimate;//用户对此文章的评价星级
	private long evaluate; //顶和踩的评价值
	private long f_evaluate;//周期区间顶和踩的评价值
	private long nf_evaluate;//最新的周期区间顶和踩的评价值
	private long ts; //文章上传时间
	//当前用户up 或 down此文章的状态
	private String ud;
	private int uid;
	private String hlc;//high light content
	private long wxc;//微信分享次数
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	
	public String getAuthors() {
		return authors;
	}

	public void setAuthors(String authors) {
		this.authors = authors;
	}

	public List<TagDTO> getTags() {
		return tags;
	}

	public void setTags(List<TagDTO> tags) {
		this.tags = tags;
	}

	public String getThird_tags() {
		return third_tags;
	}

	public void setThird_tags(String third_tags) {
		this.third_tags = third_tags;
	}

	public void setF_evaluate(long f_evaluate) {
		this.f_evaluate = f_evaluate;
	}

	public long getVc() {
		return vc;
	}

	public void setVc(long vc) {
		this.vc = vc;
	}

	public long getSc() {
		return sc;
	}

	public void setSc(long sc) {
		this.sc = sc;
	}

	public long getAc() {
		return ac;
	}

	public void setAc(long ac) {
		this.ac = ac;
	}

	public long getCc() {
		return cc;
	}

	public void setCc(long cc) {
		this.cc = cc;
	}

	public void setEvaluate(long evaluate) {
		this.evaluate = evaluate;
	}

	public String getEstimate() {
		return estimate;
	}

	public void setEstimate(String estimate) {
		this.estimate = estimate;
	}
	
	public String getU_estimate() {
		return u_estimate;
	}

	public void setU_estimate(String u_estimate) {
		this.u_estimate = u_estimate;
	}

	public long getEvaluate() {
		return evaluate;
	}

	public long getTs() {
		return ts;
	}

	public void setTs(long ts) {
		this.ts = ts;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public long getWxc() {
		return wxc;
	}

	public void setWxc(long wxc) {
		this.wxc = wxc;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public long getF_evaluate() {
		if(f_evaluate >= 0){
			return f_evaluate;
		}
		return 0;
	}

	public long getNf_evaluate() {
		if(nf_evaluate >= 0){
			return nf_evaluate;
		}
		return 0;
	}

	public void setNf_evaluate(double nf_evaluate) {
		this.nf_evaluate = Math.round(nf_evaluate);
	}

	public void setF_evaluate(double f_evaluate) {
		this.f_evaluate = Math.round(f_evaluate);
	}

	public String getUd() {
		return ud;
	}

	public void setUd(String ud) {
		this.ud = ud;
	}

	public String getHlc() {
		return hlc;
	}

	public void setHlc(String hlc) {
		this.hlc = hlc;
	}

}
