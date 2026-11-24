prompt
prompt ************************************************************************
prompt *** Ticket specific DB patch changes
prompt ************************************************************************

prompt ************************************************************************
prompt *** BEGIN LOGON-9562 : Proof of concept: Introducing country into the authorization concept of pools
prompt ************************************************************************

@@create_table_tlgnr_country_bs.sql
@@create_table_tlgnc_country_pool.sql
@@create_table_tlgnu_user_country_acc.sql

CREATE SEQUENCE  "SLGNC_COUNTRY_POOL"  MINVALUE 1 MAXVALUE 88888888888 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE ;

CREATE SEQUENCE  "SLGNU_USER_COUNTRY_ACC"  MINVALUE 1 MAXVALUE 88888888888 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE ;

prompt ************************************************************************
prompt *** END LOGON-9562 : Proof of concept: Introducing country into the authorization concept of pools
prompt ************************************************************************

prompt ************************************************************************
prompt *** BEGIN LOGON-9565 : LRQ 220.8 Import 'Parent Offer' (PO) reference for SAP Contract Items
prompt ************************************************************************

ALTER TABLE TLGNS_SAP_CONTRACT_ITEM ADD PARENT_OFFER_NO VARCHAR(70) NULL;

prompt ************************************************************************
prompt *** END LOGON-9565 : LRQ 220.8 Import 'Parent Offer' (PO) reference for SAP Contract Items
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
