package cn.itcast.hadoop.hdfs;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

public class HDFSDemo {

	public static void main(String[] args) throws IOException, URISyntaxException {
		FileSystem fs = FileSystem.get(new URI("hdfs://itcast01:9000"),new Configuration());
		
		InputStream in = fs.open(new Path("/jdk"));
		OutputStream out = new FileOutputStream("C:/JDK1.7");
		IOUtils.copyBytes(in, out, 4096, true);
		
	}

}
