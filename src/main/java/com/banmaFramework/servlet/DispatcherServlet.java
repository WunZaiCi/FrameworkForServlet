package com.banmaFramework.servlet;

import java.io.IOException;
import java.io.ObjectInputFilter.Config;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.banmaFramework.model.URLMapping;
import com.banmaFramework.util.ClassUtil;

import javassist.NotFoundException;

///**
// * Servlet implementation class DispatcherServlet
// */
//@WebServlet("/DispatcherServlet")
public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Map<String, URLMapping> mappings = new HashMap<>();
	private static final String REQUEST_PRFIX = "prefix";// 請求的前綴
	private static final String REQUEST_SUFFIX = "suffix";// 請求的後綴
	private static final String BASEPACKAGE = "basePackage";// 包名

	private String prefix = "/WEB-INF/jsp";
	private String suffix = ".jsp";
	private String basePackage = "com.banmaFramework";

	@Override
	public void init(ServletConfig config) throws ServletException {
		try {
			if (config.getInitParameter(REQUEST_PRFIX).trim().isEmpty()) {
				prefix = config.getInitParameter(REQUEST_PRFIX);
			}
			if (config.getInitParameter(REQUEST_SUFFIX).trim().isEmpty()) {
				suffix = config.getInitParameter(REQUEST_SUFFIX);
			}
			if (config.getInitParameter(BASEPACKAGE).trim().isEmpty()) {
				basePackage = config.getInitParameter(BASEPACKAGE);
			}

			mappings = ClassUtil.getURLMappings(basePackage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=UTF-8");

		try {
			// 获取到请求的URI
			String requestURI = request.getRequestURI();
			// 获取目标URI
			URLMapping urlMapping = mappings.get(requestURI.replace(request.getContextPath(), ""));

			// 请求的路径不存在
			if (urlMapping == null) {
				response.setStatus(404);
				response.getWriter().println("您请求的路径不存在:404" + requestURI);
				return;
			}
			
			//获取目标对象
			Object obj = urlMapping.getObj();
			//获取目标方法
			Method method = urlMapping.getMethod();
			//获取目标方法的参数信息  参数名称 参数值
			Map<String, Class<?>> parameters = urlMapping.getParameters();

			//存储请求参数的值 
			Object[] params = new Object[method.getParameterCount()];
			int index = 0;
			for (String key : parameters.keySet()) {
				// 请求参数的名称
				String paramName = key;
				// 请求参数的值(一个参数一个值)
				String paramValue = request.getParameter(paramName);
				// 请求参数的值(一个参数多个值)
				//String[] paramValues =request.getParameterValues(paramName);
				// 请求参数的类型
				Class<?> paramType = parameters.get(key);

				if (paramType == HttpServletRequest.class) {
					params[index++] = request;
					continue;
				}
				if (paramType == HttpServletResponse.class) {
					params[index++] = response;
					continue;
				} else {
					// 这里没有处理数据类型					
					params[index++] = paramValue;
					continue;
				}
			}

			Object modelView = method.invoke(obj, params);
			/**
			 * 如果方法有返回值 如果是请求转发 返回视图的名称 如果是重定向"redirect:/重定向的路径"
			 */
			if (modelView != null) {
				if (modelView instanceof String) {
					String viewName = (String) modelView;

					// 重定向
					if (viewName.startsWith("redirect:")) {
						response.sendRedirect(request.getContextPath() + viewName.replace("redirect:", ""));
					} else {// 请求转发
						request.getRequestDispatcher(prefix + "/" + viewName + suffix).forward(request, response);
					}
				} else {// 如果方法的返回值是其他类型 按JSON数据处理
					response.setContentType("application/json;charset=UTF-8");
					// 将modelView转换成json字符串 打印到浏览器
					String jsonResult = JSON.toJSONString(modelView);
					response.getWriter().print(jsonResult);
				}

			}

		} catch (Exception e) {
			response.setStatus(500);
			response.getWriter().println("服务器内部错误500:");
			e.printStackTrace(response.getWriter());
			e.printStackTrace();// 打印到控制台
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
