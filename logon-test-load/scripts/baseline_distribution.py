from net.grinder.script.Grinder import grinder

# define the scrit names which should be executed
usecases = ["u_call_order",
           "u_dispokarte",
           "u_extorder_search",
           "u_monitor",
           "u_order_search",
           "u_scheduling",
           "u_shipping",
           "u_web_order",]

# Ensure that all modules are imported and initialized correctly
for usecase in usecases: exec("from scripts.usecases_w_cache import %s" % usecase)

# create a testrunner from the the module
def createTestRunner(module):
    exec ("x = %s.TestRunner()" % module)
    return x

class TestRunner:
    def __call__(self):
        for usecase in usecases:
            self.testRunner = createTestRunner(usecase)
            self.testRunner()