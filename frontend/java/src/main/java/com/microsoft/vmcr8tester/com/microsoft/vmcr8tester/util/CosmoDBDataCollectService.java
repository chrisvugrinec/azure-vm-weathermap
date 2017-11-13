package com.microsoft.vmcr8tester.com.microsoft.vmcr8tester.util;

import com.google.gson.Gson;
import com.microsoft.azure.documentdb.*;
import com.microsoft.vmcr8tester.com.microsoft.vmcr8tester.model.AzureVM;
import com.microsoft.vmcr8tester.com.microsoft.vmcr8tester.model.RegionResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CosmoDBDataCollectService implements CosmoDBInterface{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Database databaseCache;
    private String collectionLink = "dbs/db/colls/azurevms";
    private DocumentCollection azurevmsCollectionCache;
    private DocumentClient documentClient;
    private Gson gson = new Gson();
    private FeedOptions fo = new FeedOptions();

    private String RESULT_SUCCESS = "sunny.png";
    private String RESULT_SLOW = "cloudy.png";
    private String RESULT_FAIL = "fail.png";

    //  Within 3 minutes is OK (3 x 60 sec)
    private static int TIME_LIMIT = 180;

    public CosmoDBDataCollectService() {
    }

    @Autowired
    public CosmoDBDataCollectService(String cosmoDBserviceEndpoint, String cosmoDBmasterkey){

        documentClient = new DocumentClient(cosmoDBserviceEndpoint , cosmoDBmasterkey, ConnectionPolicy.GetDefault(), ConsistencyLevel.Session);
        try{
            azurevmsCollectionCache = getAzureVMCollection();
        }catch(Exception ex){
            logger.error("Exception during initialization when creating connection to cosmo DataBase");
        }
    }


    private Database getAzureVMsDB(String dbName) throws Exception {
        if (databaseCache == null) {
            // Get the database if it exists
            List<Database> databaseList = documentClient
                    .queryDatabases("SELECT * FROM root r WHERE r.id='" + dbName + "'", null).getQueryIterable().toList();

            if (databaseList.size() > 0) {
                databaseCache = databaseList.get(0);
            } else {
                logger.error("Exception Cosmo database does not exist");
                throw new Exception("Database "+dbName+" does not exist");
            }
        }
        return databaseCache;
    }


    private DocumentCollection getAzureVMCollection() throws Exception {

        String collectionName = "azurevms";
        String dbName = "db";
        fo.setEnableCrossPartitionQuery(true);
        fo.setEnableScanInQuery(true);


        if (azurevmsCollectionCache == null) {
            // Get the collection if it exists.
            List<DocumentCollection> collectionList = documentClient
                    .queryCollections(
                            getAzureVMsDB(dbName).getSelfLink(), "SELECT * FROM root r WHERE r.id='" + collectionName
                                    + "'", null).getQueryIterable().toList();

            if (collectionList.size() > 0) {
                azurevmsCollectionCache = collectionList.get(0);
            } else {
                logger.error("Exception Cosmo Collection does not exist");
                throw new Exception("Collection "+collectionName+" does not exist");
            }

            PartitionKeyDefinition partitionKeyDef = new PartitionKeyDefinition();
            ArrayList<String> paths = new ArrayList<>();
            paths.add("/id");
            partitionKeyDef.setPaths(paths);
            azurevmsCollectionCache.setPartitionKey(partitionKeyDef);
        }
        return azurevmsCollectionCache;
    }



    public String getTotalMachinesBuild() {
        String result = null;
        String query = "SELECT value count(1) FROM azurevms";
        try{
            Document aDoc = documentClient.queryDocuments(collectionLink, query, fo).getQueryIterable().toList().get(0);
            result = aDoc.get("_aggregate").toString();
        }catch(Exception ex){
            logger.error("Exception while doing query: {0}",query);
        }
        return result;
    }

    private ArrayList<AzureVM> getTodaysAzureVMs(){
        return getTodaysAzureVMs("ALL");
    }

    private ArrayList<AzureVM> getTodaysAzureVMs(String region){
        ArrayList<AzureVM> result = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String query = "SELECT * FROM azurevms avms where startswith(avms.timecreated,'" + dateFormat.format(date) + "')";

        try {
            if (region != "ALL") {
                query = "SELECT * FROM azurevms avms where startswith(avms.timecreated,'" + dateFormat.format(date) + "') and avms.region = '"+region+"'";
            }
            List<Document> documentList = documentClient.queryDocuments(collectionLink, query, fo).getQueryIterable().toList();

            for (Document azurevm : documentList) {
                result.add(gson.fromJson(azurevm.toString(), AzureVM.class));
            }

        }catch(Exception ex){
            logger.error("Exception while doing query: {0}",query);
        }
        return result;
    }


    public List<RegionResults> getMachineResults() {
        ArrayList<AzureVM> azureVMS = getTodaysAzureVMs();
        List<RegionResults> results = new ArrayList<>();
        HashSet<String> regions = new HashSet<>();

        //  Extract all regions in Set (unique)
        for(AzureVM azureVM : azureVMS){
            regions.add(azureVM.getRegion());
        }

        //  Here the results per region are determined
        for(String region : regions) {
            ArrayList<AzureVM> azureVMSperRegion = getTodaysAzureVMs(region);
            int amountOfBuildsToday = azureVMSperRegion.size();
            RegionResults mResult = new RegionResults();
            double buildTimes = 0;


            //  For determining latest result
            TreeSet timeAndResult = new TreeSet();

            for(AzureVM azureVM : azureVMSperRegion){
                timeAndResult.add(azureVM.getTimecreated());
                buildTimes += azureVM.getTime();
            }
            String timeOfLastResult = timeAndResult.last().toString();
            String buildResult = RESULT_FAIL;
            for(AzureVM azureVM : azureVMSperRegion){
                //  The latest buildresult from the region is taken
                //  Rules
                //  If avg buildtime <= 180 == SUNNY
                //  If avg buildtime  > 180 == SLOW
                //  if latest result != SUCCESS ==> FAIL
                double avgBuildTime = buildTimes/amountOfBuildsToday;
                if(avgBuildTime<=TIME_LIMIT)
                    buildResult = RESULT_SUCCESS;
                else
                    buildResult = RESULT_SLOW;
                //  Override result if the latest build contained anything else than Succeeded
                if(     (azureVM.getTimecreated() == timeOfLastResult)  &&
                        !(azureVM.getResult().equalsIgnoreCase("Succeeded"))){
                    buildResult = RESULT_FAIL;
                }
                mResult.setResult(buildResult);

                mResult.setCalculatedtime(avgBuildTime);
                mResult.setRegion(region);



            }
            results.add(mResult);
        }

        return results;
    }


    public String getTotalMachinesBuildToday() {
        String result = null;
        Document aDoc = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String query = "SELECT value count(1) FROM azurevms avms where startswith(avms.timecreated,'"+ dateFormat.format(date) +"')";
        try{
            aDoc = documentClient.queryDocuments(collectionLink, query, fo).getQueryIterable().toList().get(0);
            result = aDoc.get("_aggregate").toString();
        }catch(Exception ex){
            logger.error("Exception while doing query: {0}",query);
        }
        return result;
    }


}
