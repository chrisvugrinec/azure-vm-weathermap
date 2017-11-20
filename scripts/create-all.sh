#!/bin/bash

#az login
az account list -o table
echo "please select subscription: "
read sub
az account set --subscription $sub

echo "solution name: "
read solname
echo "location: "
read location
echo "password for user: http://$solname"
read password

# Create the required resourcegroups       
az group create -n $solname -l $location   
az group create -n $solname-k8 -l $location

az ad sp create-for-rbac -n $solname --role="Contributor" --scopes=/subscriptions/$sub/resourceGroups/$solname --password=$password
az role assignment create --scope /subscriptions/$sub/resourceGroups/$solname --role Contributor --assignee http://$solname
az role assignment create --scope /subscriptions/$sub/resourceGroups/$solname-k8 --role Contributor --assignee http://$solname

echo "where you able to create the user? Y for yes"
read usercreated

if [[ $usercreated != "Y" ]]
then
   exit 1
fi

# Create container registry
az acr create -n $solname -g $solname --admin-enabled true --sku Basic

# Create Redis Cache
az redis create -g $solname -n $solname --sku Basic -l $location  --vm-size c1

# Create Cosmos DB
az cosmosdb create -g $solname -n $solname

# Create keyvault
az keyvault create -n $solname -g $solname

# Create Managed Kubernetes
az aks create -g $solname-k8 -n $solname --service-principal http://$solname --client-secret $password

# Creating vnets
for location in $(az account list-locations | jq -r .[].name)                                                                                                                 
do                                                                                                                                                                            
  # vnet                                                                                                                                                                      
  az network vnet create -g $solname --location $location -n $solname-vnet-$location                                                                                    
  # subnet                                                                                                                                                                    
  az network vnet subnet create -g $solname -n default --vnet-name $solname-vnet-$location --address-prefix 10.0.0.0/24                                                 
done                                                                                                                                                                          

# Create container instance for presentation
#az container create --name azure-vm-weathermap --image  vmcr8tester.azurecr.io/weathermap:1.2 --resource-group vmcr8tester --ip-address public  --port 8080
