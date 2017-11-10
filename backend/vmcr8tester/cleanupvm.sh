#!/bin/bash
machine=$1
location=$2
cleanupvm=$(echo $machine | sed 's/_//g' | awk ' { print $0 "-" "'"$location"'" }')
echo "cleaning up machine..."$cleanupvm
# Machines:
for machine in $(az vm list -g vmcr8tester | jq -r '.[].name' | grep $cleanupvm); do az vm delete -n $machine -g vmcr8tester -y; done
# Disks
for disk in $(az disk list -g vmcr8tester | jq -r '.[].name' | grep $cleanupvm); do az disk delete -n $disk -g vmcr8tester -y; done
# Nics
for nic in $(az network nic list -g vmcr8tester | jq -r '.[].name' | grep $cleanupvm); do az network nic delete -n $nic -g vmcr8tester; done
