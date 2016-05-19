package base;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;


public abstract class BaseController  {
	/**
	 * 输出str字符串
	 * 
	 * @param response
	 * @param str
	 * @throws Exception
	 */
	public void printWriterStr(HttpServletResponse response, String str) {
		try {
			response.setContentType("application/json");
			response.setContentType("text/html;charset=UTF-8");
			response.setHeader("Access-Control-Allow-Origin", "*");
			PrintWriter out = response.getWriter();
			out.print(str);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
