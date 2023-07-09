package com.banmaFramework.annoation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE,ElementType.METHOD})//该注解只能标注在类和方法上
@Retention(RetentionPolicy.RUNTIME)//该注解只在运行期有效
public @interface RequestMapping {
	String value() default "";
}
