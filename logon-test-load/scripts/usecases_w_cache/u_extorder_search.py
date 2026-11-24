# The Grinder 3.4
# HTTP script recorded by TCPProxy at Sep 8, 2011 11:39:30 AM

from net.grinder.script import Test as StandardTest

def Test(number, description):
  # Adjust the 10000 to the appropriate offest
  return StandardTest(number+ 80000, description)
  
from net.grinder.script.Grinder import grinder
from net.grinder.plugin.http import HTTPPluginControl, HTTPRequest
from HTTPClient import NVPair
from scripts.base_config import url0, loadtest_username, loadtest_password

connectionDefaults = HTTPPluginControl.getConnectionDefaults()
httpUtilities = HTTPPluginControl.getHTTPUtilities()

# To use a proxy server, uncomment the next line and set the host and port.
# connectionDefaults.setProxyServer("localhost", 8001)

# These definitions at the top level of the file are evaluated once,
# when the worker process is started.

connectionDefaults.defaultHeaders = \
  [ NVPair('Accept-Language', 'en-us,en;q=0.7,de-ch;q=0.3'),
    NVPair('Accept-Charset', 'ISO-8859-1,utf-8;q=0.7,*;q=0.7'),
    NVPair('Accept-Encoding', 'gzip, deflate'),
    NVPair('User-Agent', 'Mozilla/5.0 (X11; Linux i686; rv:6.0) Gecko/20100101 Firefox/6.0'), ]

