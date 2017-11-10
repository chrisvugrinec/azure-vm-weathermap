#!/bin/bash

echo "cleaning up..."
# Machines:
for machine in $(az vm list -g vmcr8tester | jq -r '.[].name'); do az vm delete -n $machine -g vmcr8tester -y; done
# Disks
for disk in $(az disk list -g vmcr8tester | jq -r '.[].name'); do az disk delete -n $disk -g vmcr8tester -y; done
# Nics
for nic in $(az network nic list -g vmcr8tester | jq -r '.[].name'); do az network nic delete -n $nic -g vmcr8tester; done
