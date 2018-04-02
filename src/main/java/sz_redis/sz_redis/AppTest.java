package sz_redis.sz_redis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

/**
 * Hello world!
 * 
 */
public class AppTest {
	
	static final Logger log = Logger.getLogger(AppTest.class.getSimpleName());
	
	public static void main(String[] args) {
		
		log.info("Hello World!");
		
		ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
//		ApplicationContext context = new ClassPathXmlApplicationContext("spring_dev.xml");
//		ApplicationContext context = new ClassPathXmlApplicationContext("spring_new.xml");
		
//		ShardedJedisPool redis = (ShardedJedisPool) context.getBean("shardedJedisPool");
//		ShardedJedis sj = redis.getResource();
//		String value = sj.get("a");
//		System.out.println(value);
		
		JedisCluster jedisCluster = (JedisCluster) context.getBean("jedisCluster");
		
//		log.info("## " + jedisCluster.del("a"));
//		log.info(jedisCluster.get("a"));
		
		
		
		Set<String> set = keysAll(jedisCluster, "*b*");
		for (String s : set) {
			System.out.println(s);
		}
		
		for (String s : set) {
			long result = jedisCluster.del(s);
			System.out.println("### " + result);
		}
		
		Set<String> set2 = keysAll(jedisCluster, "*b*");
		for (String s : set2) {
			System.out.println(s);
		}
		
		
		
	}
	
	public static Set<String> keysAll(JedisCluster jedisCluster, String pattern) {
		Set<String> keys = new HashSet<String>();
		if (null != pattern) {
			Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
			for (String k : clusterNodes.keySet()) {
				JedisPool jp = clusterNodes.get(k);
				Jedis connection = jp.getResource();
				try {
					keys.addAll(connection.keys(pattern));
				} 
				catch (Exception e) {
					e.printStackTrace();
				} 
				finally {
					// 用完一定要close这个链接！！！
					if (null != connection) {
						connection.close();
					}
				}
			}
		}
		
		return keys;
	}
	
	/**
	 * 集群中实现模糊查询
	 * @param jedisCluster
	 * @param pattern
	 * @return
	 */
	public static Set<String> keys2(JedisCluster jedisCluster, String pattern) {
		log.info("Start getting keys...");
		Set<String> keys = new HashSet<String>();
		Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
		for (String k : clusterNodes.keySet()) {
			log.info("Getting keys from: {}" + k);
			JedisPool jp = clusterNodes.get(k);
			Jedis connection = jp.getResource();
			try {
				keys.addAll(connection.keys(pattern));
			}
			catch (Exception e) {
				e.printStackTrace();
			} 
			finally {
				log.info("Connection closed.");
				// 用完一定要close这个链接！！！
				if (null != connection) {
					connection.close();
				}
			}
		}
		log.info("Keys gotten!");
		
		return keys;
	}
	
	/**
	 * 集群中实现模糊查询
	 * @param jedisCluster
	 * @param pattern
	 * @return
	 */
	public static TreeSet<String> keys(JedisCluster jedisCluster, String pattern) {
		log.info("Start getting keys...");
		TreeSet<String> keys = new TreeSet<>();
		Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
		for (String k : clusterNodes.keySet()) {
			log.info("Getting keys from: {}" + k);
			JedisPool jp = clusterNodes.get(k);
			Jedis connection = jp.getResource();
			try {
				keys.addAll(connection.keys(pattern));
			}
			catch (Exception e) {
				e.printStackTrace();
			} 
			finally {
				log.info("Connection closed.");
				// 用完一定要close这个链接！！！
				if (null != connection) {
					connection.close();
				}
			}
		}
		log.info("Keys gotten!");
		return keys;
	}
	
	//序列化 
    public static byte [] serialize(Object obj){
        ObjectOutputStream obi=null;
        ByteArrayOutputStream bai=null;
        try {
            bai=new ByteArrayOutputStream();
            obi=new ObjectOutputStream(bai);
            obi.writeObject(obj);
            byte[] byt=bai.toByteArray();
            return byt;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    //反序列化
    public static Object unserizlize(byte[] byt){
        ObjectInputStream oii=null;
        ByteArrayInputStream bis=null;
        bis=new ByteArrayInputStream(byt);
        try {
            oii=new ObjectInputStream(bis);
            Object obj=oii.readObject();
            return obj;
        } catch (Exception e) {
            
            e.printStackTrace();
        }
    
        
        return null;
    }
	
}


