import redis
import sys
import os
from azure.common.credentials import ServicePrincipalCredentials
from azure.mgmt.keyvault import KeyVaultManagementClient
from azure.keyvault import KeyVaultClient,KeyVaultId

credentials = ServicePrincipalCredentials(
    client_id=os.getenv('AZURE_USER'),
    secret=os.getenv('AZURE_PWD'),
    tenant=os.getenv('AZURE_TENANT')
)

client = KeyVaultClient(
    credentials
)
KEYVAULT='https://vmcr8tester.vault.azure.net'
secret_bundle = client.set_secret(KEYVAULT, 'dummy', '')
secret_id = KeyVaultId.parse_secret_id(secret_bundle.id)
redis_secret=client.get_secret(KEYVAULT, 'vmcr8tester-redis-pwd',secret_id.version_none).value

key = sys.argv[1]
value = sys.argv[2]

r = redis.StrictRedis(host='vmcr8tester.redis.cache.windows.net', port=6380, db=0, password=redis_secret, ssl=True)
r.set(key, value)
