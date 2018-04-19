import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class gui {
	public JLabel hostadddis, portn_sdis, portn_rdis, filendis, received, receivedn;
	public JTextField hostaddtext, portn_stext, portn_rtext, filentext;
	public JButton transb, reliableb;
	public boolean reliable = true;
	public JFrame window;
	public gui() {
		window = new JFrame("File Receiver");
		JPanel panelm = new JPanel();
		panelm.setLayout(new BoxLayout(panelm, BoxLayout.PAGE_AXIS));
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		JPanel panel3 = new JPanel();
		JPanel panel4 = new JPanel();
		JPanel panel5 = new JPanel();
		
		hostadddis = new JLabel("Host Address: ");
		panel1.add(hostadddis);
		hostaddtext = new JTextField(16);
		panel1.add(hostaddtext);
		portn_sdis = new JLabel("Sender Port #: ");
		panel2.add(portn_sdis);
		portn_stext = new JTextField(5);
		panel2.add(portn_stext);
		portn_rdis = new JLabel("Receiver Port #: ");
		panel2.add(portn_rdis);
		portn_rtext = new JTextField(5);
		panel2.add(portn_rtext);
		filendis = new JLabel("File Name: ");
		panel3.add(filendis);
		filentext = new JTextField(16);
		panel3.add(filentext);
		reliableb = new JButton("SWITCH TO UNRELIABLE TRANSFER");
		panel4.add(reliableb);
		transb = new JButton("TRANSFER FILE");
		panel4.add(transb);
		received = new JLabel("CURRENT NUMBER OF RECEIVED IN-ORDER PACKETS: ");
		panel5.add(received);
		receivedn = new JLabel("0");
		panel5.add(receivedn);
		
		panelm.add(panel1);
		panelm.add(panel2);
		panelm.add(panel3);
		panelm.add(panel4);
		panelm.add(panel5);
        window.getContentPane().add(panelm);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setVisible(true);
		
		reliableb.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e)  {
		    	if(reliable == true) {
		    		reliable = false;
		    		reliableb.setText("SWITCH TO RELIABLE TRANSFER");
		    	} else {
		    		reliable = true;
		    		reliableb.setText("SWITCH TO UNRELIABLE TRANSFER");
		    	}
		    	
		    }
		});
	}
	public static void main(String[] args) {
	}

}
