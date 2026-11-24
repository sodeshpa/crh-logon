prompt
prompt ************************************************************************
prompt *** Ticket specific DB patch changes
prompt ************************************************************************

prompt ************************************************************************
prompt *** BEGIN LOGON-9554 : Consider certified from and to for drivers at PDA login
prompt ************************************************************************

-- Missing error text will be inserted for the new Invalid certificate return message to mobile client
@@../../refdata/tlgnr_mop_status.sql

prompt ************************************************************************
prompt *** END LOGON-9554 : Consider certified from and to for drivers at PDA login
prompt ************************************************************************

prompt ************************************************************************
prompt *** BEGIN LOGON-9183 : LRQ 403.0: New GUI to upload rail distance data in table TLGNC_RAIL_TIMETABLE.
prompt ************************************************************************

-- Adding logon admin lite to the new Rail Distance/Timetable functionality, according to comment by: 
-- Enrique Velasco [Crh] - 01.12.2015 16:56
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  23 /* Logon Admin Lite */,  8641);

prompt ************************************************************************
prompt *** END LOGON-9183 : LRQ 403.0: New GUI to upload rail distance data in table TLGNC_RAIL_TIMETABLE.
prompt ************************************************************************

-- ^^^ Add Patches HERE ^^^, COPY the "template" first from below. Be careful with dependencies amongst individual patches!
-- HINT: look at helper tool "logon-persistence/logon_alter_db.sh" !

-- BEGIN TEMPLATE
----------------------------------------------------------------------------------------------------------------|
-- LOGON-XXXX                                                                                                   |
-- LRQ xyz.u : Name / Description                                                                               |
-- Summary of the things your are patching, etc,                                                                |
----------------------------------------------------------------------------------------------------------------|

-- commit;
-- END TEMPLATE

--
-- Typically this includes
-- - create table
-- - alter table add
-- - alter table modify
-- - alter table drop colum: Careful with drop! Migration may fail -> data loss!!
-- - PK
-- - Sequences (add here, for better readibility)
-- - FK (add here as well, better readibility if things are close together)
--
-- Correct way to add non-nullable columns:
-- - Add new columns (with null - NO DEFAULT!)
-- - Update/Migration
-- - Change added columns to not null if needed.
-- => Do NOT work with db default in new columns! <=
--    (Reason: They cannot be removed completely anymore, only set to 'default null')
--
-- => Write update statements in a way that re-executing the same statement
--    would not corrupt the data, i.e. "idempotent".
--    This may be needed when delivering several times in a dev or test-
--    environment but the patch is actually written for a productive migration
--    Errors will occur (to be tolerated in this situation), but data loss or
--    corruption is unacceptable just because script is executed several times!

prompt
