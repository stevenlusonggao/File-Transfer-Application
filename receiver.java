import java.net.*;
import java.nio.ByteBuffer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.JOptionPane;
import java.util.Date;

public class receiver {
	DatagramSocket socket;
	DatagramPacket inbound;
	DatagramPacket outbound;
	InetAddress ip;
    boolean connected = false;
    int seg_size = 124;
    byte[] seg_out;
    byte[] seg_in;
    int port_s = 0;
	int port_r = 0;
	
	public static void main(String[] args) throws Exception {
		gui g = new gui();
		receiver receiver = new receiver();
		
		//transfer file button
		g.transb.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e)  {
		    	if (receiver.connected == true)
			    		return;
		    	
		    	//connection settings input checking
		    	if (g.hostaddtext.getText().isEmpty() || g.portn_rtext.getText().isEmpty() || g.portn_stext.getText().isEmpty() || g.filentext.getText().isEmpty()) {
		    		JOptionPane.showMessageDialog(g.window,"Connection setting fields cannot be empty","Input error", JOptionPane.ERROR_MESSAGE);
		    		return;
		    	}
		    	if (g.hostaddtext.getText().matches("(([0-1]?[0-9]{1,2}\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))") != true && g.hostaddtext.getText().matches("localhost") != true) {
		    		JOptionPane.showMessageDialog(g.window,"Invalid host IP address format","Input error", JOptionPane.ERROR_MESSAGE);
		    		return;
		    	}
		    	if (g.portn_rtext.getText().matches("-?\\d+(\\.\\d+)?") == false || g.portn_stext.getText().matches("-?\\d+(\\.\\d+)?") == false) {
		    		JOptionPane.showMessageDialog(g.window,"Sender and/or reciever port number must be numbers","Input error", JOptionPane.ERROR_MESSAGE);
		    		return;
		    	} else if ((Integer.parseInt(g.portn_rtext.getText()) <= 1024 || Integer.parseInt(g.portn_rtext.getText()) >= 49151) || (Integer.parseInt(g.portn_stext.getText()) <= 1024 || Integer.parseInt(g.portn_stext.getText()) >= 49151)) {
		    		JOptionPane.showMessageDialog(g.window,"Sender and/or reciever port number is invalid","Input error", JOptionPane.ERROR_MESSAGE);
		    		return;
		    	} else {
		    		receiver.port_r = Integer.parseInt(g.portn_rtext.getText());
		    		receiver.port_s = Integer.parseInt(g.portn_stext.getText());
		    	}
		    	
		    	try {
		    		//initialize UDP connection
		    		receiver.socket = new DatagramSocket(receiver.port_r);
		    		receiver.ip = InetAddress.getByName(g.hostaddtext.getText());
		    		receiver.connected = true;
		    		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(g.filentext.getText()));
		    		//send start transmission message to sender
		    		String msg = "start";
					receiver.seg_out = msg.getBytes();
					receiver.outbound = new DatagramPacket(receiver.seg_out, receiver.seg_out.length, receiver.ip, receiver.port_s);
					receiver.socket.send(receiver.outbound);
					//begin timer
					long startTime = System.currentTimeMillis();
					//setup for file reading and writing to output file
					int counter = 1; //counter for timeout simulation
					int packet_count = 0;
					byte[] outfile;
					ByteBuffer buf_in;
					ByteBuffer buf_out;
					boolean eot = false;
					
					//start reading packets from sender
					while (eot == false) {
						receiver.seg_in = new byte[receiver.seg_size];
						receiver.seg_out = new byte[receiver.seg_size];
						receiver.inbound = new DatagramPacket(receiver.seg_in, receiver.seg_in.length);
						receiver.socket.receive(receiver.inbound);
						buf_in = ByteBuffer.wrap(receiver.inbound.getData());
						buf_out = ByteBuffer.wrap(receiver.seg_out);
						outfile = new byte[receiver.seg_size - 4];
						
						//end read if eot message is received
						if (new String(receiver.inbound.getData(), 0, receiver.inbound.getLength()).matches("end")) {
							System.out.println("End of file transmission");
							eot = true;
							receiver.connected = false;
							receiver.socket.close();
							bos.close();
							long elapsedTime = (new Date()).getTime() - startTime;
							JOptionPane.showMessageDialog(g.window,"Total file transfer time: " + elapsedTime + " miliseconds" + " (" + (double)(elapsedTime/1000) + " seconds" + ") \n" + "Total packets transfered: " + g.receivedn.getText(),"File Transfer Completed", JOptionPane.PLAIN_MESSAGE);
							g.receivedn.setText("0");
							break;
						//drop packet if it is the 10th packet and if unreliable option is on
						} else if (g.reliable == false && counter == 10) {
							System.out.println("Dropping packet" + buf_in.getInt());
							counter = 1;
						//read packet and write to file
						} else {
							//send ACK
							receiver.seg_out = buf_out.putInt(buf_in.getInt()).array();
							receiver.outbound = new DatagramPacket(receiver.seg_out, receiver.seg_out.length, receiver.ip, receiver.port_s);
							receiver.socket.send(receiver.outbound);
							//write to output file
							buf_in.get(outfile);
							bos.write(outfile, 0, outfile.length);
							counter = counter + 1;
							packet_count = packet_count + 1;
							g.receivedn.setText(Integer.toString(packet_count));
							/*updategui update = new updategui(packet_count, g); //start a new thread to update segment counter display while still in a while loop
							SwingUtilities.invokeAndWait(update);
							Thread thread = new Thread(update);
			            	thread.start();
			            	thread.join();*/
			            	
						}
					}
				} catch (Exception e1) {
						System.out.println("Error: " + e1.getMessage());
						receiver.socket.close();
				}
		    }
				});
	}
	
	
}

/*class updategui implements Runnable {
	int counter;
	gui g;
	public updategui (int counter, gui g) {
		this.counter = counter;
		this.g= g;
	}
	@Override
	public void run() {
		g.receivedn.setText(Integer.toString(counter));
		return;
	}
}*/