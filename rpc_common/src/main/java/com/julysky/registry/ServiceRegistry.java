package com.julysky.registry;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.zookeeper.CreateMode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * Created by haoyifen on 2017/6/19 9:55.
 */
@ConfigurationProperties("service.registry")
@Data
public class ServiceRegistry {
	private String zkAddress;
	private String serviceName;
	private CuratorFramework client;

	public static void main(String[] args) throws Exception {
		TestingServer testingServer = new TestingServer(2181);
		testingServer.start();
		ServiceRegistry registry = new ServiceRegistry();
		registry.setServiceName("test");
		registry.setZkAddress("localhost:2181");

		registry.connect();
		registry.register(9000);
		List<String> strings = registry.client.getChildren().forPath(ServiceCommon.getServicePath(registry.serviceName));
		strings.stream().map(s -> {
			try {
				byte[] bytes = registry.client.getData().forPath(ServiceCommon.getServicePath(registry.serviceName) + "/" + s);
				return new String(bytes, StandardCharsets.UTF_8);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}).forEach(System.out::println);
		System.out.println(strings);
		registry.client.close();
		testingServer.stop();
	}

	public synchronized void connect() throws Exception {
		CuratorFramework client = CuratorFrameworkFactory.newClient(zkAddress, 15 * 1000, 5000,
				new ExponentialBackoffRetry(1000, 3));
		client.start();
		this.client = client;
	}

	public void register(int port) {
		try {
			String hostName = InetAddress.getLocalHost().getHostName();
			String serviceAddress = hostName + ":" + port;
			client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(
					ServiceCommon.getServicePath(serviceName) + "/server", serviceAddress.getBytes(StandardCharsets.UTF_8));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
