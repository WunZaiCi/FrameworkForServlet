package com.banmaFramework.model;

import java.lang.reflect.Method;

public class URLMapping {
	private Object obj;
	private Method method;
	
	public URLMapping(Object obj, Method method) {
		this.obj = obj;
		this.method = method;
	}

	public Object getObj() {
		return obj;
	}

	public Method getMethod() {
		return method;
	}


	
	
	
}
