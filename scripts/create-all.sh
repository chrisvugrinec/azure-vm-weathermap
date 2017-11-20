echo "solution name: "
read solname
echo "location: "
read location

# Create the required resourcegroups
az group create -n $solname -l $location
az group create -n $solname-k8 -l $location

# Create container registry
az acr create -n $solname -g $solname --admin-enabled true --sku Basic

# Create Redis Cache
az redis create -g $solname -n $solname --sku Basic -l $location  --vm-size c1

# Create Cosmos DB
az cosmosdb create -g $solname -n $solname

# Create keyvault
az keyvault create -n $solname -g $solname


# Create container instance for presentation
#az container create --name azure-vm-weathermap --image  vmcr8tester.azurecr.io/weathermap:1.2 --resource-group vmcr8tester --ip-address public  --port 8080
