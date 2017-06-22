package com.julysky;

/**
 * Created by haoyifen on 2017/6/15 23:57.
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.google.common.primitives.Ints;
import com.julysky.pojo.RpcCall;
import com.julysky.serialize.SerializeUtils;

public class Client {
	public static void main(String[] args) throws IOException {

		Socket netty = null;
		try {
			netty = new Socket("localhost", 13988);
			OutputStream outputStream = netty.getOutputStream();
			for (int i = 0; i < 1; i++) {
				RpcCall rpcCall = new RpcCall();
				rpcCall.setId("100");
				rpcCall.setClassName("com.julysky.service.UserService");
				rpcCall.setMethodName("findByAge");
				rpcCall.setParameters(new Object[] { 24 });
				rpcCall.setParameterTypes(new String[] { Integer.class.getName() });
				byte[] bytes = SerializeUtils.serialize(rpcCall);
				// String s = JSON.toJSONString(rpcCall);
				// byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
				int length = bytes.length;
				byte[] lengthBytes = Ints.toByteArray(length);
				outputStream.write(lengthBytes);
				outputStream.write(bytes);
				InputStream inputStream = netty.getInputStream();
				while (true) {
					int read = inputStream.read();
					System.out.print(read + " ");
				}
			}
		} finally {
			if (netty != null) {
				netty.close();
			}
		}
	}

}
