#---------dynamic settings from build properties--------
url0 = 'http://localhost:15080'

database = ''
db_user = 'ulgn02'
db_pass = 'ulgn02'
db_schema = 'ulgn01'

#---------static settings--------

# adnovum int
# url0 = 'https://logonebm2-int.zh.adnovum.ch'

# local dev
# url0 = 'http://localhost:8080/logon/order/'

# Crh LQ0 Ext 1 (Old)
# url0 = 'http://emea-dcnq-s241.ea.crh.net

environment='ltr'
use_database=True

proxy_host=""
#proxy_host="proxy.adnovum.ch"
proxy_port=3128

loadtest_username = 'loadtest1'
loadtest_password = 'Loadtest%1'

# Inner-test loop repetitions (1 test run = one login + n repetitions. Reasoning: Login is slow and happens rarely)
num_repetitions = 5
