Param(
  $connectionstring,
  $dbdumpfile,
  $packagelocation,
  $logs,
  $dumpdir
)
# This script drops the whole logon database, then recreates the users and imports the dump.
# $packagelocation should be the latest db package that contains the drop_user.sql and user.sql files.
#
# NOTE that the dump should always be located in I:\orabackup\TLGNEBM1\
# to change this: http://stackoverflow.com/questions/21182196/invalid-value-for-parameter-directory
#
# EXAMPLE for invocation
# .\importdump-v1-0.ps1 '@logonebmdb3-int.zh.adnovum.ch:1521/TLGNEBM1' 'EXPDP_LOGONEBMDB5-INT_2.2.8.2_20150218.DMP' 'I:\2.3.1.5\logondb' 'C:\TEMP\importdump.log'

write-host Connecting to db > $logs
$logon = "sys/managed@$($connectionstring) as sysdba"
write-host using $logon >> $logs

#this kills the sessions and drops the users and the WHOLE database.
$dropuser = "@$($packagelocation)\admin\drop_user.sql"
echo exit | sqlplus.exe -L $logon $dropuser >> $logs

#recreate the users
$user = "@$($packagelocation)\user.sql"
echo exit | sqlplus.exe -L $logon $user >> $logs

#let's import the dump
write-host impdp "'$($logon)' directory=$($dumpdir)  dumpfile=$($dbdumpfile) logfile=imp_logon_full.log schemas=ulgn01,ulgn02,ulgn03,ulgnsap" >> $logs
impdp "'$($logon)' directory=$($dumpdir) dumpfile=$($dbdumpfile) logfile=imp_logon_full.log schemas=ulgn01,ulgn02,ulgn03,ulgnsap" >> $logs

write-host Finished
