# Azure-vm-weathermap

## What
This applications' backend runs on a managed kubernetes cluster (Azure AKS) and the frontend is available as Serverless instance (Azure Container Instance). All images are stored in a private registry (Azure Container Registry)
It uses Azure Paas solutions like: Keyvault: for storing secrets, CosmosDB for storing results, Redis Cache for temp storage.
Frontend is a java Springboot app which you can only access when Authentication on your configured Azure AD tenant and backend is simple Python and shell code.

## How

### pre requisites
Pre requisites are:
* git client; repo client (https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
* docker; container tool (https://docs.docker.com/install/)
* az-cli; cli for azure  (https://docs.microsoft.com/en-us/cli/azure/install-azure-cli?view=azure-cli-latest)
* kubectl; cli for kubernetes cluster (https://kubernetes.io/docs/tasks/tools/install-kubectl/)
* helm; package manager kubernetes (https://github.com/kubernetes/helm/blob/master/docs/install.md)
* draft; tool for dev on kubernetes/azure (https://github.com/Azure/draft/blob/master/docs/install.md) 

### steps
* git clone https://github.com/chrisvugrinec/azure-vm-weathermap.git
* 
