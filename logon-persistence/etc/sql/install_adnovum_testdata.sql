/**
 * Installs AdNovum testdata into the top of a Logon production dump.
 */

spool /temp/logon_install_adnovum_testdata.log

@set_logon_session_settings.sql

--Adnovum test wagon types, which refer onwards to the Adnovum test repair and maintenance materials 
--(adza_testdata/insert_tlgns_sap_material.sql will set the fields repair/maintenance material).
@testdata/tlgnr_wagon_type.sql

@adza_initialdata/initialdata_ADZA.sql
@adza_testdata/testdata_ADZA.sql
@testdata_load_prod_dump.sql
@testdata/insert_config.sql

spool off
