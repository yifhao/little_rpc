package com.julysky.autoConfiguration;

import com.julysky.client.ServiceProxyUtil;
import com.julysky.client.ConnectionManager;
import com.julysky.registry.ServiceDiscovery;
import org.springframework.context.annotation.Bean;

/**
 * Created by haoyifen on 2017/6/19 18:03.
 */
public class ServiceDiscoveryAutoConfiguration {
	@Bean
	public ServiceDiscovery serviceDiscovery() {
		return new ServiceDiscovery();
	}
	@Bean
	public ConnectionManager connectionManager() {
		return new ConnectionManager();
	}
	@Bean
	public ServiceProxyUtil serviceProxyUtil(){
		return new ServiceProxyUtil();
	}
}
