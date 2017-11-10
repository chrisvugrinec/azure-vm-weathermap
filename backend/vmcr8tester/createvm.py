import redis
import nltk
import json
import subprocess
import sys
import os
import pydocumentdb;
import pydocumentdb.document_client as document_client
from timeit import Timer
import time
from datetime import datetime
nltk.download('punkt')
from azure.common.credentials import ServicePrincipalCredentials
from azure.mgmt.keyvault import KeyVaultManagementClient
from azure.keyvault import KeyVaultClient,KeyVaultId

credentials = ServicePrincipalCredentials(
    client_id=os.getenv('AZURE_USER'),
    secret=os.getenv('AZURE_PWD'),
    tenant=os.getenv('AZURE_TENANT')
)

client = KeyVaultClient(
    credentials
)
KEYVAULT='https://vmcr8tester.vault.azure.net'
secret_bundle = client.set_secret(KEYVAULT, 'dummy', '')
secret_id = KeyVaultId.parse_secret_id(secret_bundle.id)
redis_secret=client.get_secret(KEYVAULT, 'vmcr8tester-redis-pwd',secret_id.version_none).value
cosmosdb_secret=client.get_secret(KEYVAULT, 'vmc8tester-cosmosdb-pwd',secret_id.version_none).value

config = {                                                                                         
    'ENDPOINT': 'https://vmcr8tester.documents.azure.com',                                         
    'MASTERKEY': cosmosdb_secret,                                                                  
    'DOCUMENTDB_DATABASE': 'db',                                                                   
    'DOCUMENTDB_COLLECTION': 'coll'                                                                
};                                                                                                 
                                                                                                   
                                                                                                   
r = redis.StrictRedis(host='vmcr8tester.redis.cache.windows.net',port=6380, db=0, password=redis_secret, ssl=True)
vmkeys = r.keys()                                                                                                 
                                                                                                                  
# Initialize the Python DocumentDB client                                                                         
client = document_client.DocumentClient(config['ENDPOINT'], {'masterKey': config['MASTERKEY']})                   
database = client.ReadDatabase('dbs/db')                                                                          
collection = client.ReadCollection('dbs/db/colls/azurevms')                                                       
                                                                                                                  
def persistResult():                                                                                              
    client.CreateDocument(collection['_self'],                                                                    
    {                                                                                                             
        'id': createVM.id,                                                                                        
        'machine': createVM.machine,                                                                              
        'timecreated': createVM.timecreated,                                                                      
        'region': createVM.region,                                                                                
        'result': createVM.result,                                                                                
        'time': str(ttime),                                                                                       
    })       

def createVM():                                                                                                   
                                                                                                                  
    createVM.machine = machine                                                                                    
    createVM.region = region                                                                                      
    vnetname = 'vmcr8tester-vnet-'+region                                                                         
    createVM.id = machine + str(time.time())                                                                      
    createVMargs = ("/opt/azure-vmtest/createvm.sh",machine,region,vnetname)                                      
    createVM.result = "unknown"                                                                                   
    createVM.timecreated = "unknown"                                                                              
    popen = subprocess.Popen(createVMargs, stdout=subprocess.PIPE)                                                
    out, err = popen.communicate()                                                                                
    dt = datetime.now()                                                                                           
    formattedTime = '{:%d %m %Y - %H:%M}'.format(dt)                                                              
                                                                                                                  
    # try to parse                                                                                                
    try:                                                                                                          
        decoded_data = out.decode('utf-8')                                                                        
        json_data = json.loads(decoded_data)                                                                      
        createVM.result = json_data['properties']['provisioningState']                                            
        createVM.timecreated = json_data['properties']['timestamp']                                               
    except:                                                                                                       
        print("exception during parsing")                                                                         
        createVM.result = out                                                                                     
        dt = datetime.now()                                                                                       
        createVM.timecreated = '{:%d %m %Y - %H:%M}'.format(dt)                                                   
    popen.wait()                                                                                                  
    return


def deleteVM():                                                                                                   
    deleteVMargs=("/opt/azure-vmtest/cleanupvm.sh",machine,region)                                                
    popen = subprocess.Popen(deleteVMargs, stdout=subprocess.PIPE)                                                
    out, err = popen.communicate()                                                                                
    popen.wait()                                                                                                  
    return                                                                                                        
                                                                                                                  
# get machine from redis cache, and remove from r cache                                                           
# create vm                                                                                                       
# store results in cosmodb                                                                                        
# destroy created vm                                                                                              
                                                                                                                  
for vmkey in vmkeys:                                                                                              
    value = r.get(vmkey).decode('UTF-8')                                                                          
    if value is not None:                                                                                         
        print(value)                                                                                              
        print("delete key: ",vmkey.decode('UTF-8'))                                                               
        r.delete(vmkey)                                                                                           
        if value:                                                                                                 
            tokens = nltk.word_tokenize(value)                                                                    
            print("creating and deleting vm: ",tokens[1]," in location: ",tokens[0])                              
            machine = tokens[1]                                                                                   
            region = tokens[0]                                                                                    
            try:                                                                                                  
                print("creating VM")                                                                              
                tt = Timer(createVM)                                                                              
                ttime = tt.timeit(1)                                                                              
            except:                                                                                               
                print("Error during creating VM")                                                                 
            try:                                                                                                  
                print("persisting result")                                                                        
                persistResult()     
            except:                                                                                               
                print("Error during persisting result")                                                           
            try:                                                                                                  
                print("delete VM")                                                                                
                deleteVM()                                                                                        
            except:                                                                                               
                print("Error during delete VM")  

