set DOMAINCFG=LOGONLAN
set EARFILEDIR=%AdnJBossAppDir%\data\provision\LOGONLAN\standalone\deployments
set EARFILE=logon-int.ear

:setdefaultvar
set DOMAINNAME=LOGONLAN
set PORTOFFSET=0
IF NOT EXIST %AdnJBossAppDir%\instances\%DOMAINNAME% GOTO createInst
IF EXIST     %AdnJBossAppDir%\instances\%DOMAINNAME% GOTO removeInst


:removeInst
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

