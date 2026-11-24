# coding=utf8
# The Grinder 3.11
# HTTP script recorded by TCPProxy at Jul 28, 2014 1:27:00 PM

import string
import random
from datetime import date, datetime, timedelta
from net.grinder.script import Test as StandardTest
from net.grinder.script.Grinder import grinder
from net.grinder.plugin.http import HTTPPluginControl, HTTPRequest
from HTTPClient import NVPair

from scripts.base_config import url0, loadtest_username, loadtest_password, num_repetitions, proxy_host, proxy_port
from scripts.mop_suite import users, pdas, trucks

connectionDefaults = HTTPPluginControl.getConnectionDefaults()
connectionDefaults.useContentEncoding = 1

if len(proxy_host) > 0:
	connectionDefaults.setProxyServer(proxy_host, proxy_port)

httpUtilities = HTTPPluginControl.getHTTPUtilities()

def Test(number, description):
  return StandardTest(number+ 120000, description)

def createRequest(test, url, headers=None):
    request = HTTPRequest(url=url)
    if headers: request.headers=headers
    test.record(request, HTTPRequest.getHttpMethodFilter())
    return request

# Prepare HTTP Headers ##########################################################
headers0= \
  [ NVPair('Accept-Encoding', 'gzip,deflate'),
    NVPair('User-Agent', 'Apache-HttpClient/4.1.1 (java 1.5)'),
    NVPair('SOAPAction', 'consume'),]

# login
request101 = createRequest(Test(101, 'u_mop_1'), url0, headers0)
switchTruckRequest = createRequest(Test(102, 'u_mop_102'), url0, headers0)
useTruckRequest = createRequest(Test(103, 'u_mop_103'), url0, headers0)
# geoPosition
request201 = createRequest(Test(201, 'u_mop_2'), url0, headers0)
# done
# pause
request301 = createRequest(Test(301, 'u_mop_3'), url0, headers0)
request401 = createRequest(Test(401, 'u_mop_4'), url0, headers0)
# fertig pause
# message to logon
request501 = createRequest(Test(501, 'u_mop_5'), url0, headers0)
# logout
request601 = createRequest(Test(601, 'u_mop_6'), url0, headers0)

