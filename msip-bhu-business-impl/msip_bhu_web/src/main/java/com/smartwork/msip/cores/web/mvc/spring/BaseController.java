package com.smartwork.msip.cores.web.mvc.spring;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;

import com.smartwork.msip.cores.web.exception.BusinessException;
import com.smartwork.msip.cores.web.mvc.WebHelper;
import com.smartwork.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseStatus;


public abstract class BaseController implements ServletContextAware {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	protected static ServletContext servletContext;
	protected static String contextPath = "";
	protected String defaultErrorMessage = "操作错误，请重试";
	
	
	//protected final static String defaultErrorMessage = "操作错误，请重试";
	protected void prepareModelAndView(ModelAndView mv){
		
	}
	
	protected void prepareCtx4ModelAndView(ModelAndView mv) {
		//System.out.println(this.servletContext.getServletContextName());
		/*if(contextPath == null){
			contextPath = this.servletContext.getInitParameter("contextPath");
			System.out.println(contextPath);
		}*/
		//mv.addObject("ctx",getContextPath());
    }

	protected static String getContextPath(){
		return servletContext.getContextPath();
		/*if(StringUtils.isEmpty(contextPath)){
			contextPath = this.servletContext.getInitParameter("contextPath");
		}
		return contextPath;*/
	}
	
/*	protected void writeValidateCookies(
			HttpServletRequest request,
			HttpServletResponse response,int uid,String email){
		LoginToken loginToken = LoginTokenHelper.generateLoginToken(uid,email, WebHelper.getRemoteAddr(request));
		//CookieUtils.setCookie(request, response, "local.uid", email);
		//String json = JsonHelper.getObjectData(DateConvertType.TOSTRING_EN_COMMON_YMDHMS, loginToken, false, "lt","ls","it");
		CookieUtils.setCookie(request, response, LoginTokenHelper.RemoteCookieName, LoginTokenHelper.toLoginTokenJson(loginToken));
	}
	protected void resetCookies(
			HttpServletRequest request,
			HttpServletResponse response){
		//CookieUtils.deleteCookie(request, response, CookieUtils.getCookie(request, "local.uid"));
		CookieUtils.deleteCookie(request, response, CookieUtils.getCookie(request, LoginTokenHelper.RemoteCookieName));
	}*/
	
	@ExceptionHandler(BusinessI18nCodeException.class)
    protected ModelAndView rpcBusinessI18nCodeException(BusinessI18nCodeException ex, HttpServletRequest request, HttpServletResponse response) {
        logging(ex, request);
        response.setStatus(ResponseStatus.OK.getStatus());
        if (isJsonRequest(request)) {
        	String jsonpcallback = request.getParameter("jsonpcallback");
        	if(StringUtils.isNotEmpty(jsonpcallback))
        		SpringMVCHelper.renderJsonp(response,jsonpcallback, ResponseError.embed(ex.getErrorCode(), ex.getPayload()));//, ex.locateResponseErrorCode()));
        	else	
        		SpringMVCHelper.renderJson(response, ResponseError.embed(ex.getErrorCode(), ex.getPayload()));//, ex.locateResponseErrorCode()));
            return null;
        }
        if(isXmlRequest(request)){
        	SpringMVCHelper.renderXml(response, ResponseError.embed(ex.getErrorCode(), ex.getPayload()));//, ex.locateResponseErrorCode()));
            return null;        	
        }
        ModelAndView mv = new ModelAndView();
        StaticResultController.redirectError(mv, servletContext.getContextPath()+"/index.html", ex.getMessage());
        return mv;
    }
	
	@ExceptionHandler(BusinessException.class)
    protected ModelAndView businessException(BusinessException ex, HttpServletRequest request, HttpServletResponse response) {
        //logging(ex, request);
        response.setStatus(ex.getResponse_status().getStatus());
        if (isJsonRequest(request)) {
        	String jsonpcallback = request.getParameter("jsonpcallback");
        	if(StringUtils.isNotEmpty(jsonpcallback))
        		SpringMVCHelper.renderJsonp(response,jsonpcallback, ResponseError.embed(ex.getErrorCode(),ex.getPayload()));
        	else	
        		SpringMVCHelper.renderJson(response, ResponseError.embed(ex.getErrorCode(),ex.getPayload()));
            return null;
        }
        if(isXmlRequest(request)){
        	SpringMVCHelper.renderXml(response, ResponseError.embed(ex.getErrorCode(),ex.getPayload()));
            return null;        	
        }
        ModelAndView mv = new ModelAndView();
        StaticResultController.redirectError(mv, servletContext.getContextPath()+"/index.html", ex.getMessage());
        return mv;
    }
	
