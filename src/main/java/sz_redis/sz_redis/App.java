package sz_redis.sz_redis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
//		ApplicationContext context = new ClassPathXmlApplicationContext("spring_dev.xml");
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
		
		List<Obj> list = new ArrayList<Obj>();
		for (int i=1; i<10; i++) {
			list.add(new Obj("Name", 10+i, new Date()));
		}
		
		
		
		for (int i=1; i<200; i++) {
			String key = "a" + i;
			String value = "aaa#" + UUID.randomUUID().toString();
			
			if (i % 5 == 0) {
				key = "b" + i;
			}
			
//			log.info(jedisCluster.set(key, value));
			log.info(jedisCluster.setex(key, 300, value));
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
		
		//存对象
//        Perso?n p=new Person();  //peson类记得实现序列化接口 Serializable
        Obj p = new Obj();
        p.setAge(20);
        p.setName("姚波");
//        p.setId(1);
        p.setDate(new Date());
//        jedis.set("person".getBytes(), serialize(p));
//        jedisCluster.set("obj".getBytes(), serialize(p));
//        byte[] byt=jedis.get("person".getBytes());
//        byte[] byt = jedisCluster.get("obj".getBytes());
//        Object obj=unserizlize(byt);
//        if(obj instanceof Obj){
//            System.out.println(obj);
//        }
		
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

class Obj implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8775341447794170330L;
	
	private String name;
	private int age;
	private Date date;
	
	public Obj() {}
	
	public Obj(String name, int age, Date date) {
		this.name = name;
		this.age = age;
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
}