class TestRunner:
  """A TestRunner instance is created for each worker thread."""

  def page1(self):
    uniqueNumber = (grinder.runNumber % 20 + 20 *(grinder.threadNumber))
    self.currTime = (datetime.now() + timedelta(seconds=random.randint(-3600, 0))).strftime("%Y-%m-%dT%H:%M:%S")
    self.pda = pdas[uniqueNumber]
    self.userid = users[uniqueNumber]
    self.truck = trucks[uniqueNumber]
    grinder.logger.info("Using pda %s" % uniqueNumber)
    grinder.logger.info("Using user %s" % uniqueNumber)
    grinder.logger.info("Using truck %s" % uniqueNumber)
    grinder.logger.info("Using event time = %s and pdaid = %s userid = %s truck = %s" % (self.currTime, self.pda, self.userid, self.truck))

    result = request101.POST('/logon/webservices-mop-incoming/services/MOPEventConsumer',
      '''<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">
		<s:Body xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">
			<MOPMessage xmlns=\"http://logon.crh.com/2009/09/MOPEvent/\">
				<senderPDAID xmlns=\"\">%s</senderPDAID>
				<sendersTime xmlns=\"\">%s</sendersTime>
				<sessionID xmlns=\"\"></sessionID>
				<MOPEvent xmlns=\"\">
					<eventID>123</eventID>
					<eventTime>%s</eventTime>
					<eventName xmlns=\"\">login</eventName>
					<geoX xmlns=\"\">8.529017</geoX>
					<geoY xmlns=\"\">47.387571</geoY>
					<speed xmlns=\"\">50</speed>
					<course xmlns=\"\">0</course>
					<geoTime xmlns=\"\">%s</geoTime>
					<MOPData xmlns=\"\">
						<Field><name>pdaUserLabel</name><value>%s</value></Field>
						<Field><name>pdaUserRFIDIdentity</name><value></value></Field>
						<Field><name>PIN</name><value>1234</value></Field>
						<Field><name>forceLogin</name><value>true</value></Field>
					</MOPData>
				</MOPEvent>
			</MOPMessage>
		</s:Body>
	</s:Envelope>''' % (self.pda, self.currTime, self.currTime, self.currTime, self.userid) ,
      ( NVPair('Content-Type', 'text/xml;charset=UTF-8'), ))


    if string.find(result.getText(), "Login OK") < 1:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("Could not log in for sending pda MOP messages! Is pda ID %s a valid one? user=%s" % (self.pda, self.userid))
        grinder.logger.error(result.getText())
	return False
    import re # avoid weird ValueError
    ex = re.compile(r"<contextValue>([\w\d]*)</contextValue>") # sessionID sits in here
    res = ex.findall(result.getText())
    if len(res) < 1:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("No sessionID found in MOP response!")
        grinder.logger.error(result.getText())
	return False
    self.sessionID = res[0]
    grinder.logger.info("Logged in with sessionID = '%s'" % self.sessionID)
    return True

  def useTruck(self):
    result = switchTruckRequest.POST('/logon/webservices-mop-incoming/services/MOPEventConsumer',
      '''<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">
		<s:Body xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">
			<MOPMessage xmlns=\"http://logon.crh.com/2009/09/MOPEvent/\">
				<senderPDAID xmlns=\"\">%s</senderPDAID>
				<sendersTime xmlns=\"\">%s</sendersTime>
				<sessionID xmlns=\"\">%s</sessionID>
				<MOPEvent xmlns=\"\">
					<eventID>123</eventID>
					<eventTime>%s</eventTime>
					<eventName xmlns=\"\">switchTruck</eventName>
					<geoX xmlns=\"\">8.529017</geoX>
					<geoY xmlns=\"\">47.387571</geoY>
					<speed xmlns=\"\">50</speed>
					<course xmlns=\"\">0</course>
					<geoTime xmlns=\"\">%s</geoTime>
					<MOPData xmlns=\"\">
						<Field><name>truckLabel</name><value>%s</value></Field>
               					<Field><name>truckRFIDIdentity</name><value/></Field>
					</MOPData>
				</MOPEvent>
			</MOPMessage>
		</s:Body>
	</s:Envelope>''' % (self.pda, self.currTime, self.sessionID, self.currTime, self.currTime, self.truck) ,
      ( NVPair('Content-Type', 'text/xml;charset=UTF-8'), ))

    if string.find(result.getText(), "<txt>OK</txt>") < 1:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("Could not send switch truck! %s %s" % (self.truck, self.pda))
        grinder.logger.error(result.getText())
	return False
    else:
	result = useTruckRequest.POST('/logon/webservices-mop-incoming/services/MOPEventConsumer',
	      '''<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">
			<s:Body xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">
				<MOPMessage xmlns=\"http://logon.crh.com/2009/09/MOPEvent/\">
					<senderPDAID xmlns=\"\">%s</senderPDAID>
					<sendersTime xmlns=\"\">%s</sendersTime>
					<sessionID xmlns=\"\">%s</sessionID>
					<MOPEvent xmlns=\"\">
						<eventID>123</eventID>
						<eventTime>%s</eventTime>
						<eventName xmlns=\"\">useTruckComposition</eventName>
						<geoX xmlns=\"\">8.529017</geoX>
						<geoY xmlns=\"\">47.387571</geoY>
						<speed xmlns=\"\">50</speed>
						<course xmlns=\"\">0</course>
						<geoTime xmlns=\"\">%s</geoTime>
						<MOPData xmlns=\"\">
							<Field><name>truckLabel</name><value>%s</value></Field>
						</MOPData>
					</MOPEvent>
				</MOPMessage>
			</s:Body>
		</s:Envelope>''' % (self.pda, self.currTime, self.sessionID, self.currTime, self.currTime, self.truck) ,
	      ( NVPair('Content-Type', 'text/xml;charset=UTF-8'), ))
	if string.find(result.getText(), "<txt>OK</txt>") < 1:
        	grinder.statistics.forCurrentTest.setSuccess(0)
       		grinder.logger.error("Could not send use truck! %s %s" % (self.truck, self.pda))
        	grinder.logger.error(result.getText())
		return False

    return True;

  def page2(self):
    result = request201.POST('/logon/webservices-mop-incoming/services/MOPEventConsumer',
      '''<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"><s:Body xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><MOPMessage xmlns=\"http://logon.crh.com/2009/09/MOPEvent/\"><senderPDAID xmlns=\"\">%s</senderPDAID><sendersTime xmlns=\"\">2014-07-29T14:04:17</sendersTime><sessionID xmlns=\"\">%s</sessionID><MOPEvent xmlns=\"\"><eventID>123</eventID><eventTime>2014-07-29T14:04:17</eventTime><eventName>geoPosition</eventName><geoX>8.529017</geoX><geoY>47.387571</geoY><speed>50</speed><course>0</course><geoTime>2014-07-29T14:04:17</geoTime><MOPData><Field><name>assignmentPartID</name><value/></Field></MOPData></MOPEvent></MOPMessage></s:Body></s:Envelope>''' % (self.pda, self.sessionID),
      ( NVPair('Content-Type', 'text/xml;charset=UTF-8'), ))
    if string.find(result.getText(), "<txt>OK</txt>") < 1:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("Could not send geoPosition event!")
        grinder.logger.error(result.getText())
    return result

  def page3(self):
    result = request301.POST('/logon/webservices-mop-incoming/services/MOPEventConsumer',
      '''<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:mop=\"http://logon.crh.com/2009/09/MOPEvent/\"><soapenv:Header/><soapenv:Body><MOPMessage xmlns=\"http://logon.crh.com/2009/09/MOPEvent/\"><senderPDAID xmlns=\"\">%s</senderPDAID><sendersTime xmlns=\"\">%s</sendersTime><sessionID xmlns=\"\">%s</sessionID><MOPEvent xmlns=\"\"><eventID>123</eventID><eventTime>%s</eventTime><eventName>pause</eventName><geoX>8.529017</geoX><geoY>47.387571</geoY><speed>50</speed><course>0</course><geoTime>%s</geoTime><MOPData/></MOPEvent></MOPMessage></soapenv:Body></soapenv:Envelope>''' % (self.pda, self.currTime, self.sessionID, self.currTime, self.currTime),
      ( NVPair('Content-Type', 'text/xml;charset=UTF-8'), ))
    if string.find(result.getText(), "<txt>OK</txt>") < 1:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("Could not send pause start event!")
        grinder.logger.error(result.getText())
    return result

  def page4(self):
    result = request401.POST('/logon/webservices-mop-incoming/services/MOPEventConsumer',
      '''<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:mop=\"http://logon.crh.com/2009/09/MOPEvent/\"><soapenv:Header/><soapenv:Body><MOPMessage xmlns=\"http://logon.crh.com/2009/09/MOPEvent/\"><senderPDAID xmlns=\"\">%s</senderPDAID><sendersTime xmlns=\"\">%s</sendersTime><sessionID xmlns=\"\">%s</sessionID><MOPEvent xmlns=\"\"><eventID>123</eventID><eventTime>%s</eventTime><eventName>resume</eventName><geoX>8.529017</geoX><geoY>47.387571</geoY><speed>50</speed><course>0</course><geoTime>%s</geoTime><MOPData/></MOPEvent></MOPMessage></soapenv:Body></soapenv:Envelope>''' % (self.pda, self.currTime, self.sessionID, self.currTime, self.currTime),
      ( NVPair('Content-Type', 'text/xml;charset=UTF-8'), ))
    if string.find(result.getText(), "<txt>OK</txt>") < 1:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("Could not send pause end event!")
        grinder.logger.error(result.getText())
    return result

  def page5(self):
    result = request501.POST('/logon/webservices-mop-incoming/services/MOPEventConsumer',
      '''<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:mop=\"http://logon.crh.com/2009/09/MOPEvent/\"><soapenv:Header/><soapenv:Body><MOPMessage xmlns=\"http://logon.crh.com/2009/09/MOPEvent/\"><senderPDAID xmlns=\"\">%s</senderPDAID><sendersTime xmlns=\"\">%s</sendersTime><sessionID xmlns=\"\">%s</sessionID><MOPEvent xmlns=\"\"><eventID>123</eventID><eventTime>%s</eventTime><eventName>messageToLogon</eventName><geoX>8.529017</geoX><geoY>47.387571</geoY><speed>50</speed><course>0</course><geoTime>%s</geoTime><MOPData><Field><name>message</name><value>Hello World From Grinder Load Test</value></Field></MOPData></MOPEvent></MOPMessage></soapenv:Body></soapenv:Envelope>''' % (self.pda, self.currTime, self.sessionID, self.currTime, self.currTime),
      ( NVPair('Content-Type', 'text/xml;charset=UTF-8'), ))
    if string.find(result.getText(), "<txt>OK</txt>") < 1:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("Could not send logon message event! user = %s" % self.userid)
        grinder.logger.error(result.getText())
    return result

  def page6(self):
    result = request601.POST('/logon/webservices-mop-incoming/services/MOPEventConsumer',
      '''<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:mop=\"http://logon.crh.com/2009/09/MOPEvent/\"><soapenv:Header/><soapenv:Body><MOPMessage xmlns=\"http://logon.crh.com/2009/09/MOPEvent/\"><senderPDAID xmlns=\"\">%s</senderPDAID><sendersTime xmlns=\"\">%s</sendersTime><sessionID xmlns=\"\">%s</sessionID><MOPEvent xmlns=\"\"><eventID>123</eventID><eventTime>%s</eventTime><eventName>logout</eventName><geoX>8.529017</geoX><geoY>47.387571</geoY><speed>50</speed><course>0</course><geoTime>%s</geoTime><MOPData/></MOPEvent></MOPMessage></soapenv:Body></soapenv:Envelope>''' % (self.pda, self.currTime, self.sessionID, self.currTime, self.currTime),
      ( NVPair('Content-Type', 'text/xml;charset=UTF-8'), ))
    if string.find(result.getText(), "<txt>OK</txt>") < 1:
        grinder.statistics.forCurrentTest.setSuccess(0)
        grinder.logger.error("Could not send logout message event! user = %s" % self.userid)
        grinder.logger.error(result.getText())
    return result


  # Test Plan / Test Entry Point ################################################
  def __call__(self):
    """Called for every run performed by the worker thread."""
    if self.page1():           # login
    	if self.useTruck(): # usetruck
    		for x in range(num_repetitions):
      			self.page2()         # geoPosition
      		if x % 20 == 0:
        		self.page3()       # pause start
        		self.page4()       # pause end
      		if x % 10 == 0:
        		self.page5()       # logon message
	        self.page6()           # logout
    		grinder.sleep(1000)



# Instrument page methods #######################################################
Test(100, 'u_mop_login').record(TestRunner.page1)
Test(101, 'u_mop_truck').record(TestRunner.useTruck)
Test(200, 'u_mop_position').record(TestRunner.page2)
Test(300, 'u_mop_pause').record(TestRunner.page3)
Test(400, 'u_mop_pause_done').record(TestRunner.page4)
Test(500, 'u_mop_logon_msg').record(TestRunner.page5)
Test(600, 'u_mop_logout').record(TestRunner.page6)

