import java.io.*;
import java.nio.*;
import java.util.*;

public class canvas {
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		File file = new File("src/test3.txt");
		BufferedInputStream bis;
        BufferedOutputStream bos;
		
		byte[] senddata = new byte[124];
		ByteBuffer buf = ByteBuffer.wrap(senddata);
		
		String message = "";
		byte[] ms = message.getBytes();
		System.out.println(Arrays.toString(ms));
		
		senddata = buf.putInt(2141).array();
		System.out.println(ByteBuffer.wrap(senddata).getInt());
		System.out.println(Arrays.toString(senddata));
		System.out.println(senddata.length);
		System.out.println(buf.position());
		try {
			bis = new BufferedInputStream(new FileInputStream(file));
			bos = new BufferedOutputStream(new FileOutputStream("testprint.txt"));
			long packets = (long) Math.ceil(((double) file.length()) / (124 - 4));
			System.out.println(file.length());
			System.out.println(packets);
			
			byte[] readfile = new byte[124 - 4];
			byte[] outfile;
			
			for (int i=0; i<packets; i++) {
				readfile = new byte[124 - 4];
				outfile = new byte[124 - 4];
				bis.read(readfile, 0, readfile.length);
				buf.position(4);
				buf.put(readfile);
				buf.position(0);
				System.out.println(buf.getInt());
				System.out.println(new String(senddata, "UTF-8"));
				buf.position(4);
				buf.get(outfile);
				bos.write(outfile, 0, outfile.length);
			}
			bos.close();
			bis.close();
			long elapsedTime = (new Date()).getTime() - startTime;
			System.out.println("Total file transfer time: " + elapsedTime + " miliseconds" + " (" + (double)(elapsedTime/1000) + " seconds" + ")");
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*File file = new File(".");
		for(String fileNames : file.list()) System.out.println(fileNames);*/
	}
}
