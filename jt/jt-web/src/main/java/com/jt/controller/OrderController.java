package com.jt.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.jt.dubbo.service.CartService;
import com.jt.dubbo.service.DubboOrderService;
import com.jt.pojo.Cart;
import com.jt.pojo.Order;
import com.jt.util.ThreadLocalUtil;
import com.jt.vo.SysResult;

@Controller
@RequestMapping("/order")
public class OrderController {

	@Reference(check = false)
	private CartService cartService;
	
	//结算购物车所有信息
	//http://www.jt.com/order/create.html
	@RequestMapping("/create")
	public String create(HttpServletRequest request) {
		
		List<Cart> carts = cartService.findCartByUserId(ThreadLocalUtil.get().getId());
		
		request.setAttribute("carts", carts);
		
		return "order-cart";
	}
	
	
	//http://www.jt.com/order/submit    提交订单
	
	
	@Reference(check = false)
	private DubboOrderService orderService;
	
	@RequestMapping("/submit")
	@ResponseBody
	public SysResult orderSubmit(Order order) {
		
		Long userId = ThreadLocalUtil.get().getId();
		order.setUserId(userId);
		
		String orderId = orderService.saveOrder(order);
		
		return SysResult.success(orderId);
	}
	
	//order/success.html?id="+result.data     提交订单页面
	
	@RequestMapping("/success")
	public String orderSuccess(@Qualifier("orderId")String id,Model model) {
		
		Order order = orderService.findOrderById(id);
		model.addAttribute("order", order);
		return "success";
	}
	
   ////http://www.jt.com/order/myOrder.html   显示我的订单
	
	@RequestMapping("/myOrder")
	public String myOrder() {
		
		
		
		return "my-orders";
	}
	
	
	
	
	
	
	
	
	
	
}

