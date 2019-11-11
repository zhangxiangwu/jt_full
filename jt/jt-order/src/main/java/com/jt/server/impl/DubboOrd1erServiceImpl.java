package com.jt.server.impl;


import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.jt.dubbo.service.DubboOrderService;
import com.jt.mapper.OrderItemMapper;
import com.jt.mapper.OrderMapper;
import com.jt.mapper.OrderShippingMapper;
import com.jt.pojo.Order;
import com.jt.pojo.OrderItem;
import com.jt.pojo.OrderShipping;


@Service
public class DubboOrd1erServiceImpl implements DubboOrderService{

	@Autowired
	private OrderMapper orderMapper;

	@Autowired
	private OrderItemMapper orderItemMapper;

	@Autowired
	private OrderShippingMapper orderShippingMapper;

	@Override
	@Transactional
	public String saveOrder(Order order) {
		// TODO Auto-generated method stub

		System.out.println("===========================order="+order);
		
		String orderId = "" + order.getUserId() + System.currentTimeMillis();

		Date date = new Date();

		order.setOrderId(orderId)
		.setStatus(1)
		.setCreated(date)
		.setUpdated(date);
		orderMapper.insert(order);
		System.out.println("订单入库成功");

		OrderShipping orderShipping = order.getOrderShipping();

		orderShipping.setOrderId(orderId)
		.setCreated(date)
		.setUpdated(date);

		orderShippingMapper.insert(orderShipping);
		System.out.println("订单物流入库成功!!!!");

		List<OrderItem> orderItems = order.getOrderItems();             

		for (OrderItem orderItem : orderItems) {

			orderItem.setOrderId(orderId)
			.setCreated(date)
			.setUpdated(date);
			orderItemMapper.insert(orderItem);

		}
		System.out.println("订单商品入库成功!!!!");

		return orderId;
	}

	@Override
	public Order findOrderById(String id) {
		
		//Order order = orderMapper.selectById(id);
		
		Order order = orderMapper.findOrderById(id);
		
		return order;
	}

}
















