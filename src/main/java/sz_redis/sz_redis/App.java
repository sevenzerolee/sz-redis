package sz_redis.sz_redis;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
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
public class App {
	
	static final Logger log = Logger.getLogger(App.class.getSimpleName());
	
	public static void main(String[] args) {
		log.info("Hello World!");
		
		ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
//		ApplicationContext context = new ClassPathXmlApplicationContext("spring_new.xml");
		
//		ShardedJedisPool redis = (ShardedJedisPool) context.getBean("shardedJedisPool");
//		ShardedJedis sj = redis.getResource();
//		String value = sj.get("a");
//		System.out.println(value);
		
		JedisCluster jedisCluster = (JedisCluster) context.getBean("jedisCluster");
		
//		log.info("## " + jedisCluster.del("a"));
//		log.info(jedisCluster.get("a"));
		
		
		
		log.info(jedisCluster.set("a", "aaa"));
		log.info("" + jedisCluster.expire("a", 300));
		
		
//		while (jedisCluster.ttl("a") > 0) {
//			log.info("### " + jedisCluster.get("a"));
//			try {
//				Thread.sleep(2000L);
//			} 
//			catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		
		for (int i=1; i<20; i++) {
			String key = "a" + i;
			String value = "aaa#" + UUID.randomUUID().toString();
			
//			log.info(jedisCluster.set(key, value));
			log.info(jedisCluster.setex(key, 30, value));
		}
		
//		Set<String> set = keys2(jedisCluster, "a*");
//		for (String key : set) {
//			log.info(key + ", " + jedisCluster.get(key));
//		}
		
//		log.info(jedisCluster.get("a2"));
		
//		ShardedJedisPool pool = (ShardedJedisPool) context.getBean("shardedJedisPool");
//		
//		for (int i=0; i<20; i++) {
//			String key = "a" + i;
//			String value = "aaa#" + UUID.randomUUID().toString();
//			
//			pool.getResource().set(key, value);
//			
//			
//		}
		
		
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
	
}
