package com.jt.dubbo.user.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jt.dubbo.service.DubboUserService;
import com.jt.json.ObjectJsonUtil;
import com.jt.mapper.UserMapper;
import com.jt.pojo.User;

import redis.clients.jedis.JedisCluster;

@Service
public class DubboUserServiceImpl implements DubboUserService {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private JedisCluster jedisCluster;

	@Override
	public int insertUser(User user) {
		// TODO Auto-generated method stub

		String md5Pass = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());

		user.setPassword(md5Pass)
		.setEmail(user.getPhone())
		.setCreated(new Date())
		.setUpdated(user.getCreated());

		int rows = userMapper.insert(user);

		return rows;
	}

//======第二种方法,因为要把ticket/userJson和ip地址都存入redis中,没法以key:value的方式存入到redis中,所以以key:object的形式存入到rdis中,
//   存入格式为 {userName,["JT_TICKET:ticket,"JT_USERNAME":userJson,"JT_IP":ip]}==========================================
	
	@Override
	public String doLogin(User user,String ip) {
		
		String password = user.getPassword();
		
		String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
		
		user.setPassword(md5Password);
		
		QueryWrapper<User> queryWrapper = new QueryWrapper<>(user);
		
		User userDB = userMapper.selectOne(queryWrapper);
		
		if(userDB == null) {
			return null;
		}
		
		String uuid = UUID.randomUUID().toString();
		
		String ticket = DigestUtils.md5DigestAsHex(uuid.getBytes());
		
		userDB.setPassword("********");
		
		Map map = new HashMap();
		
		map.put("JT_TICKET", ticket);
		map.put("JT_USERNAME", ObjectJsonUtil.toJSON(userDB));
		map.put("JT_IP", ip);
		
		jedisCluster.hmset(user.getUsername(), map);
		jedisCluster.expire(user.getUsername(), 7*24*3600);
		return ticket;
	}
	
	
//============第一种方法,随机生成一个ticket,然后传给controller层,再把ticket当作key,userJson当作value存入redis中==================
//	@Override
//	public String doLogin(User user) {
//		// TODO Auto-generated method stub
//
//		String md5Passwrod = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
//
//		user.setPassword(md5Passwrod);
//
//		QueryWrapper<User> queryWrapper = new QueryWrapper<>(user);
//
//		User userDB = userMapper.selectOne(queryWrapper);
//
//		if(userDB!=null) {
//
//			String uuid = UUID.randomUUID().toString();
//
//			String ticket = DigestUtils.md5DigestAsHex(uuid.getBytes());
//
//			userDB.setPassword("********");
//
//           String jsonUser = ObjectJsonUtil.toJSON(userDB);
//           
//           jedisCluster.setex(ticket, 7*24*3600, jsonUser);
//           
//           return ticket;
//
//		}
//
//		return null;
//	}


}


















