#!/bin/bash
az group deployment create --name "vmcr8test-"$2-$1 --template-file azurevm.json --parameters '{"vmSize":{"value":'"'$1'"'},"location":{"value":'"'$2'"'}, \
 "existingVirtualNetworkName":{"value":'"'$3'"'}}' -g vmcr8tester
