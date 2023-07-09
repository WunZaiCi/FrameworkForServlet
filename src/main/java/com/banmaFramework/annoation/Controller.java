package com.banmaFramework.annoation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解在类上 该类可以被DispatcherServlet访问和调用
 * @author WHASSUPMAN
 *
 */

@Target(ElementType.TYPE)//该注解只能标注在类上
@Retention(RetentionPolicy.RUNTIME)//该注解只在运行期有效
public @interface Controller {

}
