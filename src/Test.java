import java.io.IOException;

import sync.Receiver;
import sync.Transmitter;







public class Test {

    
    public static void main(String[] args) throws IOException {
    	byte[] ip = {67,194-256,102,111};
    	Transmitter tra = new Transmitter(ip);
    	tra.sendTime(152365);
    	/*Receiver rec = new Receiver();
    	rec.receiveTime();*/

    }

}