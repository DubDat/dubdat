package sync;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class Transmitter {
	private byte[] ip;
	

	@SuppressWarnings("unused")
	private Transmitter() {}
	
	public Transmitter(byte[] ip) {
		this.ip = ip;
	}
	
	public void sendTime(int time) throws IOException {
		DatagramSocket ds = new DatagramSocket(50074); 
	    byte[] ms = new byte[1024]; 
	   
	    ms=ByteBuffer.allocate(4).putInt(time).array();
	    DatagramPacket ps = new DatagramPacket(ms, ms.length, InetAddress.getByAddress(ip),1599); 
	    ds.send(ps);
	    ds.close();
	}
}
