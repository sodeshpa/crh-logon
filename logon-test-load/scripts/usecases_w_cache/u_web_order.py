# The Grinder 3.4
# HTTP script recorded by TCPProxy at Sep 8, 2011 11:23:23 AM

from net.grinder.script import Test as StandardTest

def Test(number, description):
  # Adjust the 10000 to the appropriate offest
  return StandardTest(number+ 20000, description)
  
from net.grinder.script.Grinder import grinder
from net.grinder.plugin.http import HTTPPluginControl, HTTPRequest
from HTTPClient import NVPair

from scripts.base_config import url0, loadtest_username, loadtest_password

connectionDefaults = HTTPPluginControl.getConnectionDefaults()
httpUtilities = HTTPPluginControl.getHTTPUtilities()

# To debug/print out decompressed HTTP responses, uncomment the next line.
# connectionDefaults.useContentEncoding = 1

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
    NVPair('Referer', 'http://localhost:8080/logon/cust/webbestellung/WebBestellungEntry.do'), ]

headers4= \
  [ NVPair('Accept', 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8'),
    NVPair('Referer', 'http://localhost:8080/logon/cust/webbestellung/WebBestellungEntry.do'), ]

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
request301 = Test(301, 'GET WebBestellungEntry.do').wrap(request301)

request304 = HTTPRequest(url=url0, headers=headers3)
request304 = Test(304, 'GET WebOrderAjaxService.js').wrap(request304)

request401 = HTTPRequest(url=url0, headers=headers4)
request401 = Test(401, 'POST WebBestellungEntry.do').wrap(request401)

request404 = HTTPRequest(url=url0, headers=headers3)
request404 = Test(404, 'GET WebOrderAjaxService.js').wrap(request404)

# performing logout action
request501 = HTTPRequest(url=url0, headers=headers4)
request501 = Test(501, 'GET Logout.do').wrap(request501)

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
    """GET WebBestellungEntry.do (requests 301-304)."""
    result = request301.GET('/logon/cust/webbestellung/WebBestellungEntry.do')
    request304.GET('/logon/cust/dwr/interface/WebOrderAjaxService.js')
    return result

  def page4(self):
    """POST WebBestellungEntry.do (requests 401-404)."""
    result = request401.POST('/logon/cust/webbestellung/WebBestellungEntry.do',
      ( NVPair('formAction', 'save'),
        NVPair('durationTime', ''),
        NVPair('distanceMeters', ''),
        NVPair('durationCorrFactor', ''),
        NVPair('compositeId', 'ADN1208869_1002_-1470_-31447'),
        NVPair('order.orderConfFaxNumber', ''),
        NVPair('order.orderConfEmailAdresses', ''),
        NVPair('order.delivnotePaperFlag', 'true'),
        NVPair('maxNumberOfAdditionalItems', '0'),
        NVPair('sapMaterialId', ''),
        NVPair('custWeborderNote', ''), ),
      ( NVPair('Content-Type', 'application/x-www-form-urlencoded'), ))
    request404.GET('/logon/cust/dwr/interface/WebOrderAjaxService.js')
    return result
    
  def page5(self):
    """GET Logout.do (request 301)."""
    result = request501.GET('/logon/order/Logout.do')
    return result

  def __call__(self):
    """This method is called for every run performed by the worker thread."""
    self.page1()      # GET / (requests 101-102)
    self.page2()      # POST j_security_check (requests 201-204)
    self.page3()      # GET WebBestellungEntry.do (requests 301-304)
    self.page4()      # POST WebBestellungEntry.do (requests 401-404)
    grinder.sleep(1000)
    self.page5()      # logout
    
    grinder.sleep(300000) # wait for 5 minutes until the next test is started
    

def instrumentMethod(test, method_name, c=TestRunner):
  """Instrument a method with the given Test."""
  unadorned = getattr(c, method_name)
  import new
  method = new.instancemethod(test.wrap(unadorned), None, c)
  setattr(c, method_name, method)

# Replace each method with an instrumented version.
# You can call the unadorned method using self.page1.__target__().
instrumentMethod(Test(100, 'u_web_order_01'), 'page1')
instrumentMethod(Test(200, 'u_web_order_02'), 'page2')
instrumentMethod(Test(300, 'u_web_order_03'), 'page3')
instrumentMethod(Test(400, 'u_web_order_04'), 'page4')
instrumentMethod(Test(400, 'u_web_order_05'), 'page5')
