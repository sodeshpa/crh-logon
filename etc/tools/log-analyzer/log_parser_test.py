import unittest

from log_parser import parse_log_lines
from error_entry import ErrorEntry

class LogParserTest(unittest.TestCase):

	def test_should_find_correct_number_of_error_entries(self):

		print "==> test_should_find_correct_number_of_error_entries"

		lines = []
		lines.append("2014-03-10 11:54:13,815 WARN  [action.ActionMapping:78  ] [http-0.0.0.0-80-16] Unable to find 'null' forward.")
		lines.append("2014-03-10 11:54:13,815 INFO  [             jcan.Op:90  ] [http-0.0.0.0-80-16] 2 <==== rtCtx=defaultPackage/v1.0/defaultServerInstance, pCtx=0a265843/1358/018878be, obj=jnp://localhost:1099/UserAdminService, mth=externalPasswordChange, tID=0a264110.1358.0a265843.0020c90d-0020c910, pri=hkroeger, sC=OK, dT=47ms, usedMem=589389216, extLogin=hkroeger, newExtLoginPwd=*****, isOneTimePwd=*****")
		lines.append("2014-03-10 12:05:19,319 ERROR [LogonSessionListener:98  ] [http-0.0.0.0-80-52] could not record logout user activity")
		lines.append("java.lang.NullPointerException")
		lines.append("	at com.crh.logon.web.listeners.LogonSessionListener.sessionDestroyed(LogonSessionListener.java:93)")
		lines.append("	at org.apache.catalina.session.StandardSession.expire(StandardSession.java:702)")
		lines.append("2014-03-10 12:05:46,869 INFO  [             jcan.Op:90  ] [http-0.0.0.0-80-53] 2 <==== rtCtx=defaultPackage/v1.0/defaultServerInstance, pCtx=0a265843/1358/00ce89f7, obj=jnp://localhost:1099/ConfigService, mth=getConfigBean, tID=0a26410d.1358.0a265843.0020ceed-0020ceee, pri=mkunz, sC=OK, dT=15ms, usedMem=853026800")
		lines.append("2014-03-10 07:13:14,590 ERROR [icesExceptionHandler:82  ] [http-0.0.0.0-80-19] ")
		lines.append("2014-03-10 12:43:57,088 INFO  [             jcan.Op:90  ] [DefaultQuartzScheduler_Worker-2] 2 <<<<< rtCtx=defaultPackage/v1.0/defaultServerInstance, pCtx=0a265843/1358/018a2d93, obj=SapImportService, mth=shouldImportJobRun, tID=0a265843.1358.0a265843.0020db88-0020db89, pri=1 (<anonymous>), sC=EXCEPTION, dT=42074ms, usedMem=533511376, cronstatusName=sapimport_mat")

		error_entries = parse_log_lines(lines)
		self.assertEqual(3, len(error_entries))

		print "<== test_should_find_correct_number_of_error_entries"

	def test_error_entry_fields(self):

		print "==> test_error_entry_fields"

		timestamp 	= "2014-03-10 11:54:13,815"
		loglevel 	= "WARN"
		classname 	= "action.ActionMapping:78"
		thread		= "http-0.0.0.0-80-16"
		message 	= "Unable to find 'null' forward."

		error_line = "{0} {1}  [{2}  ] [{3}] {4}".format(timestamp, loglevel, classname, thread, message)

		lines = []		
		lines.append(error_line)

		error_entries 	= parse_log_lines(lines)
		error_entry 	= error_entries[0]

		self.assertEqual(timestamp, 	error_entry.timestamp)
		self.assertEqual(loglevel, 		error_entry.loglevel)
		self.assertEqual(classname, 	error_entry.classname)
		self.assertEqual(thread, 		error_entry.thread)
		self.assertEqual(message, 		error_entry.message)

		print "<== test_error_entry_fields"

def main():
    unittest.main()

if __name__ == "__main__":
    main()
