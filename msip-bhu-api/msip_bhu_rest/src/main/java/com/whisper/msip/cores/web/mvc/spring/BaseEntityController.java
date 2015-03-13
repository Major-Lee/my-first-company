package com.whisper.msip.cores.web.mvc.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.smartwork.msip.cores.helper.ExtensionHelper;
import com.smartwork.msip.cores.helper.XStreamHelper;
import com.smartwork.msip.cores.orm.logic.identifier.Identifier;
import com.smartwork.msip.cores.orm.service.EntityService;
import com.smartwork.msip.jdo.Response;
import com.whisper.msip.cores.web.mvc.spring.helper.SpringMVCHelper;

/**
 * 定义标准的rest方法以对应实体对象的操作,以达到统一rest的方法名称,
 * 还可以避免子类需要重复编写@RequestMapping annotation.
 * 
 * 子类要实现某功能只需覆盖下面的方法即可.
 * 注意: 覆盖时请使用@Override,以确保不会发生错误
 * <pre>
 * /user                => index()  
 * /user/new            => _new()  
 * /user/{id}           => show()  
 * /user/{id}/edit      => edit()  
 * /user        POST    => create()  
 * /user/{id}   PUT     => update()  
 * /user/{id}   DELETE  => delete()  
 * /user        DELETE  => batchDelete()  
 * </pre>
 * @author edmond
 */
public abstract class BaseEntityController<PK extends java.io.Serializable,Entity extends Identifier> extends BaseController {
	
	public abstract EntityService<PK,Entity,?> getEntityService();
	public abstract String getEntityPageBasePath();
	
	@RequestMapping(value="/index.html",method=RequestMethod.GET)
	public abstract ModelAndView index(
			ModelAndView mv,
			//@RequestParam(required = false, defaultValue = "1") int pageno,
			//@RequestParam(required = false, defaultValue = "10") int pagesiz,
			Entity model);
	
	/** 显示 html*/
	@RequestMapping(value="/{id}/detail.html",method=RequestMethod.GET)
	public ModelAndView show(ModelAndView mv,@PathVariable PK id){
		Entity entity = this.getEntityService().getById(id);
		mv.addObject(ExtensionHelper.generatePropertyName(entity.getClass().getSimpleName()),entity);
		mv.setViewName(getEntityPageBasePath()+"/detail");
		this.prepareModelAndView(mv);
		return mv;
	}
	
	/** 进入新增/修改 */
	@RequestMapping(value="/{id}/edit.html", method = RequestMethod.GET)
	public abstract ModelAndView edit(ModelAndView mv,@PathVariable PK id);
/*	{
		Entity entity = null;
		if(id == null) entity = entity.getClass().newInstance();
		Entity entity = this.getEntityService().getById(id);
		mv.addObject(ExtensionHelper.generatePropertyName(entity.getClass().getSimpleName()),entity);
		mv.setViewName(getEntityPageBasePath()+"/edit");
		this.prepareModelAndView(mv);
		return mv;		
	}*/
	
	
	/* ===================================json&xml supported================================================*/
	/** 新增/修改 */
	@RequestMapping(value="/edit.json", method = RequestMethod.POST)
	public void edit(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required=true,value="checkedRoleIds") Integer[] checkedRoleIds,
			Entity model){
		//Entity oldModel = this.getEntityService().getById((PK)model.getIdentifier());
		//BeanCopier copy = BeanCopier.create(model.getClass(), model.getClass(), false);
		//copy.copy(arg0, arg1, arg2);
		//BeanUtils.copyProperties(teacher,teacherForm);
		this.getEntityService().saveOrUpdate(model);
	}

	
	/** 显示 json*/
	@RequestMapping(value={"/{id}.json","{id}.xml"},method=RequestMethod.GET)
	public void detail(HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable PK id){
		Entity model = this.getEntityService().getById(id);
		if(this.isJsonRequest(request)){
			//Map<String,Object> map = JsonHelper.filterObjectData(true, model, false, "id","nickname","truename","updatetime");
	    	//String jsonStr = JsonHelper.getContentData(map);
	    	SpringMVCHelper.renderJson(response, model,true);
		}else{
			SpringMVCHelper.renderXml(response, XStreamHelper.toXML(model));
		}
	}
	
	/** 删除 */
	@RequestMapping(value="/{id}.json",method={RequestMethod.DELETE})
	public void remove(HttpServletResponse response,@PathVariable PK id){
		this.getEntityService().deleteById(id);
		SpringMVCHelper.renderJson(response, Response.SUCCESS);
	}
	

	/** 批量删除 */
	@RequestMapping(value="/remove.json",method=RequestMethod.DELETE)
	public abstract void batchRemove(HttpServletResponse response,@RequestParam("itemids") String itemids);
/*	{
		//String[] idArray = itemids.split(",");
		//for(String idStr:idArray){
		//	this.getEntityService().deleteById(Integer.parseInt(idStr));
		//}
		//Arrays.as
		this.getEntityService().deleteByIds(Arrays.asList(itemids));//.deleteById(id);
		SpringMVCHelper.renderJson(response, Response.SUCCESS);
	}*/

	@Override
	protected void prepareModelAndView(ModelAndView mv) {
		super.prepareCtx4ModelAndView(mv);
		mv.addObject(this.getEntityService());
	}
	
	
}