    /**
     * 处理通用的RuntimeException异常
     *
     * @param ex
     * @param request
     * @param response
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    protected ModelAndView runtimeException(RuntimeException ex, HttpServletRequest request, HttpServletResponse response) {
    	logging(ex, request);
    	response.setStatus(ResponseStatus.InternalServerError.getStatus());
        //Response resp = new Response(false,ex.getMessage());
        if (isJsonRequest(request)) {
        	/*if(isJsonUploadRequest(request))
        		SpringMVCHelper.renderHtml(response,JsonHelper.getJSONString(resp));
        	else*/
        	String jsonpcallback = request.getParameter("jsonpcallback");
        	if(StringUtils.isNotEmpty(jsonpcallback))
        		SpringMVCHelper.renderJsonp(response,jsonpcallback, ResponseError.BUSINESS_ERROR);
        	else	
        		SpringMVCHelper.renderJson(response, ResponseError.BUSINESS_ERROR);
            return null;
        }
        if(isXmlRequest(request)){
        	SpringMVCHelper.renderXml(response, ResponseError.BUSINESS_ERROR);
            return null;        	
        }

        ModelAndView mv = new ModelAndView();
        
        StaticResultController.redirectError(mv, servletContext.getContextPath()+"/index.html", ex.getMessage());
        return mv;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ModelAndView parameterExceptionHandler(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        logging(ex, request);
        response.setStatus(ResponseStatus.BadRequest.getStatus());
        if (isJsonRequest(request)) {
        	String jsonpcallback = request.getParameter("jsonpcallback");
        	if(StringUtils.isNotEmpty(jsonpcallback))
        		SpringMVCHelper.renderJsonp(response,jsonpcallback, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_MISSING));
        	else	
        		SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_MISSING));
            return null;
        }
        if(isXmlRequest(request)){
        	SpringMVCHelper.renderXml(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_MISSING));
            return null;        	
        }
        ModelAndView mv = new ModelAndView();
        StaticResultController.redirectError(mv, "/index.html", ex.getMessage());
        return mv;
    }
    /**
     * 处理通用的Exception异常
     *
     * @param ex
     * @param request
     * @param response
     * @return
     */
    @ExceptionHandler(Exception.class)
    protected ModelAndView exceptionHandler(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        logging(ex, request);
        //Response resp = new Response(false,ex.getMessage());
        if (isJsonRequest(request)) {
        	/*if(isJsonUploadRequest(request))
        		SpringMVCHelper.renderHtml(response,JsonHelper.getJSONString(resp));
        	else*/
        	String jsonpcallback = request.getParameter("jsonpcallback");
        	if(StringUtils.isNotEmpty(jsonpcallback))
        		SpringMVCHelper.renderJsonp(response,jsonpcallback, ResponseError.SYSTEM_ERROR);
        	else	
        		SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
            return null;
        }
        if(isXmlRequest(request)){
        	SpringMVCHelper.renderXml(response, ResponseError.SYSTEM_ERROR);
            return null;        	
        }
        ModelAndView mv = new ModelAndView();
        StaticResultController.redirectError(mv, "/index.html", ex.getMessage());
        return mv;
    }	
    protected void loggingWarn(String errorCode, HttpServletRequest request) {
    	String remoteIp = WebHelper.getRemoteAddr(request);
    	/*Enumeration<?> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()) {
          String headerName = (String)headerNames.nextElement();
          System.out.println(headerName+":"+request.getHeader(headerName));
        }*/
        logger.error(String.format("exception catched when access [%s] via[ip:%s] for RequestErrorCode [%s]", request.getRequestURI(),remoteIp, errorCode));
    }
    protected void logging(Throwable ex, HttpServletRequest request) {
    	String remoteIp = WebHelper.getRemoteAddr(request);
        logger.error(String.format("exception catched when access [%s] via[ip:%s] for message [%s]", request.getRequestURI(),remoteIp, ex.getMessage()), ex);
        ex.printStackTrace();
    }
    
	@Override
	public void setServletContext(ServletContext servletContext) {
		BaseController.servletContext = servletContext;
	}
	
    protected boolean isJsonRequest(HttpServletRequest request) {
    	return SpringMVCHelper.isJsonRequest(request);
        //return request.getRequestURI().toLowerCase().endsWith(".json");
    }
    /*protected boolean isJsonpRequest(HttpServletRequest request) {
        return StringUtils.isNotEmpty(request.getParameter("jsonpcallback"));
    }*/
    protected boolean isJsonUploadRequest(HttpServletRequest request) {
        return request.getRequestURI().toLowerCase().endsWith("upload.json");
    }
    
    protected boolean isXmlRequest(HttpServletRequest request) {
        return request.getRequestURI().toLowerCase().endsWith(".xml");
    }    
    
    public String getDefaultErrorMessage() {
        return defaultErrorMessage;
    }

    public void setDefaultErrorMessage(String defaultErrorMessage) {
        this.defaultErrorMessage = defaultErrorMessage;
    }    
    
