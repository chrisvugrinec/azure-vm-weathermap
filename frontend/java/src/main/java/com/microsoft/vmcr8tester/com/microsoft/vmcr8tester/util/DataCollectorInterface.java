package com.microsoft.vmcr8tester.com.microsoft.vmcr8tester.util;

import com.microsoft.vmcr8tester.com.microsoft.vmcr8tester.model.RegionResults;

import java.util.List;

public interface DataCollectorInterface {
    public String getTotalMachinesBuild();
    public String getTotalMachinesBuildToday();
    public List<RegionResults> getMachineResults();
}
