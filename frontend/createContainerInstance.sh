#!/bin/bash
echo "name of your image:"
read image
echo "name of your solution: "
read solname
az container create --name azure-vm-weathermap --image  $image  --resource-group $solname --ip-address public  --port 8080
