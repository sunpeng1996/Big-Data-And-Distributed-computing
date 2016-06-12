package cn.hit.topology;

import cn.hit.bolt.LogStat;
import cn.hit.bolt.LogWriter;
import cn.hit.spout.LogReader;
import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

public class Topology {

	public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {
		// TODO Auto-generated method stub
		TopologyBuilder builder = new TopologyBuilder();
		
		builder.setSpout("log-reader", new LogReader(), 1);
		
		builder.setBolt("log-stat",new LogStat(), 2).fieldsGrouping("log-reader",new Fields("user"));
		
		builder.setBolt("log-writer", new LogWriter(), 1).shuffleGrouping("log-stat");
		
		Config conf = new Config();
		StormSubmitter.submitTopology("log-tolology", conf, builder.createTopology());
	}

}
