package fr.ubdx.net.buffers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class StoreWithByteOrder {

	public static void usage() {
		System.out.println("USAGE : StoreWithByteOrder [LE|BE] filename");
		System.out.println("\t then provide long numbers separated by new lines");
		System.out.println("\t end using CTRL+D");
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			usage();
			return;
		}
		Path pOut = Paths.get(args[1]);
		FileChannel out = FileChannel.open(pOut, StandardOpenOption.WRITE, StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);
		// TODO
		ByteBuffer bf = ByteBuffer.allocate(Long.BYTES);
		
		switch (args[0].toUpperCase()) {
			case "LE":
			// TODO
				bf.order(ByteOrder.LITTLE_ENDIAN);
			break;
			case "BE":
			// TODO
				bf.order();
			break;
			default:
				System.out.println("Unrecognized option : "+args[0]);
				usage();
				return;
		}
		Scanner sc = new Scanner(System.in);
		while (sc.hasNextLong()) {
			long l = sc.nextLong();
			// TODO
			bf.putLong(l);
			bf.flip();
			out.write(bf);
			bf.clear ();
		}
		out.close();
		sc.close();
	}
}