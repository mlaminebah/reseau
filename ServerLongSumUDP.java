package fr.ubdx.net.udp;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class ServerLongSumUDP {

    private static final byte OPCODE_OP = 1;
    private static final byte OPCODE_ACK = 2;
    private static final byte OPCODE_SUM = 3;

    public static final int BUFFER_SIZE = 1024;
    private final DatagramChannel dc;
    private final ByteBuffer receiveBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    private final ByteBuffer sendBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    private HashMap <InetSocketAddress, HashMap<Long,HashMap <Long,Long>>> operands = new HashMap <> ();
    private HashMap <InetSocketAddress,HashMap<Long,Long>> totals = new HashMap <>();
    
    public ServerLongSumUDP(int port) throws IOException {
        dc = DatagramChannel.open();
        dc.bind(new InetSocketAddress(port));
        System.out.println("ServerLongSumUDP started on port " + port);
    }

    
    private void processRequest(InetSocketAddress client,ByteBuffer buff) throws IOException {
        //TODO
    	long session_id = buff.getLong();
    	long idPosOper =  buff.getLong();
    	long totalOper = buff.getLong();
    	long opValue = buff.getLong();
    	if (!totals.containsKey(client)) totals.put(client, new HashMap<>());
    	if (!totals.get(client).containsKey(session_id)) totals.get(client).put(session_id, totalOper);
    	if (!operands.containsKey(client)) operands.put(client, new HashMap<>());
    	if (!operands.get(client).containsKey(session_id)) 
    		operands.get(client).put(session_id,new HashMap<>());
    	if (!operands.get(client).get(session_id).containsKey(idPosOper)) 
			operands.get(client).get(session_id).put(idPosOper, opValue);
    	
    	if (this.totals.get(client).get(session_id) == this.operands.get(client).get(session_id).size()) {
    		HashMap<Long, Long> operands = this.operands.get(client).get(session_id);
    		long sum = 0;
    		for (Long i : operands.keySet()) {
    			sum += operands.get(i);
    		}
    		this.sendSUM(client, session_id, sum);
    	} else {
    		this.sendACK(client, session_id, idPosOper);
    	}
    	
    	
    }

    private void sendSUM(InetSocketAddress client, long session, long sum) throws IOException {
        //TODO
    	this.sendBuffer.clear();
    	this.sendBuffer.put (OPCODE_SUM);
    	this.sendBuffer.putLong(session);
    	this.sendBuffer.putLong(sum);
    	this.sendBuffer.flip();
    	this.dc.send(this.sendBuffer, client);
    	
    	
    }

    private void sendACK(InetSocketAddress client, long session, long pos) throws IOException {
        //TODO
    	this.sendBuffer.clear();
    	this.sendBuffer.put(OPCODE_ACK);
    	this.sendBuffer.putLong(session);
    	this.sendBuffer.putLong(pos);
    	this.sendBuffer.flip();
    	this.dc.send(this.sendBuffer, client);
    }




    public void serve() throws IOException {
        while (!Thread.interrupted()) {
            receiveBuffer.clear();
            InetSocketAddress client = (InetSocketAddress) dc.receive(receiveBuffer);
            receiveBuffer.flip();
            if (receiveBuffer.remaining() == 0) {
                continue;
            }
            byte opCode = receiveBuffer.get();
            if(opCode==OPCODE_OP){
                processRequest(client,receiveBuffer);
            }
        }
    }

    public static void usage() {
        System.out.println("Usage : ServerLongSumUDP port");
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            usage();
            return;
        }
        ServerLongSumUDP server;
        int port = Integer.valueOf(args[0]);
        if (!(port >= 1024) & port <= 65535) {
            System.out.println("The port number must be between 1024 and 65535");
            return;
        }
        try {
            server = new ServerLongSumUDP(port);
        } catch (BindException e) {
            System.out.println("Server could not bind on " + port + "\nAnother server is probably running on this port.");
            return;
        }
        server.serve();
    }



}
