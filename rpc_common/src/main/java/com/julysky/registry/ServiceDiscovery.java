package com.julysky.registry;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import javafx.util.Pair;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;


/**
 * Created by haoyifen on 2017/6/19 12:56.
 */
@ConfigurationProperties("service.discovery")
@Data
public class ServiceDiscovery {
	private static Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);
	private String zkAddress;
	private CuratorFramework client;
	private ConcurrentHashMap<String, PathChildrenCache> servicesMap = new ConcurrentHashMap<>();

	public static void main(String[] args) throws Exception {

		TestingServer testingServer = new TestingServer(2181);

		ServiceRegistry serviceRegistry = new ServiceRegistry();
		serviceRegistry.setServiceName("test");
		serviceRegistry.setZkAddress(testingServer.getConnectString());
		serviceRegistry.connect();
		serviceRegistry.register(9000);

		ServiceDiscovery serviceDiscovery = new ServiceDiscovery();
		serviceDiscovery.setZkAddress(testingServer.getConnectString());
		serviceDiscovery.connect();

		Thread.sleep(2000);
		PathChildrenCache pathChildrenCache = new PathChildrenCache(serviceDiscovery.client,
				ServiceCommon.getServicePath(serviceRegistry.getServiceName()), true);
		pathChildrenCache.start();
		ImmutableList<Pair<String, Integer>> test = serviceDiscovery.getService("test");
		System.out.println(test);

		serviceRegistry.register(8999);
		Thread.sleep(2000);
		ImmutableList<Pair<String, Integer>> test1 = serviceDiscovery.getService("test");
		System.out.println(test1);
	}

	@PostConstruct
	public void connect() throws Exception {
		CuratorFramework client = CuratorFrameworkFactory.newClient(zkAddress, 15 * 1000, 5000,
				new ExponentialBackoffRetry(1000, 3));
		client.start();
		this.client = client;
	}

	public ImmutableList<Pair<String, Integer>> getService(String serviceName) {

		PathChildrenCache pathChildrenCache = getPathChildrenCache(serviceName);
		if (pathChildrenCache == null)
			return ImmutableList.of();
		List<ChildData> currentData = pathChildrenCache.getCurrentData();
		List<Pair<String, Integer>> service = currentData.stream().map(childData -> {
			byte[] data = childData.getData();
			String strData = new String(data, StandardCharsets.UTF_8);
			String[] split = strData.split(":");
			return split;
		}).map(array -> {
			String hostName = array[0];
			Integer port = Integer.parseInt(array[1]);
			return new Pair<>(hostName, port);
		}).collect(Collectors.toList());
		return ImmutableList.copyOf(service);
	}

	private PathChildrenCache getPathChildrenCache(String serviceName) {
		PathChildrenCache value = servicesMap.get(serviceName);
		if (value != null) {
			return value;
		}
		//有多个线程同时执行也没有关系. 只不过是多加载了几个cache.
		PathChildrenCache pathChildrenCache = new PathChildrenCache(this.client, ServiceCommon.getServicePath(serviceName),
				true);
		try {
			pathChildrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
			servicesMap.put(serviceName, pathChildrenCache);
			return pathChildrenCache;
		} catch (Exception e) {
			logger.warn("get cache wrong , reason is " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
}
