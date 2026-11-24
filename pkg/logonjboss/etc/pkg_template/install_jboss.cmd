@echo off
setlocal

set scriptDir=%~dp0
set JBossInstance=Logon_@ZONE@
set JBossDirPrefix=@PKG_WIN_INSTALL_DIR@JBossEAP-6.4.0_
set JBossDir=%JBossDirPrefix%%JBossInstance%
set error=0

rem ============================
rem = check preconditions
rem ============================
rem check AdnJBoss is not installed
sc query ADNJBOSS-LOGONINT  >NUL && (
  echo Windows service ADNJBOSS-LOGONINT found
  echo please uninstall AdnJBoss first
  set /a error=error+1
  )

sc query ADNJBOSS-LOGONEXT >NUL &&  (
  echo Windows service ADNJBOSS-LOGONEXT found
  echo please uninstall AdnJBoss first
  set /a error=error+1
  )

sc query umg  >NUL && (
  echo Windows service umg found
  echo please uninstall AdnGlassFish first
  set /a error=error+1
  )

if exist "%JBossDirPrefix%Logon_int" (
  echo found "%JBossDirPrefix%int"
  echo Please uninstall "%JBossDirPrefix%Logon_int" first
  set /a error=error+1
  )

if exist "%JBossDirPrefix%Logon_ext" (
  echo found "%JBossDirPrefix%ext"
  echo Please uninstall "%JBossDirPrefix%Logon_ext" first
  set /a error=error+1
  )

rem check for java
if not exist "%JAVA_HOME%/bin/java.exe" (
  echo "%JAVA_HOME%/bin/java.exe" not found
  echo please ensure java is installed and JAVA_HOME is correctly set
  set /a error=error+1
  )

if not exist "jboss-eap-6.4.0-installer.jar" (
  echo jboss-eap-6.4.0-installer.jar not found
  echo please place the jboss-installer package in the same directory
  set /a error=error+1
  )

if %error% gtr 0 (
  echo %error%
  goto errorExit
  )

rem ============================
rem = installation
rem ============================
powershell -ExecutionPolicy ByPass "& .\helpers\generateConfig.ps1"
pause

rem ============================
rem = installation
rem ============================

rem install JBoss for this instance
java -jar "%scriptDir%\jboss-eap-6.4.0-installer.jar" "%scriptDir%\config\autoinstall.xml" -variablefile "%scriptDir%\config\autoinstall.xml.variables" || (
  echo Error during installation
  echo please check output above
  goto errorExit
  )

copy "%scriptDir%\config\standalone.xml" "%JBossDir%\standalone\configuration\standalone.xml" || (
  echo failed to copy standalone.xml to "%JBossDir%\standalone\configuration\"
  goto errorExit
  )

copy "%scriptDir%\config\logon.properties" "%JBossDir%\bin\" || (
  echo failed to copy logon.properties to "%JBossDir%\bin\
  goto errorExit
  )

copy "%scriptDir%\config\standalone.conf.bat" "%JBossDir%\bin\" || (
  echo failed to copy standalone.conf.bat to "%JBossDir%\bin\
  goto errorExit
  )


call "%JBossDir%\modules\system\layers\base\native\sbin\service.bat" install /startup /name "JBoss-%JBossInstance%" /controller localhost:10099 /desc "JBoss Instance for %JBossInstance%" || (
  echo failed to install JBoss Service
  )

sc start "JBoss-%JBossInstance%" || (
  echo failed to start JBoss service
  goto errorExit
  )

goto exit

:errorExit
echo =========================
echo Installation failed
echo =========================
pause
goto :eof

:exit
echo =========================
echo Installation finished
echo =========================
pause
rem exit 0
