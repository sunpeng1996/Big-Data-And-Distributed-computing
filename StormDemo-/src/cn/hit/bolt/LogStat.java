package cn.hit.bolt;

import java.util.HashMap;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class LogStat extends BaseRichBolt {
	
	private OutputCollector _collector;
	private Map<String,Integer> _pvMap = new HashMap<String,Integer>();

	public void execute(Tuple tuple) {
		String user = tuple.getStringByField("user");
		
		if (_pvMap.containsKey(user)) {
			_pvMap.put(user, _pvMap.get(user)+1);
		}else
			_pvMap.put(user, 1);
		
		_collector.emit(new Values(user,_pvMap.get(user)));
	}

	public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
		_collector =collector;
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("user","pv"));
	}



}