headers0= \
  [ NVPair('Accept', 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8'), ]

headers1= \
  [ NVPair('Accept', 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8'),
    NVPair('Referer', 'http://localhost:8080/logon/order/OrderEntry.do'), ]

headers2= \
  [ NVPair('Accept', '*/*'),
    NVPair('Referer', 'http://localhost:8080/logon/order/OrderEntry.do'), ]

headers3= \
  [ NVPair('Accept', '*/*'),
    NVPair('Referer', 'http://localhost:8080/logon/order/bestellung/BestellungExternalSearchEntry.do'), ]

headers4= \
  [ NVPair('Accept', 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8'),
    NVPair('Referer', 'http://localhost:8080/logon/order/bestellung/BestellungExternalSearchEntry.do'), ]

# Create an HTTPRequest for each request, then replace the
# reference to the HTTPRequest with an instrumented version.
# You can access the unadorned instance using request101.__target__.
request101 = HTTPRequest(url=url0, headers=headers0)
request101 = Test(101, 'GET /').wrap(request101)

request102 = HTTPRequest(url=url0, headers=headers0)
request102 = Test(102, 'GET OrderEntry.do').wrap(request102)

request201 = HTTPRequest(url=url0, headers=headers1)
request201 = Test(201, 'POST j_security_check').wrap(request201)

request202 = HTTPRequest(url=url0, headers=headers1)
request202 = Test(202, 'GET OrderEntry.do').wrap(request202)

request301 = HTTPRequest(url=url0, headers=headers1)
request301 = Test(301, 'GET BestellungExternalSearchEntry.do').wrap(request301)

request304 = HTTPRequest(url=url0, headers=headers3)
request304 = Test(304, 'GET OrderAjaxService.js').wrap(request304)

request401 = HTTPRequest(url=url0, headers=headers4)
request401 = Test(401, 'POST OrderAjaxService.searchExternalOrders.dwr').wrap(request401)

request501 = HTTPRequest(url=url0, headers=headers4)
request501 = Test(501, 'POST OrderAjaxService.searchExternalOrders.dwr').wrap(request501)

request601 = HTTPRequest(url=url0, headers=headers4)
request601 = Test(601, 'GET BestellungExternalSearchEntry.do').wrap(request601)

# performing logout action
request701 = HTTPRequest(url=url0, headers=headers4)
request701 = Test(701, 'GET Logout.do').wrap(request701)


class TestRunner:
  """A TestRunner instance is created for each worker thread."""

  # A method for each recorded page.
  def page1(self):
    """GET / (requests 101-102)."""
    # Expecting 302 'Moved Temporarily'
    result = request101.GET('/')
    request102.GET('/logon/order/OrderEntry.do')
    return result

  def page2(self):
    """POST j_security_check (requests 201-204)."""
    # Expecting 302 'Moved Temporarily'
    result = request201.POST('/logon/order/j_security_check',
      ( NVPair('j_username', loadtest_username),
        NVPair('j_password', loadtest_password),
        NVPair('submit', 'Anmelden'), ),
      ( NVPair('Content-Type', 'application/x-www-form-urlencoded'), ))
    request202.GET('/logon/order/OrderEntry.do')
    return result

  def page3(self):
    """GET BestellungExternalSearchEntry.do (requests 301-304)."""
    result = request301.GET('/logon/order/bestellung/BestellungExternalSearchEntry.do')
    request304.GET('/logon/order/dwr/interface/OrderAjaxService.js')
    return result

  def page4(self):
    """POST OrderAjaxService.searchExternalOrders.dwr (request 401)."""
    result = request401.POST('/logon/order/dwr/call/plaincall/OrderAjaxService.searchExternalOrders.dwr',
			'''callCount=1\npage=/logon/order/bestellung/BestellungExternalSearchEntry.do\nhttpSessionId=\nscriptSessionId=885C5CEBFB66A65C6C5D45D5F7FF75F9157\nc0-scriptName=OrderAjaxService\nc0-methodName=searchExternalOrders\nc0-id=0\nc0-param0=number:0\nc0-param1=number:25\nc0-param2=string:deliveryToDat%20DESC\nc0-e1=string:\nc0-e2=string:\nc0-e3=string:16.11.18\nc0-e4=null:null\nc0-e5=string:1%2C2%2C3%2C4%2C5%2C6%2C7%2C8%2C9%2C10%2C11%2C18%2C12%2C13%2C14%2C15%2C16\nc0-e6=null:null\nc0-e7=null:null\nc0-param3=Object_Object:{extOrderNumber:reference:c0-e1, extDeliveryNote:reference:c0-e2, deliveryFromDat:reference:c0-e3, deliveryToDat:reference:c0-e4, statusList:reference:c0-e5, vehicleCompositeId:reference:c0-e6, vehicleName:reference:c0-e7}\nc0-param4=boolean:false\nc0-param5=boolean:false\nbatchId=1\n''',
      ( NVPair('Content-Type', 'text/plain; charset=UTF-8'), ))
    return result

  def page5(self):
    """POST OrderAjaxService.searchExternalOrders.dwr (request 501)."""
    result = request501.POST('/logon/order/dwr/call/plaincall/OrderAjaxService.searchExternalOrders.dwr',
			'''callCount=1\npage=/logon/order/bestellung/BestellungExternalSearchEntry.do\nhttpSessionId=\nscriptSessionId=885C5CEBFB66A65C6C5D45D5F7FF75F9157\nc0-scriptName=OrderAjaxService\nc0-methodName=searchExternalOrders\nc0-id=0\nc0-param0=number:0\nc0-param1=number:25\nc0-param2=string:deliveryToDat%20DESC\nc0-e1=string:\nc0-e2=string:\nc0-e3=string:16.11.18\nc0-e4=null:null\nc0-e5=string:1%2C2%2C3%2C4%2C5%2C6%2C7%2C8%2C9%2C10%2C11%2C18%2C12%2C13%2C14%2C15%2C16\nc0-e6=null:null\nc0-e7=null:null\nc0-param3=Object_Object:{extOrderNumber:reference:c0-e1, extDeliveryNote:reference:c0-e2, deliveryFromDat:reference:c0-e3, deliveryToDat:reference:c0-e4, statusList:reference:c0-e5, vehicleCompositeId:reference:c0-e6, vehicleName:reference:c0-e7}\nc0-param4=boolean:false\nc0-param5=boolean:false\nbatchId=1\n''',
      ( NVPair('Content-Type', 'text/plain; charset=UTF-8'), ))
    return result

  def page6(self):
    """GET BestellungExternalSearchEntry.do (request 601)."""
    self.token_deliveryFromDat = \
      '16.11.18'
    self.token_deliveryToDat = \
      '17.11.18'
    self.token_statusList = \
      '1,2,3,4,5,6,7,8,9,10,11,18,12,13,14,15,16'
    result = request601.GET('/logon/order/bestellung/BestellungExternalSearchEntry.do' +
      '?export&deliveryFromDat=' +
      self.token_deliveryFromDat +
      '&deliveryToDat=' +
      self.token_deliveryToDat +
      '&statusList=' +
      self.token_statusList)
    return result

  def page7(self):
    """GET Logout.do (request 701)."""
    result = request701.GET('/logon/order/Logout.do')

    return result
  
  def __call__(self):
    """This method is called for every run performed by the worker thread."""
    self.page1()      # GET / (requests 101-102)
    self.page2()      # POST j_security_check (requests 201-204)
    self.page3()      # GET BestellungExternalSearchEntry.do (requests 301-304)
    self.page4()      # POST OrderAjaxService.searchExternalOrders.dwr (request 401)
    self.page5()      # POST OrderAjaxService.searchExternalOrders.dwr (request 501)
    self.page6()      # GET BestellungExternalSearchEntry.do (request 601)
    grinder.sleep(1000)
    self.page7()
    
    grinder.sleep(600000) # wait for 10 minutes until the next test is started

def instrumentMethod(test, method_name, c=TestRunner):
  """Instrument a method with the given Test."""
  unadorned = getattr(c, method_name)
  import new
  method = new.instancemethod(test.wrap(unadorned), None, c)
  setattr(c, method_name, method)

# Replace each method with an instrumented version.
# You can call the unadorned method using self.page1.__target__().
instrumentMethod(Test(100, 'u_extorder_search_01'), 'page1')
instrumentMethod(Test(200, 'u_extorder_search_02'), 'page2')
instrumentMethod(Test(300, 'u_extorder_search_03'), 'page3')
instrumentMethod(Test(400, 'u_extorder_search_04'), 'page4')
instrumentMethod(Test(500, 'u_extorder_search_05'), 'page5')
instrumentMethod(Test(600, 'u_extorder_search_06'), 'page6')
instrumentMethod(Test(700, 'u_extorder_search_07'), 'page7')
