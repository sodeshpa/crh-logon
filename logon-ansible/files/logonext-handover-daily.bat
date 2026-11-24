@echo off
set DOMAINCFG=LOGONEXT
set EARFILEDIR=%AdnJBossAppDir%\data\provision\LOGONEXT\standalone\deployments
set EARFILEEXT=logon-ext.ear
set EARFILETERM=logon-terminal.ear

:setdefaultvar
set DOMAINNAME=LOGONEXT
set PORTOFFSET=0
IF NOT EXIST %AdnJBossAppDir%\instances\%DOMAINNAME% GOTO createInst
IF EXIST     %AdnJBossAppDir%\instances\%DOMAINNAME% GOTO removeInst


:removeInst
echo  ##############################################################################
echo  The Instanz %DOMAINNAME% allready exist do you want to remove the existing Instanz
echo  ##############################################################################

call adnjboss inst remove %DOMAINNAME% 
call adnjboss inst create %DOMAINNAME% %PORTOFFSET% %AdnJBossProvisionDir%\%DOMAINCFG%
goto exit

:createInst
echo  ##############################################################################
echo  The Instanz %DOMAINNAME% not exist and will be created now  
echo  ##############################################################################
call adnjboss inst create %DOMAINNAME% %PORTOFFSET% %AdnJBossProvisionDir%\%DOMAINCFG%
goto exit 

:exit
exit

