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

public class ReadFileWithEncoding {

    private static void usage(){
        System.out.println("Usage: ReadFileWithEncoding charset filename");
    }

    private static String stringFromFile(Charset cs,Path path) throws IOException {
        // TODO
    	FileChannel fin = FileChannel.open(path,StandardOpenOption.READ);
    	int size = (int)fin.size ();
    	ByteBuffer bf = ByteBuffer.allocate (size);
    	
    	while (fin.read(bf) != -1 && bf.hasRemaining ());
    	
    	bf.flip();
        return cs.decode(bf).toString();
       
    }

    public static void main(String[] args) throws IOException {
        if (args.length!=2){
            usage();
            return;
        }
        Charset cs=Charset.forName(args[0]);
        Path path=Paths.get(args[1]);
        System.out.print(stringFromFile(cs,path));
    }


}
