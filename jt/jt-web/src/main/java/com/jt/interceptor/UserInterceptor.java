package com.jt.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.jt.pojo.User;
import com.jt.util.CookieUtil;
import com.jt.util.IPUtil;
import com.jt.util.ObjectMapperUtil;
import com.jt.util.ThreadLocalUtil;

import redis.clients.jedis.JedisCluster;

@Component
public class UserInterceptor implements HandlerInterceptor{

	@Autowired
	private JedisCluster jedisCluster;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// TODO Auto-generated method stub

//		Cookie[] cookies = request.getCookies();
//
//		if(cookies.length>0) {
//
//			for (Cookie cookie : cookies) {
//
//				if(cookie.getName().equals("JT_TICKET")) {
//					
//					if(!StringUtils.isEmpty(jedisCluster.get(cookie.getValue()))){
//						
//						return true;
//					}
//				}
//
//			}
//
//		}
		
		Map<String,String> map = CookieUtil.getCookieUtil(request, "JT_TICKET","JT_USERNAME");

		String ip = IPUtil.getIpAddr(request);
		
		if(!ip.equalsIgnoreCase(jedisCluster.hget(map.get("userName"), "JT_IP"))) {
			
			response.sendRedirect("/user/login.html");
			return false;
		}
		
		if(!map.get("ticket").equalsIgnoreCase(jedisCluster.hget(map.get("userName"), "JT_TICKET"))){
			
			response.sendRedirect("/user/login.html");
			return false;
		}
		
		ThreadLocalUtil.set(ObjectMapperUtil.toObject(jedisCluster.hget(map.get("userName"), "JT_USERNAME"), User.class));
		
		return true;
		
//		if(!StringUtils.isEmpty(cookieValue)) {
//			
//			String userJSON = jedisCluster.get(cookieValue);
//			
//			if(!StringUtils.isEmpty(userJSON)) {
//				
//				//request.setAttribute("JT-USER", ObjectMapperUtil.toObject(userJSON, User.class));
//				
//				ThreadLocalUtil.set(ObjectMapperUtil.toObject(userJSON, User.class));
//				
//				return true;
//			}
//	 }

		
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

       ThreadLocalUtil.remove();
	}
	
   
}
























