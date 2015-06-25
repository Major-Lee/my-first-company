package com.bhu.vas.api.dto;

public class ScoreDTO{
	
	private int score;
	private String hint;
	public ScoreDTO(){
		
	}
	public ScoreDTO(int score,String hint) {
		this.score = score;
		this.hint = hint;
	}
	public String getHint() {
		return hint;
	}
	public void setHint(String hint) {
		this.hint = hint;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	
	public String toString(){
		return String.format("score[%s] hint[%s]", score,hint);
	}
}
