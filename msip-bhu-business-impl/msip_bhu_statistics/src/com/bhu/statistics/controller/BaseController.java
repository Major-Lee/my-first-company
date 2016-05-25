package com.bhu.statistics.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.bhu.statistics.logic.IUMLogic;
import com.bhu.statistics.logic.Impl.UMLogicImpl;


public abstract class BaseController  {
	public IUMLogic staticsService = UMLogicImpl.getInstance();
	/**
	 * 杈撳嚭str瀛楃涓�
	 * 
	 * @param response
	 * @param str
	 * @throws Exception
	 */
	public void printWriterStr(HttpServletResponse response, String str) {
		try {
			response.setContentType("application/json");
			response.setContentType("text/html;charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.print(str);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
