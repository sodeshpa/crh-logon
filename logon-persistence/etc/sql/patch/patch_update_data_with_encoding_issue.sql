-- This script is used for DEV ENVIROMENT only
-- Following LOGON-8416
-- Bad character encoding of some testdata on DHULGN01 and RHULGN02

update TLGNC_RAILWAY_STATION set RAILWAY_STATION_NAME = 'F: Siggenthal-Würenlingen' where RAILWAY_STATION_NAME like 'F: Siggenthal-W%renlingen';
update TLGNC_RAILWAY_STATION set RAILWAY_STATION_NAME = 'F: Münchenstein' where RAILWAY_STATION_NAME like 'F: M%nchenstein';

update TLGNS_SAP_VENDOR set CITY = 'Zürich' where CITY LIKE 'Z%rich' AND COUNTRY_CD = 'CH';
update TLGNS_SAP_VENDOR set STREET = 'Röntgenstrasse' where STREET LIKE 'R%ntgenstrasse' AND COUNTRY_CD = 'CH';

update TLGNS_SAP_BP set CITY = 'Zürich' where CITY LIKE 'Z%rich' AND COUNTRY_CD = 'CH';