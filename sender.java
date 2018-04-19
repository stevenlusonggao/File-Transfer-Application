import java.net.*;
import java.nio.ByteBuffer;
import java.io.*;

public class sender {
	public static void main(String[] args) throws Exception {
		DatagramSocket socket;
		DatagramPacket inbound;
		DatagramPacket outbound;
		InetAddress ip = null;
		int port_s = 0;
		int port_r = 0;
		File file;
		String file_n;
		int packets;
		int timeout = 0;
		int seq = 0;
		int seg_size = 124;
		byte[] seg_in;
		byte[] seg_out;
		
		
		//argument input error checking
		try {
		if (args[0].matches("(([0-1]?[0-9]{1,2}\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))") != true && args[0].matches("localhost") != true) {
			System.out.println("Connection Error: Incorrect IP address format");
			System.exit(0);
		} else ip = InetAddress.getByName(args[0]);
		if (args[1].matches("-?\\d+(\\.\\d+)?") == false || args[2].matches("-?\\d+(\\.\\d+)?") == false) {
			System.out.println("Sender and/or reciever port number must be numbers");
			System.exit(0);
    	} else if ((Integer.parseInt(args[1]) <= 1024 || Integer.parseInt(args[1]) >= 49151) || (Integer.parseInt(args[2]) <= 1024 || Integer.parseInt(args[2]) >= 49151)) {
    		System.out.println("Sender and/or reciever port number is invalid");
			System.exit(0);
    	} else {
    		port_r = Integer.parseInt(args[1]);
    		port_s = Integer.parseInt(args[2]);
    	}
		if (args[4].matches("\\d+") != true) {
			System.out.println("Timeout must be a number");
			System.exit(0);
		} else timeout = Integer.parseInt(args[4]);
		} catch(Exception e) {
			System.out.println("Connection Error: Cannot process inputted connection fields");
  		  	System.exit(0);
		}
		
		//initialize connection
		socket = new DatagramSocket(port_s);
		file_n = args[3];
		
		try {
			//wait for start of transmission message from receiver
			seg_in = new byte[seg_size];
			inbound =  new DatagramPacket(seg_in, seg_in.length);
			boolean waiting = true;
			while (waiting == true) {
				socket.receive(inbound);
				if (new String(inbound.getData(), 0, inbound.getLength()).matches("start")) 
					waiting = false;
			}
			System.out.println("Sender connected with receiver, file transfer will commence");	
		} catch(Exception e) {
			System.out.println("Error: " + e.getMessage());
			
		}
		
		//commence file transfer
		socket.setSoTimeout(timeout);
		file = new File(file_n);
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		packets = (int) Math.ceil(((double) file.length()) / (seg_size - 4)); //always round up when calculating total number of packets needed to be sent
		seq = 1; //set initial sequence number
		ByteBuffer buf;
		byte[] readfile;
	
		for (long i = 0; i < packets; i++) {
			//put sequence number in the first 4 bytes of a packet
			seg_out = new byte[seg_size];
			seg_in = new byte[seg_size];
			buf = ByteBuffer.wrap(seg_out);
			seg_out = buf.putInt(seq).array();
			//put file data into the packet
			readfile = new byte[seg_size - 4];
			bis.read(readfile, 0, readfile.length);
			buf.position(4);
			buf.put(readfile);
			//transmit packets and deal with time out of ACKS
			boolean next_packet = false;
			while(next_packet == false) {
				try {
					outbound = new DatagramPacket(seg_out, seg_out.length, ip, port_r);
					socket.send(outbound);
					
					inbound =  new DatagramPacket(seg_in, seg_in.length);
					socket.receive(inbound);
					if (ByteBuffer.wrap(inbound.getData()).getInt() == seq) {
						System.out.println("Segment " + seq + " transmitted succesfully");
						next_packet = true;
						seq = seq + 1;
					}
					
				} catch(SocketTimeoutException e){
					System.out.println("Segment " + seq + " timed out, sender will resend");
				}
			}
		}
		bis.close();
		
		//send end of transmition
		String msg = "end";
		seg_out = msg.getBytes();
		outbound = new DatagramPacket(seg_out, seg_out.length, ip, port_r);
		socket.send(outbound);
		socket.close();
	}
}
