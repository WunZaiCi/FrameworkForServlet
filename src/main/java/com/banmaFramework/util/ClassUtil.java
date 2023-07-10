package com.banmaFramework.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.banmaFramework.annoation.Controller;
import com.banmaFramework.annoation.RequestMapping;
import com.banmaFramework.model.URLMapping;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

public class ClassUtil {
	


	public static Map<String, URLMapping> getURLMappings(String basePackage)
			throws InstantiationException, IllegalAccessException, NotFoundException {
		// 通过包名得到Reflections对象
		Reflections reflections = new Reflections(basePackage);

		// 获取包中所有标注了@Controller注解的类
		Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Controller.class);

		// 用来放 requestURI:对象 的map 对象包含 @Controller类和@RequestMapping方法
		Map<String, URLMapping> mappings = new HashMap<>();

		for (Class<?> cls : classes) {
			String baseURI = "";
			// 检查类上是否有@RequestMapping注解
			RequestMapping baseMapping = cls.getDeclaredAnnotation(RequestMapping.class);
			// 如过类里有@RequestMapping注解
			if (baseMapping != null) {
				baseURI = baseURI + baseMapping.value();// 拼接uri
			}

			// 通过反射创建该类的对象 jdk9之后newInstance()过时了 应该得到构造器 通过构造器再创建对象
			Object obj = cls.newInstance();
			// 获取类中所有的方法
			Method[] methods = cls.getDeclaredMethods();
			for (Method method : methods) {
				// 判断方法上是否标注了@RequestMapping注解
				RequestMapping requestMapping = method.getDeclaredAnnotation(RequestMapping.class);
				if (requestMapping != null) {
					String requestURI = baseURI + requestMapping.value();

					ClassPool pool = ClassPool.getDefault();
					pool.insertClassPath(new ClassClassPath(cls));
//					 CtClass cc = pool.get(classname);  
					CtMethod cm = pool.getMethod(cls.getName(), method.getName());
					MethodInfo methodInfo = cm.getMethodInfo();
					CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
					LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute
							.getAttribute(LocalVariableAttribute.tag);

					Class<?>[] parameterTypes = method.getParameterTypes();
					Map<String, Class<?>> parameters = new LinkedHashMap<>();
					if (attr != null) {
						int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
						for (int i = 0; i < method.getParameterCount(); i++) {
							String paramName = attr.variableName(i + pos);
							parameters.put(paramName, parameterTypes[i]);
						}

						mappings.put(requestURI, new URLMapping(obj, method, parameters));

					}
				}

			}

		}
		return mappings;
	}
}
