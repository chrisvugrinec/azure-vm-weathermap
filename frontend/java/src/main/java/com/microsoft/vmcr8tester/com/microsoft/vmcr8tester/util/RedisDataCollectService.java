package com.microsoft.vmcr8tester.com.microsoft.vmcr8tester.util;

import com.microsoft.vmcr8tester.com.microsoft.vmcr8tester.model.BuildItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class RedisDataCollectServicex implements RedisCacheInterface{

    JedisShardInfo shardInfo;
    Jedis jedis;

    /*
    @Autowired
    public RedisDataCollectServicex(String redisCachename, String redisCacheKey){
        shardInfo = new JedisShardInfo(redisCachename+".redis.cache.windows.net", 6380, true);
        shardInfo.setPassword(redisCacheKey);
        jedis = new Jedis(shardInfo);
    }
*/

    public List<BuildItem> getQueue() {

        Set<String> keyz =  jedis.keys("*");
        List<BuildItem> result = new ArrayList<>();
        for(String key : keyz){
            BuildItem bi = new BuildItem();
            bi.setMachine(key);
            result.add(bi);
        }
        return result;
    }

}
