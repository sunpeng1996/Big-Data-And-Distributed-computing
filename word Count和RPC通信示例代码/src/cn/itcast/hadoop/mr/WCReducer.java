package cn.itcast.hadoop.mr;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class WCReducer extends Reducer<Text, LongWritable, Text,LongWritable > {

	@Override
	protected void reduce(Text k2, Iterable<LongWritable> v2s,Context context)
			throws IOException, InterruptedException {
		//input data
		Text k3 = k2;
		//定义一个计数器
		long counter = 0;
		//循环迭代V2S
		for(LongWritable i: v2s){
			counter += i.get();
		}
		//output data
		context.write(k3, new LongWritable(counter));
		
	}

	

}
