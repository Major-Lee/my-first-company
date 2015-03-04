package com.bhu.was.business.asyn.web.model;

public interface IDTO {
	public static char ACT_ADD = 'A';
	public static char ACT_UPDATE = 'U';
	public static char ACT_DELETE = 'D';
	public char getDtoType();
}
