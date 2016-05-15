package cn.itcast.hadoop.mr;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCount {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Job job = Job.getInstance(new Configuration());
		//注意：main方法所在的类设置进去
		job.setJarByClass(WordCount.class);
		
		job.setMapperClass(WCMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(LongWritable.class);
		FileInputFormat.setInputPaths(job, new Path("/words"));
		
		job.setReducerClass(WCReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);
		FileOutputFormat.setOutputPath(job, new Path("/wcout111"));
		
		job.waitForCompletion(true);
	}
	
	

}
