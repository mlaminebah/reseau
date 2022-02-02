package fr.ubdx.net.udp;

import java.util.*;
import java.util.logging.Logger;
import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ServerUpperCaseUDP {

	private static final Logger logger = Logger.getLogger(ServerUpperCaseUDP.class.getName());
    private static final int BUFFER_SIZE = 1024;
    private final DatagramChannel dc;
    private final ByteBuffer buff = ByteBuffer.allocateDirect(BUFFER_SIZE);
    private final ArrayList<Charset> availableCharsets;

    public ServerUpperCaseUDP(int port) throws IOException {
        dc = DatagramChannel.open();
        dc.bind(new InetSocketAddress(port));
        logger.info("ServerUpperCaseUDP started on port " + port);
        availableCharsets = new ArrayList <> ();
        availableCharsets.add(StandardCharsets.ISO_8859_1);
        availableCharsets.add(StandardCharsets.US_ASCII);
        availableCharsets.add(StandardCharsets.UTF_16);
        availableCharsets.add(StandardCharsets.UTF_16BE);
        availableCharsets.add(StandardCharsets.UTF_16LE);
        availableCharsets.add(StandardCharsets.UTF_8);
    }
    
    private static ByteBuffer createPacket(String s, Charset cs) {
        ByteBuffer csname = Charset.forName("ASCII").encode(cs.name());
        ByteBuffer content = cs.encode(s);
        ByteBuffer packet = ByteBuffer.allocate(4 + csname.remaining() + content.remaining());
        packet.putInt(csname.remaining());
        packet.put(csname);
        packet.put(content);
        packet.flip();
        return packet;
    }

    public void serve() throws IOException {
        while (!Thread.interrupted()) {
          // TODO
         // 1) receive request from client
        	this.buff.clear();
        	InetSocketAddress exp = (InetSocketAddress) dc.receive(this.buff);
        	System.out.println("Received "+this.buff.position()+" bytes from "+exp.toString());
        	this.buff.flip();
            // 2) decode msg in request
        	int size = this.buff.getInt();
        	String csName = new String ();
        	ByteBuffer bfcs = ByteBuffer.allocate(BUFFER_SIZE);
        	for (int i = 0; i < size; i ++)
        		csName += (char)this.buff.get();
        	
        	String msg = Charset.forName(csName).decode(this.buff).toString();
        	System.out.println("content : "+msg);
            
        	// 3) 
        	String upperCaseMsg = msg.toUpperCase();
        	List <Charset> charsets = new ArrayList <Charset>(Charset.availableCharsets().values());
        	Charset cs = charsets.get(new Random().nextInt(charsets.size()));
        	bfcs.clear();
        	bfcs.put(this.createPacket(upperCaseMsg, cs));
        	int posi = bfcs.position();
            // 4) send the packet to client
        	bfcs.flip();
        	dc.send(bfcs, exp);
        	System.out.println(("Sent "+posi+" bytes from /"+exp.toString()));
        	System.out.println("decoded content : "+upperCaseMsg);
          
        }
    }

    public static void usage() {
        System.out.println("Usage : ServerUpperCaseUDP port");
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            usage();
            return;
        }
        ServerUpperCaseUDP server;
        int port = Integer.valueOf(args[0]);
        if (!(port >= 1024) & port <= 65535) {
            logger.severe("The port number must be between 1024 and 65535");
            return;
        }
        try {
            server = new ServerUpperCaseUDP(port);
        } catch (BindException e) {
            logger.severe("Server could not bind on " + port + "\nAnother server is probably running on this port.");
            return;
        }
        server.serve();
    }
}
