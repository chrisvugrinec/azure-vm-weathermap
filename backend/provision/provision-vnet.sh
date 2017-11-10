#!/bin/bash
for location in $(az account list-locations | jq -r .[].name)
do
  # vnet
  az network vnet create -g vmcr8tester --location $location -n vmcr8tester-vnet-$location
  # subnet
  az network vnet subnet create -g vmcr8tester -n default --vnet-name vmcr8tester-vnet-$location --address-prefix 10.0.0.0/24
done
