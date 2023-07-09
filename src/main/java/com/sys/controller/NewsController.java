package com.sys.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.banmaFramework.annoation.Controller;
import com.banmaFramework.annoation.RequestMapping;

@Controller
@RequestMapping("/coms/news")
public class NewsController {
	/**
	 * 跳转到列表
	 */
	@RequestMapping
	public void list() {

	}

	/**
	 * 调转到新增页面
	 */
	@RequestMapping("/add")
	public String add(HttpServletRequest request, HttpServletResponse response) {
		System.out.println(request.getParameter("username"));
		return "redirect:/index.html";
	}

	/**
	 * 新增
	 */
	@RequestMapping("/doadd")
	public void doadd() {

	}
}
