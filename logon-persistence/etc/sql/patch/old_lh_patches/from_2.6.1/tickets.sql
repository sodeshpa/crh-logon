prompt
prompt ************************************************************************
prompt *** Ticket specific DB patch changes
prompt ************************************************************************

prompt ************************************************************************
prompt *** BEGIN LOGON-7649 : LRQ 220.8 Automatic Maintenance of Dispocards
prompt ************************************************************************

@@create_table_tlgnc_shipto_sarea_rest_maint.sql

prompt ************************************************************************
prompt *** END LOGON-7649 : LRQ 220.8 Automatic Maintenance of Dispocards
prompt ************************************************************************

prompt ************************************************************************
prompt *** BEGIN LOGON-9183 : LRQ 403.0: New GUI to upload rail distance data in table TLGNC_RAIL_TIMETABLE.
prompt ************************************************************************

insert into tlgnu_func (func_id, func_name, func_descr) VALUES ('8641', 'Master Data Rail Timetable Distance', 'Master Data: Rail Timetable, including upload and Rail Distance.');

insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8641);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  5 /* Rail Admin          */,  8641);

delete from tlgnu_role_func where role_id = 5 and func_id = 8640;

-- Delete redundant data from the distance table.
-- For example delete one of these distances: Budapest,Wien,120 ; Wien,Budapest,120
-- Also delete records where the from station and the to station are equal.
delete from tlgnc_rail_distance dist
where (dist.rail_distance_id in 
  (select d1.rail_distance_id
   from tlgnc_rail_distance d1, tlgnc_rail_distance d2
   where d1.dep_railway_station_id = d2.arr_railway_station_id 
	  and d1.arr_railway_station_id = d2.dep_railway_station_id))
  or dist.arr_railway_station_id = dist.dep_railway_station_id;

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
