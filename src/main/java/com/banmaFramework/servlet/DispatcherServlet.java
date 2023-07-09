package com.banmaFramework.servlet;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.banmaFramework.model.URLMapping;
import com.banmaFramework.util.ClassUtil;

///**
// * Servlet implementation class DispatcherServlet
// */
//@WebServlet("/DispatcherServlet")
public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=UTF-8");
		
		try {
			String requestURI = request.getRequestURI();
			URLMapping urlMapping = ClassUtil.getURLMapping(requestURI.replace(request.getContextPath(), ""));
			
			//请求的路径不存在
			if(urlMapping==null) {
				response.setStatus(404);
				response.getWriter().println("您请求的路径不存在:404"+requestURI);
				return;
			}
			Object obj = urlMapping.getObj();
			Method method = urlMapping.getMethod();
			
			Class<? >[] paramTypes= method.getParameterTypes();
			Object [] params = new  Object[method.getParameterCount()];
			int index = 0;
			for (Class<?> paramType : paramTypes) {
				if(paramType==HttpServletRequest.class) {
					params[index] = request;
				}
				if(paramType==HttpServletResponse.class) {
					params[index] = response;
				}else {
					//通过反射的方式获取到请求参数的名称 
					//通过反射的方式获取到请求参数的类型
					//通过请求参数的名称获得请求参数的值
					params[index] = request.getParameter("");
				}
				index++;
			}
			
			Object modelView=method.invoke(obj,params);
			 /**
			  * 如果方法有返回值
			  * 如果是请求转发 返回视图的名称
			  * 如果是重定向"redirect:/重定向的路径"
			  */
			if(modelView!=null) {
				if(modelView instanceof String ) {
					String viewName = (String) modelView;
					
					//重定向
					if(viewName.startsWith("redirect:")) {
						response.sendRedirect(request.getContextPath()
								+viewName.replace("redirect:", ""));
					}else {//请求转发	
						request.getRequestDispatcher("/WEB-INF/jsp/"+viewName+".jsp").forward(request, response);
					}
				}else {//如果方法的返回值是其他类型 按JSON数据处理
					response.setContentType("application/json;charset=UTF-8");
					//将modelView转换成json字符串 打印到浏览器
					String jsonResult = JSON .toJSONString(modelView);
					response.getWriter().print(jsonResult);
				}
				
				
			}
			

		} catch (Exception e) {
			response.setStatus(500);
			response.getWriter().println("服务器内部错误500:"); 
			e.printStackTrace(response.getWriter());
			e.printStackTrace();//打印到控制台
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
