package com.microsoft.vmcr8tester.com.microsoft.vmcr8tester.util;


public class CosmoDBDataCollectService extends CosmoDBDataCollectImpl {

    public CosmoDBDataCollectService(String cosmoDBserviceEndpoint, String cosmoDBmasterkey){
        super(cosmoDBserviceEndpoint,cosmoDBmasterkey);
    }


    @Override
    public String getTotalInQueue() {
        return null;
    }
}
