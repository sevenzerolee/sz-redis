package sz_redis.sz_redis;

import java.util.Map;
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
		System.out.println("Hello World!");
		
		ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
//		ApplicationContext context = new ClassPathXmlApplicationContext("spring_new.xml");
		
//		ShardedJedisPool redis = (ShardedJedisPool) context.getBean("shardedJedisPool");
//		ShardedJedis sj = redis.getResource();
//		String value = sj.get("a");
//		System.out.println(value);
		
		JedisCluster jedisCluster = (JedisCluster) context.getBean("jedisCluster");
		
		System.out.println(jedisCluster.get("a"));
		
		for (int i=1; i<20; i++) {
			String key = "a" + i;
			String value = "aaa#" + UUID.randomUUID().toString();
			
			System.out.println(jedisCluster.set(key, value));
		}
		
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
				log.info("Getting keys error: {}" + e);
			} 
			finally {
				log.info("Connection closed.");
				connection.close();// 用完一定要close这个链接！！！
			}
		}
		log.info("Keys gotten!");
		return keys;
	}
	
}
