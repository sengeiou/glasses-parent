package com.dili.client.controller;

import com.alibaba.fastjson.JSON;
import com.dili.client.boot.RabbitConfiguration;
import com.dili.client.consts.NettyCache;
import com.dili.client.domain.Protocol;
import com.dili.client.factory.ProtocolConvertFactory;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.ReqParam;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 页面控制器
 * @author wangmi
 */
@Controller
@RequestMapping("/index")
public class IndexController {

	//跳转到首页
	public static final String INDEX_PATH = "index";

	@Autowired
	private AmqpTemplate amqpTemplate;

	/**
	 * http://localhost:8080/index/index.html
	 * @param modelMap
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/index.html", method = { RequestMethod.GET, RequestMethod.POST })
	public String index(ModelMap modelMap, HttpServletRequest request) {
		return INDEX_PATH;
	}

	/**
	 * 模拟终端上报命令
	 * @param cmd
	 * @return
	 */
	@RequestMapping(value = "/exec.action", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public BaseOutput<Object> exec(@ReqParam("cmd") String cmd) {
		Protocol protocol = JSON.parseObject(cmd, Protocol.class);
		NettyCache.channel.writeAndFlush(protocol.toByteArray());
		return BaseOutput.success("命令发送完成");
	}

	/**
	 * 模拟发MQ消息给Server
	 * @param cmd
	 * @return
	 */
	@RequestMapping(value = "/send.action", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public BaseOutput<Object> send(@ReqParam("cmd") String cmd) {
		amqpTemplate.convertAndSend(RabbitConfiguration.TOPIC_EXCHANGE, RabbitConfiguration.CMD_ROUTING_KEY, cmd);
//		amqpTemplate.convertAndSend(RabbitConfiguration.TOPIC_EXCHANGE, RabbitConfiguration.REPORT_ROUTING_KEY, id+","+cmd);
		return BaseOutput.success("命令发送完成");
	}

}
