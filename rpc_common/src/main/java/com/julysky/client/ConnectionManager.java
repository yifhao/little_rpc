package com.julysky.client;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import com.julysky.registry.ServiceDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableList;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import javafx.util.Pair;

/**
 * Created by haoyifen on 2017/6/19 12:58.
 */
public class ConnectionManager {

	private static Logger logger = LoggerFactory.getLogger(ConnectionManager.class);

	@Autowired private ServiceDiscovery serviceDiscovery;
	private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
	private ConcurrentHashMap<String, RPCClientHandler> handlers = new ConcurrentHashMap<>();

	public RPCClientHandler getHandler(String serviceName) {
		ImmutableList<Pair<String, Integer>> service = serviceDiscovery.getService(serviceName);
		if (service.size()==0) {
			return null;
		}
		Random random = new Random();
		int i = random.nextInt(service.size());
		Pair<String, Integer> randomServiceAddr = service.get(i);
		RPCClientHandler handler = connect(randomServiceAddr);
		return handler;
	}

	private RPCClientHandler connect(Pair<String, Integer> serviceAddr) {
		String addrStr = serviceAddr.getKey() + ":" + serviceAddr.getValue();
		RPCClientHandler handler = handlers.get(addrStr);
		if (handler != null) {
			return handler;
		}
		synchronized (this) {
			handler = handlers.get(addrStr);
			if (handler != null) {
				return handler;
			}
			Bootstrap bootstrap = new Bootstrap();
			ClientChannelInitializer channelInitializer = new ClientChannelInitializer();
			try {
				bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class).handler(channelInitializer)
						.option(ChannelOption.SO_KEEPALIVE, true);
				ChannelFuture connectFuture = bootstrap.connect(serviceAddr.getKey(), serviceAddr.getValue()).sync();
				connectFuture.sync();
				handlers.put(addrStr, channelInitializer.getRpcClientHandler());
				return channelInitializer.getRpcClientHandler();
			} catch (Exception e) {
				e.printStackTrace();
				logger.warn(e.getMessage());
				throw new RuntimeException(e);
			}
		}
	}
}
