package com.sys.controller;

import com.banmaFramework.annoation.Controller;
import com.banmaFramework.annoation.RequestMapping;

@Controller
@RequestMapping("/sys/user")
public class UserController {
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
	public void add() {

	}

	/**
	 * 新增
	 */
	@RequestMapping("/doadd")
	public void doadd() {

	}
}
