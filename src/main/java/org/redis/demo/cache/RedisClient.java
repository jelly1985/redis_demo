package org.redis.demo.cache;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

@Component
public class RedisClient implements InitializingBean {
	private JedisSentinelPool pool;
	
	void runTest() {
		while (true) {
			try (Jedis jedis = pool.getResource();) {
			    jedis.auth("123456");
			    jedis.set("foo", "bar");
			    String result = jedis.get("foo");
			    System.out.println("result->" + result);
			    //assertEquals("bar", jedis.get("foo"));
			    jedis.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		try {
			GenericObjectPoolConfig<Jedis> config = 
					new GenericObjectPoolConfig<>();

			Set<String> sentinels = new HashSet<>();
			HostAndPort sentinel1 = 
					new HostAndPort("172.17.0.2", 5000);
			HostAndPort sentinel2 = 
					new HostAndPort("172.17.0.2", 5001);
			HostAndPort sentinel3 = 
					new HostAndPort("172.17.0.2", 5002);
			sentinels.add(sentinel1.toString());
			sentinels.add(sentinel2.toString());
			sentinels.add(sentinel3.toString());
			  
		    pool = 
		    		new JedisSentinelPool(
		    				"mymaster", sentinels, config, 
		    				1000, "123456", 1);
		    
		    new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					runTest();
				}
		    	
		    }).start();
		    
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
}
