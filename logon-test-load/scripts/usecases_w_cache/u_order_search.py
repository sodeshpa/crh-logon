# The Grinder 3.11
# HTTP script recorded by TCPProxy at Jul 28, 2014 1:27:00 PM
import string
from string import Template
import random
import calendar
from datetime import date, datetime, timedelta
from net.grinder.script import Test as StandardTest
def Test(number, description):
  return StandardTest(number+ 140000, description)
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
headers1= \
  [ NVPair('Cache-Control', 'max-age=0'),
    NVPair('Accept', 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8'),]
headers6= \
  [ NVPair('Accept', '*/*'),]

# Instrument requests ###########################################################
request202 = createRequest(Test(202,  'u_order_search_01'), url0)
request301 = createRequest(Test(301,  'u_order_search_02'), url0, headers1)
request501 = createRequest(Test(501,  'u_order_search_03'), url0, headers1)
request601 = createRequest(Test(601,  'u_order_search_04'), url0, headers6)
request701 = createRequest(Test(701,  'u_order_search_05'), url0, headers6)
request801 = createRequest(Test(801,  'u_order_search_06'), url0, headers6)
request901 = createRequest(Test(901,  'u_order_search_07'), url0, headers6)
request1001 = createRequest(Test(1001, 'u_order_search_08'), url0, headers6)
request1101 = createRequest(Test(1101, 'u_order_search_09'), url0, headers6)
request1201 = createRequest(Test(1201, 'u_order_search_10'), url0, headers6)
request1301 = createRequest(Test(1301, 'u_order_search_11'), url0, headers6)


class TestRunner:
  """A TestRunner instance is created for each worker thread."""

  # Login and Load Search Page ##################################################
  def page2(self):
    result = request202.GET('/logon/order/bestellung/BestellungSearchEntry.do', None,
      ( NVPair('Accept', 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8'), ))
    if string.find(result.getText(), "Please log in:") < 1:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Did not find login form!")

    result = request301.POST('/logon/order/j_security_check',
      ( NVPair('j_username', loadtest_username),
        NVPair('j_password', loadtest_password),
        NVPair('submit', 'Login'), ),
      ( NVPair('Content-Type', 'application/x-www-form-urlencoded'), ))
    if result.getStatusCode() != 302:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Login was not successful! sC=%s" % result.getStatusCode())
        grinder.logger.error(result.getText())

    result = request501.GET('/logon/order/bestellung/BestellungSearchEntry.do', None,
      ( NVPair('Accept', 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8'), ))
    if result.getStatusCode() != 200:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Could not load start page! sC=%s" % result.getStatusCode())
        grinder.logger.error(result.getText())

    result = request601.POST('/logon/order/dwr/call/plaincall/OrderAjaxService.searchOrders.dwr',
      '''callCount=1\npage=/logon/order/bestellung/BestellungSearchEntry.do\nhttpSessionId=\nscriptSessionId=FD15A624D8A50ABED23CA4DA1E6DD339765\nc0-scriptName=OrderAjaxService\nc0-methodName=searchOrders\nc0-id=0\nc0-param0=number:0\nc0-param1=number:25\nc0-param2=string:deliveryToDat%20DESC\nc0-e1=string:\nc0-e2=string:\nc0-e3=null:null\nc0-e4=null:null\nc0-e5=null:null\nc0-e6=null:null\nc0-e7=string:17.09.18\nc0-e8=null:null\nc0-e9=string:1%2C2%2C3%2C4%2C5%2C6%2C7%2C8%2C9%2C10%2C11%2C12\nc0-e10=string:OUT%2CIUD%2CIUP%2CINB%2CREP%2COUTR%2CIUDR%2CIUPR%2CINBR\nc0-e11=null:null\nc0-e12=null:null\nc0-e13=null:null\nc0-e14=string:\nc0-e15=string:1%2C2%2C3%2C4%2C5%2C6%2C7%2C8%2C9%2C10%2C11%2C18%2C12%2C13%2C14%2C15%2C16%2C17\nc0-e16=string:\nc0-e17=null:null\nc0-e18=null:null\nc0-e19=null:null\nc0-e20=string:\nc0-e21=null:null\nc0-e22=null:null\nc0-e23=null:null\nc0-param3=Object_Object:{orderNumber:reference:c0-e1, ldsDeliveryNoteNo:reference:c0-e2, soldtoSapBpId:reference:c0-e3, shiptoSapBpId:reference:c0-e4, billtoSapBpId:reference:c0-e5, sapMaterialId:reference:c0-e6, deliveryFromDat:reference:c0-e7, deliveryToDat:reference:c0-e8, deliveryTypeCdList:reference:c0-e9, destinationCdList:reference:c0-e10, shippingpointId:reference:c0-e11, poolId:reference:c0-e12, execCarrierId:reference:c0-e13, carrierIdList:reference:c0-e14, statusList:reference:c0-e15, creationTypeList:reference:c0-e16, sareaId:reference:c0-e17, shiptoSofficeCd:reference:c0-e18, shiptoSgroupCd:reference:c0-e19, plantCdList:reference:c0-e20, vehicleCompositeId:reference:c0-e21, vehicleName:reference:c0-e22, matPackagingType:reference:c0-e23}\nc0-param4=boolean:false\nc0-param5=boolean:false\nc0-param6=boolean:false\nbatchId=0\n''',
      ( NVPair('Content-Type', 'text/plain'), ))
    if result.getStatusCode() != 200:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Could not searchOrders! sC=%s" % result.getStatusCode())
        grinder.logger.error(result.getText())

    result = request801.POST('/logon/order/dwr/call/plaincall/OrderAjaxService.getOrderSearchInformations.dwr',
      '''callCount=1\npage=/logon/order/bestellung/BestellungSearchEntry.do\nhttpSessionId=\nscriptSessionId=FD15A624D8A50ABED23CA4DA1E6DD339765\nc0-scriptName=OrderAjaxService\nc0-methodName=getOrderSearchInformations\nc0-id=0\nc0-param0=number:0\nc0-param1=number:25\nc0-param2=string:\nc0-e1=string:\nc0-e2=string:\nc0-e3=null:null\nc0-e4=null:null\nc0-e5=null:null\nc0-e6=null:null\nc0-e7=string:17.09.18\nc0-e8=null:null\nc0-e9=string:1%2C2%2C3%2C4%2C5%2C6%2C7%2C8%2C9%2C10%2C11%2C12\nc0-e10=string:OUT%2CIUD%2CIUP%2CINB%2CREP%2COUTR%2CIUDR%2CIUPR%2CINBR\nc0-e11=null:null\nc0-e12=null:null\nc0-e13=null:null\nc0-e14=string:\nc0-e15=string:1%2C2%2C3%2C4%2C5%2C6%2C7%2C8%2C9%2C10%2C11%2C18%2C12%2C13%2C14%2C15%2C16%2C17\nc0-e16=string:\nc0-e17=null:null\nc0-e18=null:null\nc0-e19=null:null\nc0-e20=string:\nc0-e21=null:null\nc0-e22=null:null\nc0-e23=null:null\nc0-param3=Object_Object:{orderNumber:reference:c0-e1, ldsDeliveryNoteNo:reference:c0-e2, soldtoSapBpId:reference:c0-e3, shiptoSapBpId:reference:c0-e4, billtoSapBpId:reference:c0-e5, sapMaterialId:reference:c0-e6, deliveryFromDat:reference:c0-e7, deliveryToDat:reference:c0-e8, deliveryTypeCdList:reference:c0-e9, destinationCdList:reference:c0-e10, shippingpointId:reference:c0-e11, poolId:reference:c0-e12, execCarrierId:reference:c0-e13, carrierIdList:reference:c0-e14, statusList:reference:c0-e15, creationTypeList:reference:c0-e16, sareaId:reference:c0-e17, shiptoSofficeCd:reference:c0-e18, shiptoSgroupCd:reference:c0-e19, plantCdList:reference:c0-e20, vehicleCompositeId:reference:c0-e21, vehicleName:reference:c0-e22, matPackagingType:reference:c0-e23}\nc0-param4=boolean:false\nc0-param5=boolean:false\nbatchId=2\n''',
      ( NVPair('Content-Type', 'text/plain'), ))
    if result.getStatusCode() != 200:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Could not getOrderSearchInformations! sC=%s" % result.getStatusCode())
        grinder.logger.error(result.getText())
    return result

  # Change Date and Search ######################################################
  def page9(self):
    daysFrom = calendar.timegm((datetime.now() + timedelta(days=random.randint(-365, 10))).timetuple())
    daysTo   = calendar.timegm((datetime.now() + timedelta(days=random.randint(11, 365))).timetuple())
    grinder.logger.info("Select orders from (unix timestamp) %s to %s" % (daysFrom, daysTo))

    templ = Template("callCount=1\npage=/logon/order/bestellung/BestellungSearchEntry.do\nhttpSessionId=\nscriptSessionId=FD15A624D8A50ABED23CA4DA1E6DD339765\nc0-scriptName=OrderAjaxService\nc0-methodName=searchOrders\nc0-id=0\nc0-param0=number:25\nc0-param1=number:25\nc0-param2=string:deliveryToDat%20DESC\nc0-e1=string:\nc0-e2=string:\nc0-e3=null:null\nc0-e4=null:null\nc0-e5=null:null\nc0-e6=null:null\nc0-e7=string:17.09.18\nc0-e8=null:null\nc0-e9=string:1%2C2%2C3%2C4%2C5%2C6%2C7%2C8%2C9%2C10%2C11%2C12\nc0-e10=string:OUT%2CIUD%2CIUP%2CINB%2CREP%2COUTR%2CIUDR%2CIUPR%2CINBR\nc0-e11=null:null\nc0-e12=null:null\nc0-e13=null:null\nc0-e14=string:\nc0-e15=string:1%2C2%2C3%2C4%2C5%2C6%2C7%2C8%2C9%2C10%2C11%2C18%2C12%2C13%2C14%2C15%2C16%2C17\nc0-e16=string:\nc0-e17=null:null\nc0-e18=null:null\nc0-e19=null:null\nc0-e20=string:\nc0-e21=null:null\nc0-e22=null:null\nc0-e23=null:null\nc0-param3=Object_Object:{orderNumber:reference:c0-e1, ldsDeliveryNoteNo:reference:c0-e2, soldtoSapBpId:reference:c0-e3, shiptoSapBpId:reference:c0-e4, billtoSapBpId:reference:c0-e5, sapMaterialId:reference:c0-e6, deliveryFromDat:reference:c0-e7, deliveryToDat:reference:c0-e8, deliveryTypeCdList:reference:c0-e9, destinationCdList:reference:c0-e10, shippingpointId:reference:c0-e11, poolId:reference:c0-e12, execCarrierId:reference:c0-e13, carrierIdList:reference:c0-e14, statusList:reference:c0-e15, creationTypeList:reference:c0-e16, sareaId:reference:c0-e17, shiptoSofficeCd:reference:c0-e18, shiptoSgroupCd:reference:c0-e19, plantCdList:reference:c0-e20, vehicleCompositeId:reference:c0-e21, vehicleName:reference:c0-e22, matPackagingType:reference:c0-e23}\nc0-param4=boolean:false\nc0-param5=boolean:false\nc0-param6=boolean:false\nbatchId=3\n")
    templ = templ.substitute(daysFrom=daysFrom, daysTo=daysTo)
    result = request901.POST('/logon/order/dwr/call/plaincall/OrderAjaxService.searchOrders.dwr', templ,
      ( NVPair('Content-Type', 'text/plain'), ))
    if result.getStatusCode() != 200:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Could not searchOrders! sC=%s" % result.getStatusCode())
        grinder.logger.error(result.getText())
    if string.find(result.getText(), "Exception") > 0:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("Found exceptions in order search results!")
        grinder.logger.error(result.getText())
    if string.find(result.getText(), "orderCount:0") > 0:
        grinder.logger.warn("No orders found. Can be ok but if it happens too often -> add more test data")

    templ = Template("callCount=1\npage=/logon/order/bestellung/BestellungSearchEntry.do\nhttpSessionId=\nscriptSessionId=FD15A624D8A50ABED23CA4DA1E6DD339765\nc0-scriptName=OrderAjaxService\nc0-methodName=getOrderSearchInformations\nc0-id=0\nc0-param0=number:25\nc0-param1=number:25\nc0-param2=string:\nc0-e1=string:\nc0-e2=string:\nc0-e3=null:null\nc0-e4=null:null\nc0-e5=null:null\nc0-e6=null:null\nc0-e7=string:17.09.18\nc0-e8=null:null\nc0-e9=string:1%2C2%2C3%2C4%2C5%2C6%2C7%2C8%2C9%2C10%2C11%2C12\nc0-e10=string:OUT%2CIUD%2CIUP%2CINB%2CREP%2COUTR%2CIUDR%2CIUPR%2CINBR\nc0-e11=null:null\nc0-e12=null:null\nc0-e13=null:null\nc0-e14=string:\nc0-e15=string:1%2C2%2C3%2C4%2C5%2C6%2C7%2C8%2C9%2C10%2C11%2C18%2C12%2C13%2C14%2C15%2C16%2C17\nc0-e16=string:\nc0-e17=null:null\nc0-e18=null:null\nc0-e19=null:null\nc0-e20=string:\nc0-e21=null:null\nc0-e22=null:null\nc0-e23=null:null\nc0-param3=Object_Object:{orderNumber:reference:c0-e1, ldsDeliveryNoteNo:reference:c0-e2, soldtoSapBpId:reference:c0-e3, shiptoSapBpId:reference:c0-e4, billtoSapBpId:reference:c0-e5, sapMaterialId:reference:c0-e6, deliveryFromDat:reference:c0-e7, deliveryToDat:reference:c0-e8, deliveryTypeCdList:reference:c0-e9, destinationCdList:reference:c0-e10, shippingpointId:reference:c0-e11, poolId:reference:c0-e12, execCarrierId:reference:c0-e13, carrierIdList:reference:c0-e14, statusList:reference:c0-e15, creationTypeList:reference:c0-e16, sareaId:reference:c0-e17, shiptoSofficeCd:reference:c0-e18, shiptoSgroupCd:reference:c0-e19, plantCdList:reference:c0-e20, vehicleCompositeId:reference:c0-e21, vehicleName:reference:c0-e22, matPackagingType:reference:c0-e23}\nc0-param4=boolean:false\nc0-param5=boolean:false\nbatchId=4\n")
    templ = templ.substitute(daysFrom=daysFrom, daysTo=daysTo)
    result = request1001.POST('/logon/order/dwr/call/plaincall/OrderAjaxService.getOrderSearchInformations.dwr', templ,
      ( NVPair('Content-Type', 'text/plain'), ))
    if result.getStatusCode() != 200:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Could not getOrderSearchInformations! sC=%s" % result.getStatusCode())
        grinder.logger.error(result.getText())
    return result

  # Open Order Details ##########################################################
  def page11(self):
    order_id = str(random.randint(1020, 1100)) # adapt according to available test data. '8549'
    result = request1101.POST('/logon/order/auftrag/OrderDetailEntry.do' +
      '?orderid=' + order_id,
      ( NVPair('showPopup', 'true'), ),
      ( NVPair('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8'), ))
    # review: getText seems always empty, maybe because of test data?
    if result.getStatusCode() != 200:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("ERROR: Could not OrderDetailEntry.do! sC=%s" % result.getStatusCode())
        grinder.logger.error(result.getText())
    return result


# Test Plan / Test Entry Point ##################################################
  def __call__(self):
    """Called for every run performed by the worker thread."""
    self.page2()          # login and default search
    for x in range(num_repetitions):
      self.page9()        # search specific date
      if x % 4 == 0:
        self.page11()     # open order details (a bit less often)


# Instrument page methods #######################################################
Test(200, 'u_order_search_login').record(TestRunner.page2)
Test(900, 'u_order_search_date').record(TestRunner.page9)
Test(1100, 'u_order_search_detail').record(TestRunner.page11)

