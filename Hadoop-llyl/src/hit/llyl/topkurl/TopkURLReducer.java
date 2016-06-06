package hit.llyl.topkurl;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.hadoop.hdfs.protocol.RollingUpgradeInfo.Bean;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import cn.itcast.hadoop.mr.flowsum.FlowBean;

public class TopkURLReducer  extends Reducer<Text, FlowBean, Text, LongWritable>{
			
			private TreeMap<FlowBean,String> treeMap = new TreeMap<FlowBean, String>();
			private double globalCount = 0;
			
			protected void reduce(Text key, Iterable<FlowBean> values,Context context)
					throws IOException, InterruptedException {
				
				long up_sum = 0;
				long down_sum = 0;
				for (FlowBean bean : values) {
					up_sum += bean.getUp_flow();
					down_sum += bean.getD_flow();
				}
				
				FlowBean flowBean = new FlowBean("", up_sum, down_sum);
				//每求得一次url的总流量，就加到全局的流量计数器中，等所有的记录完成之后，就是全局的总和
				globalCount += flowBean.getS_flow();
				treeMap.put(flowBean ,key.toString() );
				
			}
			
			//cleanup方法是在reducer方法即将退出时调用一次
			@Override
			protected void cleanup(Context context)
					throws IOException, InterruptedException {
				Set<Entry<FlowBean, String>> entrySet = treeMap.entrySet();
				double tempCount = 0;
				for (Entry<FlowBean, String> ent : entrySet) {
					
					if (tempCount / globalCount < 0.8 ) {
						context.write(new Text(ent.getValue()), new LongWritable(ent.getKey().getS_flow()));
						tempCount += ent.getKey().getS_flow();
					}else {
						return;
					}
					
					
				}
			}
}
