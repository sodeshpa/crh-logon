# The Grinder 3.11
# @Testspec: https://aww.adnovum.ch/jira/browse/LOGON-8554
import string
from string import Template
import random
from net.grinder.script import Test as StandardTest
def Test(number, description):
  return StandardTest(number+ 100000, description)
from net.grinder.script.Grinder import grinder
from net.grinder.plugin.http import HTTPPluginControl, HTTPRequest
from HTTPClient import NVPair
from scripts.base_config import url0, loadtest_username, loadtest_password, num_repetitions
connectionDefaults = HTTPPluginControl.getConnectionDefaults()
connectionDefaults.setProxyServer("proxy.adnovum.ch",3128)
# be able to read getText from response that would be compressed
connectionDefaults.useContentEncoding = 1
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
    NVPair('Accept-Encoding', 'gzip, deflate,sdch'), ]
headers0= \
  [ NVPair('Cache-Control', 'max-age=0'),
    NVPair('Accept', 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8'),]
headers1= \
  [ NVPair('Accept', 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8'),]
headers6= \
  [ NVPair('Accept', 'image/png,image/*;q=0.8,*/*;q=0.5'), ]

# Instrument requests ###########################################################
request101 = createRequest(Test(101, 'u_call_order_01'), url0)
request102 = createRequest(Test(102, 'u_call_order_02'), url0, headers0)
request301 = createRequest(Test(105, 'u_call_order_05'), url0, headers1)
request401 = createRequest(Test(107, 'u_call_order_07'), url0, headers1)
request501 = createRequest(Test(108, 'u_call_order_08'), url0, headers1)
request601 = createRequest(Test(109, 'u_call_order_09'), url0, headers6)
request701 = createRequest(Test(110, 'u_call_order_10'), url0, headers1)


class TestRunner:
  """A TestRunner instance is created for each worker thread."""

  # Login and Load Search Page ##################################################
  def login(self):
    result = request101.GET('/logon/order/bestellung/BestellungShipToEntry.do', None,
      ( NVPair('Accept', 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8'), ))
    if string.find(result.getText(), "Please log in:") < 1:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Did not find login form!")

    result = request102.POST('/logon/order/j_security_check',
      ( NVPair('j_username', loadtest_username),
        NVPair('j_password', loadtest_password),
        NVPair('submit', 'Login'), ),
      ( NVPair('Content-Type', 'application/x-www-form-urlencoded'), ))
    if result.getStatusCode() != 302:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Login was not successful! sC=%s" % result.getStatusCode())
        grinder.logger.error(result.getText())

  def shiptoSearch(self):
    result = request301.GET('/logon/order/bestellung/BestellungShipToEntry.do', None,
      ( NVPair('Accept', 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8'), ))
    if result.getStatusCode() != 200:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Could not open ShipToEntry screen! sC=%s" % result.getStatusCode())
        grinder.logger.error(result.getText())
    if string.find(result.getText(), "Ship To:") < 1:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Did not find login form!")

  def page4(self):
    searchString = random.choice(["adn", "zur", "ber"]) # PARAMETRIZABLE
    templ = Template("callCount=1\npage=/logon/order/bestellung/BestellungShipToEntry.do\nhttpSessionId=C95B48CF8F9DF7958D5C8FF164815A5F\nscriptSessionId=52EE080802A11EFC290FD23FD8022929695\nc0-scriptName=OrderAjaxService\nc0-methodName=fetchSoldtoShiptoBestList\nc0-id=0\nc0-param0=number:0\nc0-param1=number:20\nc0-param2=string:\nc0-param3=string:${searchString}\nc0-param4=boolean:false\nc0-param5=boolean:false\nbatchId=0\n")
    templ = templ.substitute(searchString=searchString)
    result = request401.POST('/logon/order/dwr/call/plaincall/OrderAjaxService.fetchSoldtoShiptoBestList.dwr', templ,
      ( NVPair('Content-Type', 'text/plain; charset=UTF-8'), ))
    if result.getStatusCode() != 200:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Could not fetch SoltoShiptoBestList! sC=%s" % result.getStatusCode())
        grinder.logger.error(result.getText())
    if string.find(result.getText(), searchString) < 1:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Missing test data, could not find '%s'! sC=%s" % (searchString, result.getStatusCode()))
        grinder.logger.error(result.getText())

  def page5(self):
    result = request501.POST('/logon/order/bestellung/BestellungShipTo.do',
      ( NVPair('shiptoSapId', 'ADN1209206'), # PARAMETRIZABLE
        NVPair('soldtoSapId', 'ADN800119'),  # PARAMETRIZABLE
        NVPair('searchtext', '11'), ),       # PARAMETRIZABLE
      ( NVPair('Content-Type', 'application/x-www-form-urlencoded'), ))
    if result.getStatusCode() != 200:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Could not open BestellungShipTo screen! sC=%s" % result.getStatusCode())
        grinder.logger.error(result.getText())
    if string.find(result.getText(), "Contact Person") < 1:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Could not find contact perso field in detail page! sC=%s" % result.getStatusCode())
        grinder.logger.error(result.getText())

  def page6(self):
    self.token__dc = \
      ''
    self.token_width = \
      '396'
    self.token_height = \
      '216'
    result = request601.GET('/logon/order/bestellungChart.servlet' +
      '?_dc=' + self.token__dc + '&width=' +
      self.token_width + '&height=' + self.token_height)
    if result.getStatusCode() != 200:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Could not request bestellungChart.servlet png image! sC=%s" % result.getStatusCode())
        grinder.logger.error(result.getText())
    return result

  def logout(self):
    result = request701.GET('/logon/order/Logout.do')
    if result.getStatusCode() != 200:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Error during logout! sC=%s" % result.getStatusCode())
        grinder.logger.error(result.getText())
    return result


# Test Plan / Test Entry Point ##################################################
  def __call__(self):
    """Called for every run performed by the worker thread."""
    self.login()
    for x in range(num_repetitions):
      self.shiptoSearch()
      self.page4()      # POST OrderAjaxService.fetchSoldtoShiptoBestList.dwr (request 401)
      self.page5()      # POST BestellungShipTo.do (requests 501-504)
      self.page6()      # GET bestellungChart.servlet (request 601)
    self.logout()      # GET Logout.do

# Instrument page methods #######################################################
Test(200, 'u_call_order_p02').record(TestRunner.login)
Test(300, 'u_call_order_p03').record(TestRunner.shiptoSearch)
Test(400, 'u_call_order_p04').record(TestRunner.page4)
Test(500, 'u_call_order_p05').record(TestRunner.page5)
Test(600, 'u_call_order_p06').record(TestRunner.page6)
Test(700, 'u_call_order_p07').record(TestRunner.logout)