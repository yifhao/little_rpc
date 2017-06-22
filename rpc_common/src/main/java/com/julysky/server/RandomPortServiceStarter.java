package com.julysky.server;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.util.SocketUtils;

import com.julysky.registry.ServiceRegistry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by haoyifen on 2017/6/19 19:08.
 */
public class RandomPortServiceStarter implements AutoCloseable,CommandLineRunner{
	private static Logger logger = LoggerFactory.getLogger(RandomPortServiceStarter.class);
	@Autowired
	private ServiceRegistry serviceRegistry;
	@Autowired
	private ServerChannelInitializer serverChannelInitializer;
	private NioEventLoopGroup bossGroup = new NioEventLoopGroup();
	private NioEventLoopGroup workerGroup = new NioEventLoopGroup();

	public ChannelFuture start() {
		ServerBootstrap serverBootstrap = new ServerBootstrap();
		try {
			serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(serverChannelInitializer).option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true);
			int inetPort = SocketUtils.findAvailableTcpPort();
			ChannelFuture bindFuture = serverBootstrap.bind(inetPort).sync();
			bindFuture.addListener(future -> {
				if (future.isSuccess()) {
					serviceRegistry.connect();
					serviceRegistry.register(inetPort);
					logger.info("register service to zk success with port " + inetPort);
				} else {
					logger.info("bind to " + inetPort + "failed, exit");
					System.exit(1);
				}
			});
			return bindFuture;
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}

	@Override
	public void run(String... args) throws Exception {
		start();
	}
}
