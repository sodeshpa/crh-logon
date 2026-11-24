from net.grinder.script.Grinder import grinder
import java.lang.System, java.util.Random

# define the weights for every script
g_Weights = {
    'U_SCHEDULING':       0,#2,
    'U_WEB_ORDER':        0,#1,    
    'U_MONITOR':          0,#2,
    'U_ORDER_SEARCH':     1,#1,#3,
    'U_DISPO':            1,#20,#3,
    'U_CALL_ORDER':       0,#0,
    'U_DISPOKARTE':       0,#0,
    'U_EXTORDER_SEARCH':  0,#1,
    'U_SHIPPING':         0,#0,

}

# define the scrit names which should be executed
scripts = ["u_call_order",
           "u_dispokarte",
           "u_extorder_search",
           "u_monitor",
           "u_order_search",
           "u_dispo",
           "u_scheduling",
           "u_shipping",
           "u_web_order"]

# Ensure that all modules are imported and initialized correctly
for script in scripts: exec("from scripts.usecases_w_cache import %s" % script)

def weightAccumulator(i_dict):
    keyList = i_dict.keys()
    keyList.sort()  # sorting is optional - order coming-in doesn't matter, but determinism is kinda cool
    listAcc = []
    weightAcc = 0
    for key in keyList:
        weightAcc += i_dict[key]
        listAcc.append((key, weightAcc))
    return (listAcc, weightAcc)  # order going-out does matter - hence "listAcc" instead of "dictAcc"

g_WeightsAcc, g_WeightsAccMax = weightAccumulator(g_Weights)
g_WeightsAccLen, g_WeightsAccMax_1 = len(g_WeightsAcc), g_WeightsAccMax-1


g_rng = java.util.Random(java.lang.System.currentTimeMillis())

def randNum(i_min, i_max):
    assert i_min <= i_max
    range = i_max - i_min + 1  # re-purposing "range" is legal in Python
    assert range <= 0x7fffffff  # because we're using java.util.Random
    randnum = i_min + g_rng.nextInt(range)
    assert i_min <= randnum <= i_max
    return randnum

# create a testrunner from the the module
def createTestRunner(module):
    exec ("x = %s.TestRunner()" % module)
    return x

class TestRunner:
    def __call__(self):
        opNum = randNum(0, g_WeightsAccMax_1)
        opType = None  # flag for assertion below
        for i in range(g_WeightsAccLen):
            if opNum < g_WeightsAcc[i][1]:
                opType = g_WeightsAcc[i][0]
                break
        assert opType in g_Weights.keys()

        if   opType=='U_CALL_ORDER':      self.testRunner = createTestRunner("u_call_order")
        elif opType=='U_DISPOKARTE':      self.testRunner = createTestRunner("u_dispokarte")
        elif opType=='U_EXTORDER_SEARCH': self.testRunner = createTestRunner("u_extorder_search")
        elif opType=='U_MONITOR':         self.testRunner = createTestRunner("u_monitor")
        elif opType=='U_ORDER_SEARCH':    self.testRunner = createTestRunner("u_order_search")
        elif opType=='U_DISPO':           self.testRunner = createTestRunner("u_dispo")
        elif opType=='U_SCHEDULING':      self.testRunner = createTestRunner("u_scheduling")
        elif opType=='U_SHIPPING':        self.testRunner = createTestRunner("u_shipping")
        elif opType=='U_WEB_ORDER':       self.testRunner = createTestRunner("u_web_order")
        else : assert False
        self.testRunner()



