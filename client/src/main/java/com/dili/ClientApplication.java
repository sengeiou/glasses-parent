package com.dili;

import com.dili.client.component.FindServerComponent;
import com.dili.client.component.NettyClient;
import com.dili.client.consts.NettyConstant;
import com.dili.ss.dto.DTOScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/**
 * 由MyBatis Generator工具自动生成
 */
@SpringBootApplication
@ComponentScan(basePackages={"com.dili.ss.util","com.dili.client", "com.dili.ss.beetl"})
@DTOScan({"com.dili.ss.dto", "com.dili.registry"})
public class ClientApplication extends SpringBootServletInitializer implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientApplication.class);

    private final FindServerComponent findServerComponent;

    @Autowired
    public ClientApplication(FindServerComponent findServerComponent) {
        this.findServerComponent = findServerComponent;
    }

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }


    @Override
    public void run(String... strings) {
        try {
            findServerComponent.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info(String.format("获取服务端地址完成[%s:%s]，准备连接服务端", NettyConstant.serverHost, NettyConstant.serverPort));
        new NettyClient(NettyConstant.serverHost, NettyConstant.serverPort).run();
    }
}
