package cn.itcast.hadoop.rpc;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

public class RPCClient {
	
	
	

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Bizable proxy = RPC.getProxy(Bizable.class, 10010, new InetSocketAddress("192.168.8.100", 9528), new Configuration());
		String result = proxy.sayhi("sunpeng");
		System.out.println(result);
		RPC.stopProxy(proxy);
	
	}

}
