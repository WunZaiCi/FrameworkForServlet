package com.banmaFramework.model;

import java.lang.reflect.Method;
import java.util.Map;

public class URLMapping {
	private Object obj;//對象
	private Method method;//對象的方法
	//key:参数名称 value:参数的数据类型	
	private Map<String,Class<?>> parameters;
	
	public URLMapping(Object obj, Method method,Map<String,Class<?>>parameters) {
		this.obj = obj; 
		this.method = method; 
		this.parameters = parameters; 
	}

	public Object getObj() {
		return obj;
	}

	public Map<String, Class<?>> getParameters() {
		return parameters;
	}

	public Method getMethod() {
		return method;
	}


	
	
	
}
