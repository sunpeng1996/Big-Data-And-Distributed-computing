package cn.itcast.hadoop.rpc;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.RPC.Server;

public class RPCServer implements Bizable {

	public String sayhi(String name){
		return "hi"+name;
	}
	
	public static void main(String[] args) throws Exception, IOException {
		//方法调用链
		Server server = new RPC.Builder(new Configuration()).setProtocol(Bizable.class).
						setInstance(new RPCServer()).setBindAddress("192.168.8.100").setPort(9528).build();
		server.start();
	}

}
