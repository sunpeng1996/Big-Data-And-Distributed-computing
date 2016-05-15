package cn.itcast.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.generated.master.tablesDetailed_jsp;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author 作者: 如今我已·剑指天涯
 * @Description:java接口使用Hbase
 * @通过连接zookeeper直接创建成功表，并不需要连接Hmaster
 *创建时间:2016年5月15日下午7:30:21
 */
public class HbaseDemo {
	
	private static Configuration conf;
	
	@Before
	private void init(){
		conf  = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum", "itcast04:2181,itcast05:2181,itcast06:2181");
	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws MasterNotRunningException, ZooKeeperConnectionException, IOException {
		
		HBaseAdmin admin = new HBaseAdmin(conf);
		HTableDescriptor htd = new HTableDescriptor(TableName.valueOf("peoples"));
		HColumnDescriptor hcd_info = new HColumnDescriptor("info");
		hcd_info.setMaxVersions(3);
		HColumnDescriptor hcd_data = new HColumnDescriptor("data");
		htd.addFamily(hcd_data);
		htd.addFamily(hcd_info);
		admin.createTable(htd);
		admin.close();
	}
	
	/*
	 * 插入数据
	 */
	@Test
	public void  testPut() throws IOException {
		HTable table = new HTable(conf, "peoples");
		Put put = new Put(Bytes.toBytes("kr0001"));
		put.add(Bytes.toBytes("info"), Bytes.toBytes("name"), Bytes.toBytes("zhangsanfeng"));
		put.add(Bytes.toBytes("info"), Bytes.toBytes("age"), Bytes.toBytes("300"));
		put.add(Bytes.toBytes("info"), Bytes.toBytes("money"), Bytes.toBytes(30000000));
		
		table.put(put);
		table.close();
	}
	
	/**
	 * 
	 * @author 作者: 如今我已·剑指天涯
	 * @Description:批量插入100万条数据
	TODO
	 *创建时间:2016年5月15日下午8:14:34
	 * @throws IOException
	 */
	@Test
	public void testPutAll() throws IOException{
		//HTablePool pool 
		HTable table = new HTable(conf, "peoples");
		List puts = new ArrayList<Put>(10000);
		for(int i = 1 ; i<= 10000000 ; i++){
			Put put = new Put(Bytes.toBytes("kr"+i));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("money"), Bytes.toBytes(""+i));
			puts.add(put);
			if(i % 10000==0){
				table.put(puts);
				puts = new ArrayList<Put>();
			}
			table.put(puts);
		}
		table.close();
	}
	
	@Test
	public void testGet() throws IOException{
		HTable table = new HTable(conf, "peoples");
		Get get = new Get(Bytes.toBytes("kr313123131"));
		Result result = table.get(get);
		String value = Bytes.toString(result.getValue(Bytes.toBytes("info"), Bytes.toBytes("money")));
		System.out.println(value);
		table.close();
	}
	
	@Test
	public void testScan() throws IOException{
		HTable table = new HTable(conf, "peoples");
		Scan scan = new Scan(Bytes.toBytes("kr31231"), Bytes.toBytes("kr9931311"));
		ResultScanner scanner = table.getScanner(scan);
		for (Result result : scanner) {
			String r = Bytes.toString(result.getValue(Bytes.toBytes("info"),Bytes.toBytes("money")));
			System.out.println(r);
		}
		table.close();		
	}
	
	@Test
	public void testTelete() throws IOException {
		HTable table = new HTable(conf, "peoples");
		Delete delete = new Delete(Bytes.toBytes("kr313131"));
		table.delete(delete);
		table.close();
	}

}
