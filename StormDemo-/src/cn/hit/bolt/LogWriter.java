package cn.hit.bolt;
/**
 * 最后的组件
 */
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

public class LogWriter extends BaseRichBolt {

	public void execute(Tuple tuple) {
		System.out.println(String.format("%s:%d", tuple.getStringByField("user"),
				tuple.getStringByField("pv")));
	}

	public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
		// TODO Auto-generated method stub

	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}

}
