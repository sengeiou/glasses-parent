package com.dili;

import com.dili.registry.component.RegistryNettyServer;
import com.dili.registry.component.RegistryTerminalServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 由MyBatis Generator工具自动生成
 * @author wangmi
 */
@SpringBootApplication
@ComponentScan(basePackages={"com.dili.ss.util","com.dili.registry"})
public class RegistryApplication implements CommandLineRunner {

    private final RegistryNettyServer registryNettyServer;

    private final RegistryTerminalServer registryTerminalServer;

    @Autowired
    public RegistryApplication(RegistryNettyServer registryNettyServer, RegistryTerminalServer registryTerminalServer) {
        this.registryNettyServer = registryNettyServer;
        this.registryTerminalServer = registryTerminalServer;
    }

    public static void main(String[] args) {
        SpringApplication.run(RegistryApplication.class, args);
    }

    @Override
    public void run(String... strings) throws InterruptedException {
        new Thread(() -> registryNettyServer.start()).start();
        registryTerminalServer.start();
    }
}
