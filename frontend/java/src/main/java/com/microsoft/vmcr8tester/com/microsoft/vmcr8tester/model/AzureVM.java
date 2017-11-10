package com.microsoft.vmcr8tester.com.microsoft.vmcr8tester.model;

import lombok.experimental.Builder;
import lombok.Data;

@Data
@Builder
public class AzureVM {
    private String machine;
    private String id;
    private String region;
    private String timecreated;
    private String result;
    private double time;

}
