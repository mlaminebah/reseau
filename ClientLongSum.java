package fr.ubdx.net.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;

public class ClientLongSum {

    private static final int BUFFER_SIZE = 1024;

    private static ArrayList<Long> randomLongList(int size){
        Random rng= new Random();
        ArrayList<Long> list= new ArrayList<>(size);
        for(int i=0;i<size;i++){
            list.add(rng.nextLong());
        }
        return list;
    }

    private static boolean checkSum(List<Long> list, long response) {
        long sum = 0;
        for(long l : list)
            sum += l;
        return sum==response;
    }


    /**
     * Write all the longs in list in BigEndian on the server
     * and read the long sent by the server and returns it
     *
     * returns Optional.empty if the protocol is not followed by the server but no IOException is thrown
     *
     * @param sc
     * @param list
     * @return
     * @throws IOException
     */
    private static Optional<Long> requestSumForList(SocketChannel sc, List<Long> list) throws IOException {
       
       // TODO
    	long nbOps = list.size();
    	ByteBuffer bf = ByteBuffer.allocate(BUFFER_SIZE);
    	//envoie du nombre d'opérandes au serveur
    	bf.putInt((int)nbOps);
    	bf.flip();
    	sc.write(bf);
    	bf.clear();
    	//envoie des nbOos pérandes au serveur
    	for (long l : list) {
    		bf.putLong(l);
    		bf.flip();
    		sc.write(bf);
    		bf.clear();
    	}
    	int r = 0;
    	ByteBuffer bfS = ByteBuffer.allocate(8);
    	while ((r = sc.read(bfS)) != -1 && bfS.hasRemaining());
    	if ( r == -1) return Optional.empty();
    	bfS.flip();
    	return Optional.of(bfS.getLong());
    }

    public static void main(String[] args) throws IOException {
        InetSocketAddress server = new InetSocketAddress(args[0],Integer.valueOf(args[1]));
        try (SocketChannel sc = SocketChannel.open(server)) {
            for(int i=0; i<5; i++) {
                ArrayList<Long> list = randomLongList(50);

                Optional<Long> l = requestSumForList(sc, list);
                if (!l.isPresent()) {
                    System.err.println("Connection with server lost.");
                    return;
                }
                if (!checkSum(list, l.get())) {
                    System.err.println("Oups! Something wrong happens!");
                }
            }
            System.err.println("Everything seems ok");
        }
    }
}
