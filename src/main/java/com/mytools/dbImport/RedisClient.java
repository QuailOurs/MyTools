package com.mytools.dbImport;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis客户端
 *
 * @author liuyunlong]
 * @since [2018.07.12]
*/
@Slf4j
public class RedisClient {

	private static RedisClient INSTANCE = new RedisClient();           //单实例对象
	private JedisPool jedisPool = null;                               //redis客户端

	//获取单实例对象
	public static RedisClient getInstance()
	{
		return INSTANCE;
	}

	//初始化
	public void init(int maxActive, int maxIdle, int minIdle, Boolean isTestOnBorrow, Boolean isTestOnReturn,
					 Boolean isTestWhileIdle, int perEvictionRun , int timeBetweenEvictionRunsMillis, int maxWaitMillis,
					 int iCreateConnectMaxWait, String host, int port, String password) throws Exception
	{
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(maxActive);                                          //设置最大连接数
		config.setMaxIdle(maxIdle);                                             //设置最大空闲连接数
		config.setMinIdle(minIdle);                                             //设置最小空闲连接数
		config.setTestOnBorrow(isTestOnBorrow);                                 //设置获取连接点是否测试连接是否可用
		config.setTestOnReturn(isTestOnReturn);                                 //设置将连接放还到连接池时, 是否检查连接是否可用
		config.setTestWhileIdle(isTestWhileIdle);                               //设置idle状态监测用异步线程evict进行检查
		config.setNumTestsPerEvictionRun(perEvictionRun);                       //设置一次最多evict的pool里的jedis实例个数
		config.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis); //设置获取test idle 线程的时间间隔
		config.setMaxWaitMillis(maxWaitMillis);                                 //设置获得jedis连接时最大等待wait时间

		//this.jedis = new JedisCluster(nodeSet , iCreateConnectMaxWait , config);
		this.jedisPool = new JedisPool(config, host, port, iCreateConnectMaxWait, password);
	}

	public Long sadd(String key, String val) {
		Jedis jedis = null;
		Long ret = 0L;
		try {
			jedis = this.jedisPool.getResource();
			ret = jedis.sadd(key, val);
		} catch (Exception e) {
			log.error("sadd", e);
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}

		return ret;
	}

}
