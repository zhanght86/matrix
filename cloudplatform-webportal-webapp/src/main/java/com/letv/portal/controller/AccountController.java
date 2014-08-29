package com.letv.portal.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mysql.jdbc.StringUtils;

@Controller
@RequestMapping("/account")
public class AccountController {
	
	@RequestMapping("/login")   //http://localhost:8080/account/login
	public String login(HttpServletRequest request,HttpServletResponse response) {
		String loginName=request.getParameter("loginName");
		String password=request.getParameter("password");
		
		if(StringUtils.isNullOrEmpty(loginName)) {
			return "account/login";
		}
		request.getSession().setAttribute("loginName", loginName);
		request.getSession().setAttribute("userId", loginName);
		
		return "redirect:/db/list";
	}
	
	

}
