package main.java.cn.chinahadoop.practise.hdfsConsumer;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;

public class HdfsConsumer {
    private final ConsumerConnector consumer;
    private final String topic;
    private  ExecutorService executor;
    private FileSystem hdfs;
    public HdfsConsumer(String a_zookeeper, String a_groupId, String a_topic) {
        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(
                createConsumerConfig(a_zookeeper, a_groupId));
        this.topic = a_topic;
        try {
                        hdfs = FileSystem.get(new HdfsConfiguration());
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }

    }

    public void shutdown() {
        if (consumer != null) consumer.shutdown();
        if (executor != null) executor.shutdown();
    }

    public void run(int a_numThreads) {
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, new Integer(a_numThreads));
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);

        
        executor = Executors.newFixedThreadPool(a_numThreads);

        int threadNumber = 0;
        for (final KafkaStream stream : streams) {
            executor.submit(new SubTaskConsumer(stream, threadNumber,hdfs));
            threadNumber++;
        }
    }

    private static ConsumerConfig createConsumerConfig(String a_zookeeper, String a_groupId) {
        Properties props = new Properties();
        props.put("zookeeper.connect", a_zookeeper);
        props.put("group.id", a_groupId);
        props.put("zookeeper.session.timeout.ms", "60000");
        props.put("zookeeper.sync.time.ms", "2000");
        props.put("auto.commit.interval.ms", "1000");
        props.put("auto.offset.reset","smallest");
        return new ConsumerConfig(props);
    }

    public static void main(String[] args) throws Exception {
       String conf_path ="";
    	
    	if (args!=null&&args.length==1){
    		conf_path=args[0];
    	}else{
    		System.out.print("please input consumerpropertie path");
    		System.exit(0);
    	}
    	Properties pro = new Properties();
    	FileInputStream in = new FileInputStream(conf_path);
    	pro.load(in);
    	String zooKeeper = pro.getProperty("zookeeper");
    	 int threads =1;
    	 String threadString = pro.getProperty("threadsnum");
    	 if (!threadString.isEmpty())
    		 threads = Integer.parseInt(threadString);
    	 String topic = pro.getProperty("topic");
    	 String groupId = pro.getProperty("groupId");
    	//String zooKeeper = "DX-1:2181";
         //String zooKeeper = "192.168.1.101:2181";
//        String topic = "logTopic3";
//        String groupId = "testGroup3";
//        int threads = Integer.parseInt("1");
        HdfsConsumer example = new HdfsConsumer(zooKeeper, groupId, topic);
        example.run(threads);
    }
}
