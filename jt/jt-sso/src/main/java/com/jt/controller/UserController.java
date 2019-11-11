package com.jt.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.jt.json.ObjectJsonUtil;
import com.jt.service.UserService;
import com.jt.util.IPUtil;
import com.jt.vo.SysResult;

import redis.clients.jedis.JedisCluster;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private JedisCluster jedisCluster;
	
	//http://sso.jt.com/user/check/dwadsaf/1?r=0.9883895682148498&callback=jsonp1572260710589&_=1572260715237
	@RequestMapping("/check/{param}/{type}")
	@ResponseBody
	public JSONPObject checkUser(@PathVariable String param,@PathVariable Integer type,String callback) {
		
	    	 boolean data = userService.checkUser(param,type);
		
	    return new JSONPObject(callback, SysResult.success(data));
	}
  
	//http://sso.jt.com/user/query/b2c12a03deaca4f48c123ffb3308e9d5?callback=jsonp1572612798772&_=1572612798816
	//用户信息回显
	
//===========第二种回显方式==================
	
	@RequestMapping("/query/{ticket}/{username}")
	@ResponseBody
	public JSONPObject findUserByUserName(@PathVariable("ticket")String ticket,
			                              @PathVariable("username")String username,
			                               String callback,
			                               HttpServletRequest request) {
		
		String ip = IPUtil.getIpAddr(request);
		
		if(!ip.equalsIgnoreCase(jedisCluster.hget(username, "JT_IP"))) {
			
			return new JSONPObject(callback, SysResult.fail()); 
		}
		
		String localTicket = jedisCluster.hget(username, "JT_TICKET");
        
		if(!localTicket.equalsIgnoreCase(ticket)) {
			
			return new JSONPObject(callback, SysResult.fail()); 
		}
	
		String userJSON = jedisCluster.hget(username, "JT_USERNAME");
		
		return new JSONPObject(callback, SysResult.success(userJSON));
	}
	
	
//=====第一种回显方式===============	
	@RequestMapping("/query/{ticket}")
	@ResponseBody
	public JSONPObject findUserByTicket(@PathVariable("ticket")String ticket,String callback) {
		
		String result = jedisCluster.get(ticket);
		
		if(StringUtils.isEmpty(result)) {
			return new JSONPObject(callback, SysResult.fail());
		}
		
		//return new JSONPObject(callback,ObjectMapperUtil.toJson(new SysResult().success(result)));
		
        return new JSONPObject(callback, SysResult.success(result));		
	}
	
}
