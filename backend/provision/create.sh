#!/bin/bash

#az login
az account list -o table
echo "please select subscription: "
read sub
az account set --subscription $sub

echo "solution name: "
read solname
echo "password used: "
read password 


file=$(az ad sp create-for-rbac -n $solname-provision --role="Contributor" --scopes /subscriptions/$sub/resourceGroups/$solname --create-cert | jq -r '.fileWithCertAndPrivateKey')
mv $file cert.pem

tenantId=$(az account show | jq -r '.tenantId')

# Create Dockerfile
cat Dockerfile.example | sed  's/XXX_SOLUTION_NAME_XXX/'$solname'/' >Dockerfile
sed -in 's/XXX_PASSWORD_XXX/'$password'/' Dockerfile
sed -in 's/XXX_TENANTID_XXX/'$tenantId'/' Dockerfile

# Create login script
cat login.sh.example | sed  's/XXX_SOLUTION_NAME_XXX/'$solname-provision'/' >login.sh
sed -in 's/XXX_TENANTID_XXX/'$tenantId'/' login.sh
chmod 750 login.sh


docker build -t $solname-provision:1.0 .

#az login --service-principal -u http://YOURAPPID -p /root/LOCATIONOFYOUR.PEMFILE --tenant TENANTID
