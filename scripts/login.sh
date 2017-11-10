#!/bin/bash
# for eg.  create your serviceprincipal with login with this command:
# az ad sp create-for-rbac -n vmcr8tester --role="Contributor" --scopes=$id --create-cert
# determine the id of your resourcegroup with this command:
# id=$(az group show -n vmcr8tester --query id | sed 's/"//g')

az login --service-principal -u http://YOURAPPID -p /root/LOCATIONOFYOUR.PEMFILE --tenant TENANTID
