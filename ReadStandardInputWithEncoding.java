package fr.ubdx.net.buffers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ReadStandardInputWithEncoding {

    private static final int BUFFER_SIZE = 1024;

    private static void usage(){
        System.out.println("Usage: ReadStandardInputWithEncoding charset");
    }



    private static String stringFromStandardInput(Charset cs) throws IOException {
        // TODO
    	ReadableByteChannel in = Channels.newChannel(System.in);
    	ByteBuffer bf = ByteBuffer.allocate(16);
    	while (in.read(bf) != -1) {
    		if (!bf.hasRemaining()) {
    			
    			ByteBuffer buf = ByteBuffer.allocate(bf.capacity()*2);
    			bf.flip ();
    			buf.put (bf);
    			bf = buf;
    		}
    	}
    	bf.flip();
        return cs.decode(bf).toString();
    }

    public static void main(String[] args) throws IOException {
        if (args.length!=1){
            usage();
            return;
        }
        Charset cs=Charset.forName(args[0]);
        System.out.print(stringFromStandardInput(cs));


    }


}
