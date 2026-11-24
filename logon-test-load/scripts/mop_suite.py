# coding=utf8

from net.grinder.script.Grinder import grinder
import java.lang.System, java.util.Random
from dbaccess import queryColumn
from fileaccess import readTestData
import random
from scripts.base_config import use_database

# define the scrit names which should be executed
scripts = ["u_mop"]

PDA_QUERY = "select pda.pda_device_no from tlgnc_pda_device pda where pda.CARRIER_ID = -15 and pda.active_flag = 1 order by pda_device_no asc"
TRUCK_QUERY = ("select distinct truck.truck_plate_no,usr.pda_user_no from tlgnu_user usr " +
	"inner join tlgnc_driver driver on (usr.user_id = driver.user_id) " +
	"inner join tlgnc_truck truck on (truck.truck_id = driver.current_truck_id) " +
	"where (usr.pda_pin like '1234' or usr.pda_pin like 'SHA-1:cRDtpNCeBiql5KOQsKVyrA0sAiA=') and usr.EXT_LOGIN like 'V800040-person%' order by truck.truck_plate_no asc")
USER_QUERY = ("select distinct usr.pda_user_no,truck.truck_plate_no from tlgnu_user usr " +
	"inner join tlgnc_driver driver on (usr.user_id = driver.user_id) " +
	"inner join tlgnc_truck truck on (truck.truck_id = driver.current_truck_id) " +
	"where (usr.pda_pin like '1234' or usr.pda_pin like 'SHA-1:cRDtpNCeBiql5KOQsKVyrA0sAiA=') and usr.EXT_LOGIN like 'V800040-person%' order by truck.truck_plate_no asc")

pdas = []
users = []
trucks = []

if use_database:
	pdas = queryColumn(PDA_QUERY)
	users = queryColumn(USER_QUERY)
	trucks = queryColumn(TRUCK_QUERY)
else:
	trucks = readTestData('mop/trucks.txt')
	users = readTestData('mop/users.txt')
	pdas = readTestData('mop/pdas.txt')

# Ensure that all modules are imported and initialized correctly
for script in scripts: exec("from scripts.usecases_w_cache import %s" % script)

# create a testrunner from the the module
def createTestRunner(module):
    exec ("x = %s.TestRunner()" % module)
    return x

class TestRunner:
    def __call__(self):
        self.testRunner = createTestRunner("u_mop")
        self.testRunner()