/*    @RequestMapping("/success.html")
    public ModelAndView success(ModelAndView mv,
                                @RequestParam(required = false) String url,
                                @RequestParam(required = false) String message,
                                @RequestParam(required = false) Integer legacy) {
        mv = new ModelAndView("/success");
        mv.addObject(StaticResultController.URL, StaticResultController.getReturnUrl(url));
        mv.addObject(StaticResultController.MESSAGE, StaticResultController.getSuccessMessage(message));
        mv.addObject(StaticResultController.LEGACY, StaticResultController.getLegacy(legacy));
        return mv;
    }

    @RequestMapping("/error.html")
    public ModelAndView error(ModelAndView mv,
                              @RequestParam(required = false) String url,
                              @RequestParam(required = false) String message,
                              @RequestParam(required = false) Integer legacy) {
        mv = new ModelAndView("/error");
        mv.addObject(StaticResultController.URL, StaticResultController.getReturnUrl(url));
        mv.addObject(StaticResultController.MESSAGE, StaticResultController.getErrorMessage(message));
        mv.addObject(StaticResultController.LEGACY, StaticResultController.getLegacy(legacy));
        return mv;
    }  */  
    
    
    //private static final String COMMON_ERROR = "CommonError";

    //private static final String COMMON_ERROR_ATTRIBUTE = "commonError";

/*    @ExceptionHandler({NoSuchRequestHandlingMethodException.class})
    public ModelAndView handleNotFound(Exception ex, HttpServletRequest request) {
	    String path = request.getPathInfo();
	    logger.warn("The page not found: " + path, ex);
	    ModelAndView mav = composeModelAndView(path, ex);
	    return mav;
    }*/


/*    @ExceptionHandler({ServiceException.class})

    public ModelAndView handleServiceException(Exception ex, HttpServletRequest request) {

    String path = request.getPathInfo();

    log.warn("Can't accomplish the request because of service layer error when access to: " + path, ex);

    ModelAndView mav = composeModelAndView(path, ex);

    return mav;

    }*/
/*
    @ExceptionHandler({SecurityServiceException.class})
    public ModelAndView handleSecurityServiceException(Exception ex, HttpServletRequest request) {
	    String path = request.getPathInfo();
	
	    logger.warn("Can't accomplish the request because of SecurityService Exception when access to: " + path, ex);
	
	    ModelAndView mav = composeModelAndView(path, ex);
	
	    return mav;

    }*/

/*    @ExceptionHandler({DataAccessException.class})
    public ModelAndView handleDataAccessException(Exception ex, HttpServletRequest request) {
	    String path = request.getPathInfo();
	    logger.warn("Can't accomplish the request because of data layer error when access to: " + path, ex);
	    ModelAndView mav = composeModelAndView(path, ex);
	    return mav;
    }*/

    /**

    * This is the fallback for all other UncaughtException.

    * @param ex

    * @param request

    * @return

    */

/*    @ExceptionHandler({Exception.class})

    public ModelAndView handleUncaughtException(Exception ex, HttpServletRequest request) {

    String path = request.getPathInfo();

    log.warn("Can't accomplish the request because an unexpected error when access to: " + path, ex);

    ModelAndView mav = composeModelAndView(path, ex);

    return mav;

    }*/

/*    private ModelAndView composeModelAndView(String path, Exception ex) {
	    ModelAndView mav = new ModelAndView(COMMON_ERROR);
	    //CommonError commonError = new CommonError();
	    //commonError.setPath(path);
	    // only output exception information when debug
	    if (logger.isDebugEnabled()) {
	    	//commonError.setException(ex.toString());
	    }
	    //mav.addObject(COMMON_ERROR_ATTRIBUTE, commonError);
	    return mav;

    }*/
}
