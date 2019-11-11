package com.jt.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {

	public static Map getCookieUtil(HttpServletRequest req,String cookieTicket,String cookieUserName) {
		
		Cookie[] cookies = req.getCookies();
		
		Map<String,String> map = new HashMap<>();
		
		if(cookies == null || cookies.length == 0) {
			return null;
		}else {
			
			String ticket = null;
			String userName = null;
			
			for (Cookie cookie : cookies) {
				
				if(cookie.getName().equalsIgnoreCase(cookieUserName)) {
					userName = cookie.getValue();
				}
				if(cookie.getName().equals(cookieTicket)) {
					ticket = cookie.getValue();
				}
			}
			
			map.put("ticket", ticket);
			map.put("userName", userName);
			return map;
		}
	}
	
	public static void addCookieUtil(HttpServletRequest request,
			                         HttpServletResponse response,
			                         String domain,String path,String ticket,
			                         String cookieName) {
		Cookie cookie = new Cookie(cookieName,ticket);
		cookie.setDomain(domain);
		cookie.setPath("/");
		cookie.setMaxAge(7*24*3600);
		response.addCookie(cookie);
		
	}
	
}



















