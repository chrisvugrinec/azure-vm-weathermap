package com.microsoft.vmcr8tester.com.microsoft.vmcr8tester.util;

import com.microsoft.vmcr8tester.com.microsoft.vmcr8tester.model.BuildItem;

import java.util.List;
import java.util.Map;

public interface RedisCacheInterface {

    public List<BuildItem> getQueue();
}
