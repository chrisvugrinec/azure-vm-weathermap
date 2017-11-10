#!/bin/bash
for location in $(az account list-locations | jq -r .[].name)
do
  counter=0
  for machine in $(az vm list-skus -l $location --query [].name | grep Standard_DS2 | grep -v Promo)
  do
      counter=$((counter+1))
      machineX=$(echo $machine | sed 's/"//g' | sed 's/,//g')
      echo $location-$counter $location $machineX
      python provision.py $location-$counter "$location $machineX"
  done
done
