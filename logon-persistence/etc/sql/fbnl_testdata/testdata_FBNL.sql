

prompt Spooling into file /temp/logon_create_db_testdata_FBNL.sql.log
spool /temp/logon_create_db_testdata_FBNL.sql.log

-- sap_vendor before carrier
@@tlgns_sap_vendor.sql

@@tlgnc_carrier.sql

spool off
