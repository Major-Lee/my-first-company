package com.bhu.vas.api.helper;

import com.bhu.vas.api.dto.ScoreDTO;
import com.smartwork.msip.cores.helper.ConvertHelper;
import com.smartwork.msip.localunit.RandomData;

public class ScoreHelper {
	//流量区间
	public enum FlowScope{
		FlowLevel1(0,5,20),
		FlowLevel2(5,20,30),
		FlowLevel3(20,30,40),
		FlowLevel4(30,45,50),
		FlowLevel5(45,-1,60),
		;
		long start;
		long end;
		int score;
		FlowScope(long start,long end,int score){
			this.start = start*1024*1024l;
			if(end > 0)
				this.end = end*1024*1024l;
			else this.end = end;
			this.score = score;
		}
		public long getStart() {
			return start;
		}
		public void setStart(long start) {
			this.start = start;
		}
		public long getEnd() {
			return end;
		}
		public void setEnd(long end) {
			this.end = end;
		}
		public int getScore() {
			return score;
		}
		public void setScore(int score) {
			this.score = score;
		}
		public boolean wasInScore(long flow){
			if(end != -1){
				return ( flow>=start && flow <=end);
			}else{//end =-1 代表无穷大
				return flow>=start;
			}
		}
	}
	
	//连接数区间
	public enum StaScope{
		StaLevel1(0,5,10),
		StaLevel2(5,20,20),
		StaLevel3(20,30,30),
		StaLevel4(30,45,35),
		StaLevel5(45,-1,40),
		;
		int start;
		int end;
		int score;
		StaScope(int start,int end,int score){
			this.start = start;
			this.end = end;
			this.score = score;
		}
		public int getStart() {
			return start;
		}
		public void setStart(int start) {
			this.start = start;
		}
		public int getEnd() {
			return end;
		}
		public void setEnd(int end) {
			this.end = end;
		}
		public int getScore() {
			return score;
		}
		public void setScore(int score) {
			this.score = score;
		}
		
		public boolean wasInScore(int sta){
			if(end != -1){
				return ( sta>=start && sta <=end);
			}else{//end =-1 代表无穷大
				return sta>=start;
			}
		}
	}
	
	public enum KoHint{
		KoLevel1(0,50,"30%"),
		KoLevel2(50,60,"40%"),
		KoLevel3(60,70,"60%"),
		KoLevel4(70,90,"80%"),
		KoLevel5(90,-1,"95%"),
		;
		int start;
		int end;
		String hint;
		KoHint(int start,int end,String hint){
			this.start = start;
			this.end = end;
			this.hint = hint;
		}
		public int getStart() {
			return start;
		}
		public void setStart(int start) {
			this.start = start;
		}
		public int getEnd() {
			return end;
		}
		public void setEnd(int end) {
			this.end = end;
		}
		public String getHint() {
			return hint;
		}
		public void setHint(String hint) {
			this.hint = hint;
		}
		public boolean wasInScore(int score){
			if(end != -1){
				return ( score>=start && score <=end);
			}else{//end =-1 代表无穷大
				return score>=start;
			}
		}
	}
	
	public static final long flow_max = 50*1024*1024l;//50M
	public static final int sta_max = 40;
	
	
	
	public static ScoreDTO analyse(long flow,int sta){
		FlowScope matched_flow = null;
		FlowScope[] fscopes = FlowScope.values();
		for(FlowScope fscope:fscopes){
			if(fscope.wasInScore(flow)){
				matched_flow = fscope;
				break;
			}
		}
		StaScope matched_sta = null;
		StaScope[] sscopes = StaScope.values();
		for(StaScope sscope:sscopes){
			if(sscope.wasInScore(sta)){
				matched_sta = sscope;
				break;
			}
		}
		int score = matched_flow.getScore()+matched_sta.getScore()+RandomData.intNumber(0, 5);
		if(score >=100){
			score = 100- RandomData.intNumber(0, 3);
		}
		KoHint matched_hint = null;
		KoHint[] hints = KoHint.values();
		for(KoHint hint:hints){
			if(hint.wasInScore(score)){
				matched_hint = hint;
				break;
			}
		}
		
		return new ScoreDTO(score,matched_hint.getHint());
		//System.out.println(matched_flow.getScore()+matched_sta.getScore());
	}
	public static void main(String[] argv){
		//System.out.println((double)3261840/flow_max);
		System.out.println(ConvertHelper.speedByteFormat(3261840l));
		ScoreDTO analyse = analyse(30*1024*1024l,40);
		System.out.println(analyse);
	}
}
