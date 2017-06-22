package com.julysky.autoConfiguration;

import com.julysky.registry.ServiceRegistry;
import com.julysky.server.RPCServerHandler;
import com.julysky.server.ServerChannelInitializer;
import com.julysky.server.ServiceProvider;
import com.julysky.server.RandomPortServiceStarter;
import org.springframework.context.annotation.Bean;

/**
 * Created by haoyifen on 2017/6/19 18:02.
 */
public class ServiceRegistryAutoConfiguration {
	@Bean
	public ServiceRegistry serviceRegistry() {
		return new ServiceRegistry();
	}
	@Bean
	public ServiceProvider serviceProvider() {
		return new ServiceProvider();
	}
	@Bean
	public RPCServerHandler rpcServerHandler() {
		return new RPCServerHandler();
	}
	@Bean
	public ServerChannelInitializer serverChannelInitializer() {
		return new ServerChannelInitializer();
	}

	@Bean
	public RandomPortServiceStarter serviceStarter() {
		return new RandomPortServiceStarter();
	}
}
