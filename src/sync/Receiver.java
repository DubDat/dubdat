package sync;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

public class Receiver {
	public void receiveTime() throws IOException {
		DatagramSocket ds = new DatagramSocket(1599);
	    byte[] ms = new byte[1024];
	    DatagramPacket ps = new DatagramPacket(ms,ms.length);
	    System.out.println("Waiting for packet...");
	    ds.receive(ps);
	    System.out.println("Received: " + (ByteBuffer.wrap(ps.getData()).getInt()));
	    ds.close();
	}
}
