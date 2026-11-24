# The Grinder 3.11
# HTTP script recorded by TCPProxy at Jul 28, 2014 1:27:00 PM
import string
import random
from datetime import date, datetime, timedelta
from net.grinder.script import Test as StandardTest
def Test(number, description):
  return StandardTest(number+ 150000, description)
from net.grinder.script.Grinder import grinder
from net.grinder.plugin.http import HTTPPluginControl, HTTPRequest
from HTTPClient import NVPair
from scripts.base_config import url0, loadtest_username, loadtest_password, num_repetitions
connectionDefaults = HTTPPluginControl.getConnectionDefaults()
httpUtilities = HTTPPluginControl.getHTTPUtilities()
def createRequest(test, url, headers=None):
    request = HTTPRequest(url=url)
    if headers: request.headers=headers
    test.record(request, HTTPRequest.getHttpMethodFilter())
    return request

# Prepare HTTP Headers ##########################################################
connectionDefaults.defaultHeaders = \
  [ NVPair('User-Agent', 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36'),
    NVPair('Accept-Language', 'en-US,en;q=0.8'),
    NVPair('Accept-Encoding', 'gzip,deflate,sdch'), ]
headers3= \
  [ NVPair('Cache-Control', 'max-age=0'),
    NVPair('Accept', 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8'),]
headers6= \
  [ NVPair('Accept', '*/*'), ]

# Instrument requests ###########################################################
request101 = createRequest(Test(101,   'u_dispo_01'), url0)
request601 = createRequest(Test(601,   'u_dispo_02'), url0, headers3)
request602 = createRequest(Test(602,   'u_dispo_03'), url0, headers3)
request701 = createRequest(Test(701,   'u_dispo_04'), url0, headers6)
request801 = createRequest(Test(801,   'u_dispo_05'), url0, headers6)
request901 = createRequest(Test(901,   'u_dispo_06'), url0, headers6)
request1001 = createRequest(Test(1001, 'u_dispo_07'), url0, headers6)
request1101 = createRequest(Test(1101, 'u_dispo_08'), url0, headers6)
request1201 = createRequest(Test(1201, 'u_dispo_09'), url0, headers6)
request1301 = createRequest(Test(1301, 'u_dispo_10'), url0, headers6)
request1401 = createRequest(Test(1401, 'u_dispo_11'), url0, headers6)
request1501 = createRequest(Test(1501, 'u_dispo_12'), url0, headers6)
request1601 = createRequest(Test(1601, 'u_dispo_13'), url0, headers6)
request1701 = createRequest(Test(1701, 'u_dispo_14'), url0, headers6)


class TestRunner:
  """A TestRunner instance is created for each worker thread."""

  # Login and Load Dispo Page ###################################################
  def page1(self): # SessionID is held in Cookie and flushed after each test run
    result = request101.GET('/logon/order/dispo/DispoEntry.do', None,
      ( NVPair('Cache-Control', 'max-age=0'),
        NVPair('Accept', 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8'), ))
    if string.find(result.getText(), "Please log in:") < 1:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Did not find login form!")

    result = request601.POST('/logon/order/dispo/j_security_check',
      ( NVPair('j_username', loadtest_username),
        NVPair('j_password', loadtest_password),
        NVPair('submit', 'Login'), ),
      ( NVPair('Content-Type', 'application/x-www-form-urlencoded'), ))
    if result.getStatusCode() != 302:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Login was not successful! sC=%s" % result.getStatusCode())
        grinder.logger.error(result.getText())

    result = request602.GET('/logon/order/dispo/DispoEntry.do')
    if result.getStatusCode() != 200:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Could not load start page! sC=%s" % result.getStatusCode())
        grinder.logger.error(result.getText())

    result = request701.POST('/logon/order/dwr/call/plaincall/DispoAjaxService.fetchCurrentCarriers.dwr',
      '''callCount=1\npage=/logon/order/dispo/DispoEntry.do\nhttpSessionId=\nscriptSessionId=\nc0-scriptName=DispoAjaxService\nc0-methodName=fetchCurrentCarriers\nc0-id=0\nc0-param0=boolean:false\nbatchId=0\n''',
      ( NVPair('Content-Type', 'text/plain'), ))
    if result.getStatusCode() != 200:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Could not fetchCurrentCarriers! sC=%s" % result.getStatusCode())
        grinder.logger.error(result.getText())

    """POST DispoAjaxService.getTruckAndAssignmentListWithLocalEJB.dwr (request 801)."""
    result = request801.POST('/logon/order/dwr/call/plaincall/DispoAjaxService.getTruckAndAssignmentListWithLocalEJB.dwr',
      '''callCount=1\npage=/logon/order/dispo/DispoEntry.do\nhttpSessionId=\nscriptSessionId=\nc0-scriptName=DispoAjaxService\nc0-methodName=getTruckAndAssignmentListWithLocalEJB\nc0-id=0\nc0-param0=null:null\nc0-param1=boolean:false\nbatchId=1\n''',
      ( NVPair('Content-Type', 'text/plain'), ))
    if result.getStatusCode() != 200:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Could not getTruckAndAssignmentList! sC=%s" % result.getStatusCode())
        grinder.logger.error(result.getText())

    result = request901.POST('/logon/order/dwr/call/plaincall/DispoAjaxService.getOrderListWithLocalEJB.dwr',
      '''callCount=1\npage=/logon/order/dispo/DispoEntry.do\nhttpSessionId=\nscriptSessionId=\nc0-scriptName=DispoAjaxService\nc0-methodName=getOrderListWithLocalEJB\nc0-id=0\nc0-param0=boolean:false\nbatchId=2\n''',
      ( NVPair('Content-Type', 'text/plain'), ))

    result = request1001.POST('/logon/order/dwr/call/plaincall/DispoAjaxService.getTrspCategoriesForPackageTypes.dwr',
      '''callCount=1\npage=/logon/order/dispo/DispoEntry.do\nhttpSessionId=\nscriptSessionId=\nc0-scriptName=DispoAjaxService\nc0-methodName=getTrspCategoriesForPackageTypes\nc0-id=0\nbatchId=3\n''',
      ( NVPair('Content-Type', 'text/plain'), ))
    if result.getStatusCode() != 200:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Could not getTrspCategories! sC=%s" % result.getStatusCode())
        grinder.logger.error(result.getText())
    return result

  def page11(self):
    day = (datetime.now()).strftime("%d.%m.%Y")
    result = request1101.POST('/logon/order/dwr/call/plaincall/DispoAjaxService.setDispoDate.dwr',
      '''callCount=1\npage=/logon/order/dispo/DispoEntry.do\nhttpSessionId=\nscriptSessionId=\nc0-scriptName=DispoAjaxService\nc0-methodName=setDispoDate\nc0-id=0\nc0-param0=string:%s\nc0-param1=boolean:false\nbatchId=4\n''' % day,
      ( NVPair('Content-Type', 'text/plain'), ))
    if result.getStatusCode() != 200:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Could not setDispoDate! sC=%s" % result.getStatusCode())
        grinder.logger.error(result.getText())

    result = request1201.POST('/logon/order/dwr/call/plaincall/DispoAjaxService.getTruckAndAssignmentListWithLocalEJB.dwr',
      '''callCount=1\npage=/logon/order/dispo/DispoEntry.do\nhttpSessionId=\nscriptSessionId=\nc0-scriptName=DispoAjaxService\nc0-methodName=getTruckAndAssignmentListWithLocalEJB\nc0-id=0\nc0-param0=null:null\nc0-param1=boolean:false\nbatchId=5\n''',
      ( NVPair('Content-Type', 'text/plain'), ))
    if result.getStatusCode() != 200:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Could not getTruckAndAssignmentList2! sC=%s" % result.getStatusCode())
        grinder.logger.error(result.getText())

    result = request1301.POST('/logon/order/dwr/call/plaincall/DispoAjaxService.getOrderListWithLocalEJB.dwr',
      '''callCount=1\npage=/logon/order/dispo/DispoEntry.do\nhttpSessionId=\nscriptSessionId=\nc0-scriptName=DispoAjaxService\nc0-methodName=getOrderListWithLocalEJB\nc0-id=0\nc0-param0=boolean:false\nbatchId=6\n''',
      ( NVPair('Content-Type', 'text/plain'), ))
    if result.getStatusCode() != 200:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Could not getOrderListWithLocalEJB! sC=%s" % result.getStatusCode())
        grinder.logger.error(result.getText())
    return result

  # Change Pool = Refresh Dispo Page ############################################
  def page14(self):
    day = (datetime.now()).strftime("%d.%m.%Y") # random days to challenge the db cache. sync days with provided test data?
    grinder.logger.info("Using day: %s" % day)
    span = str(random.randint(13, 24)) # random time span
    pool = str(random.randint(1, 3)) # random pool number (max 3 on DEV, could be higher for INT/PROD)
    result = request1401.POST('/logon/order/dispo/Dispo.do',
      ( NVPair('setPool', ''),
        NVPair('', 'on'),
        NVPair('stringDate', day), # '21.07.2014'
        NVPair('selectedPool', pool),
        NVPair('timegrid', '4'),
        NVPair('hourSpan', span),
        NVPair('selectedFilterForVisual', '0'), ),
      ( NVPair('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8'), ))
    if result.getStatusCode() != 200:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Could not switch dispo pool! sC=%s" % result.getStatusCode())
        grinder.logger.error(result.getText())

    result = request1501.POST('/logon/order/dwr/call/plaincall/DispoAjaxService.fetchCurrentCarriers.dwr',
      '''callCount=1\npage=/logon/order/dispo/DispoEntry.do\nhttpSessionId=\nscriptSessionId=\nc0-scriptName=DispoAjaxService\nc0-methodName=fetchCurrentCarriers\nc0-id=0\nc0-param0=boolean:false\nbatchId=7\n''',
      ( NVPair('Content-Type', 'text/plain'), ))
    if result.getStatusCode() != 200:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Could not fetchCurrentCarriers! sC=%s" % result.getStatusCode())
        grinder.logger.error(result.getText())

    result = request1601.POST('/logon/order/dwr/call/plaincall/DispoAjaxService.getTruckAndAssignmentListWithLocalEJB.dwr',
      '''callCount=1\npage=/logon/order/dispo/DispoEntry.do\nhttpSessionId=\nscriptSessionId=\nc0-scriptName=DispoAjaxService\nc0-methodName=getTruckAndAssignmentListWithLocalEJB\nc0-id=0\nc0-param0=null:null\nc0-param1=boolean:false\nbatchId=8\n''',
      ( NVPair('Content-Type', 'text/plain'), ))
    if result.getStatusCode() != 200:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Could not getTruckAndAssignmentListWithLocalEJB! sC=%s" % result.getStatusCode())
        grinder.logger.error(result.getText())

    result = request1701.POST('/logon/order/dwr/call/plaincall/DispoAjaxService.getOrderListWithLocalEJB.dwr',
      '''callCount=1\npage=/logon/order/dispo/DispoEntry.do\nhttpSessionId=\nscriptSessionId=\nc0-scriptName=DispoAjaxService\nc0-methodName=getOrderListWithLocalEJB\nc0-id=0\nc0-param0=boolean:false\nbatchId=9\n''',
      ( NVPair('Content-Type', 'text/plain'), ))
    if result.getStatusCode() != 200:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Could not getOrderListWithLocalEJB! sC=%s" % result.getStatusCode())
        grinder.logger.error(result.getText())
    return result


# Test Plan / Test Entry Point ################################################
  def __call__(self):
    """Called for every run performed by the worker thread."""
    self.page1()
    for x in range(num_repetitions):
      self.page11()
      self.page14()


# Instrument page methods #######################################################
Test(100,  'u_dispo_Login').record(TestRunner.page1)
Test(1100, 'u_dispo_SetDate').record(TestRunner.page11)
Test(1400, 'u_dispo_SetPool').record(TestRunner.page14)

