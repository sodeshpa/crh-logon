setlocal
FOR /F "tokens=*" %%i in ('type testclient.properties') do SET %%i
soapui/bin/testrunner.bat -h %TESTCLIENT_TARGET_URL% -a -fresults project.xml %*
endlocal
