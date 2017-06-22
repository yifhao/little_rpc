package com.julysky;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Random;

/**
 * Created by haoyifen on 2017/6/15 20:02.
 */
public class Client {
	public static void main(String[] args) throws IOException {

		Socket netty = null;

		try {
			netty = new Socket("localhost", 9000);
			OutputStream outputStream = netty.getOutputStream();
			outputStream.write(100);
			for (int i = 0; i < 100; i++) {
				int dataLength = new Random().nextInt(25)+75;
				int total = dataLength+1;
				outputStream.write(total);
				for (int i1 = 0; i1 < dataLength; i1++) {
					outputStream.write(i1 & 0xff);
				}
			}
		} finally {
			if (netty != null) {
				netty.close();
			}
		}
	}

}
