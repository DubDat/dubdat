package sync;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Communicator {
	private byte[] ip;
	

	@SuppressWarnings("unused")
	private Communicator() {}
	
	public Communicator(byte[] ip) {
		this.ip = ip;
	}
	
	public void sendTime(int time) throws IOException {
		DatagramSocket ds = new DatagramSocket(50074); 
	    byte[] ms = new byte[1024]; 
	    String a="Computer";
	    ms=a.getBytes();
	    DatagramPacket ps = new DatagramPacket(ms, ms.length, InetAddress.getByAddress(ip),1599); 
	    ds.send(ps);
	    ds.close();
	}
}
