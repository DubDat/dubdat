package sync;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Receiver {
	public void receiveTime() throws IOException {
		DatagramSocket ds = new DatagramSocket(1599);
	    byte[] ms = new byte[1024];
	    DatagramPacket ps = new DatagramPacket(ms,ms.length);
	    ds.receive(ps);
	    System.out.println(new String(ps.getData()));
	    ds.close();
	}
}
