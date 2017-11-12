package com.microsoft.vmcr8tester.com.microsoft.vmcr8tester.util;

import com.microsoft.vmcr8tester.com.microsoft.vmcr8tester.model.RegionResults;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;

import java.util.List;

@Service
public abstract class RedisDataCollectImpl implements DataCollectorInterface{



    public String getTotalInQueue(  ) {
        return null;
    }

}
