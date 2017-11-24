# azure-vm-weathermap
This applications' backend runs on a managed kubernetes cluster (Azure AKS) and the frontend is available as Serverless instance (Azure Container Instance). All images are stored in a private registry (Azure Container Registry)
It uses Azure Paas solutions like: Keyvault: for storing secrets, CosmosDB for storing results, Redis Cache for temp storage.
Frontend is a java Springboot app which you can only access when Authentication on your configured Azure AD tenant and backend is simple Python and shell code.

