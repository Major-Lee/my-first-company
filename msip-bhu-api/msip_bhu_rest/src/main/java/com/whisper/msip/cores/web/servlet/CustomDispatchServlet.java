package com.whisper.msip.cores.web.servlet;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;

/**
 * 优化ModelAndView 在springmvc中频繁创建对象，通过pool或先创建ModelAndView对象，进行复用，减少系统开销
 * 同样对于ModelMap对象的频繁创建也是大量开销系统资源，为了降低此开销，尽量使用getModeAndView().getModelMap()而不是new ModelMap()
 * @author Edmond Lee
 *
 * 用法：
 * 	ModelAndView mv = CustomDispatchServlet.getModeAndView();   
	mv.setViewName("yourPage");   
	mv.getModel().put("attribute", "yourObject");
	  
	此为原型示例，需改进
 */
public class CustomDispatchServlet extends DispatcherServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static List<ModelAndView> modeAndViews = new ArrayList<ModelAndView>(20);

	static {
		for (int i = 0; i < 20; i++) { // 缓存量为20
			modeAndViews.add(new ModelAndView());
		}
	}

	public void render(ModelAndView mv, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		super.render(mv, request, response);
		releaseModelAndView(mv);
	}

	// 回收ModelAndView
	private void releaseModelAndView(ModelAndView mv) {
		mv.clear();
		mv.getModelMap().clear();
		synchronized (modeAndViews) {
			modeAndViews.add(mv);
		}
	}

	// 取ModelAndView
	public static ModelAndView getModeAndView() {
		synchronized (modeAndViews) {
			if (modeAndViews.size() > 0)
				return modeAndViews.remove(0);
			else
				return new ModelAndView();
		}
	}
	
	//getModelMap()的代码： 
/*	public ModelMap getModelMap() { 
		if (this.model == null) { 
		this.model = new ModelMap(); 
		} 
		return this.model; 
	}*/
}
