package com.julysky.handler;

import com.julysky.serialize.SerializeUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * Created by haoyifen on 2017/6/16 22:11.
 */
public abstract class RPCEncoder<T> extends MessageToByteEncoder<T> {
	@Override
	protected void encode(ChannelHandlerContext ctx, T msg, ByteBuf out) throws Exception {
		byte[] bytes = SerializeUtils.serialize(msg);
		out.writeBytes(bytes);
	}
}
