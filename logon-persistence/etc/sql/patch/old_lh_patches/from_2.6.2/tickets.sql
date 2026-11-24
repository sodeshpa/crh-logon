prompt
prompt ************************************************************************
prompt *** Ticket specific DB patch changes
prompt ************************************************************************

prompt ************************************************************************
prompt *** BEGIN LOGON-9562 : Proof of concept: Introducing country into the authorization concept of pools
prompt ************************************************************************

RENAME SLGNC_COUNTRY_POOL to SLGNC_REGION_POOL;
RENAME SLGNU_USER_COUNTRY_ACC to SLGNU_USER_REGION_ACC;

ALTER TABLE TLGNR_COUNTRY_BS RENAME TO TLGNR_REGION_BS;
ALTER TABLE TLGNC_COUNTRY_POOL RENAME TO TLGNC_REGION_POOL;
ALTER TABLE TLGNU_USER_COUNTRY_ACC RENAME TO TLGNU_USER_REGION_ACC;

ALTER TABLE TLGNR_REGION_BS RENAME COLUMN COUNTRY_CD TO REGION_CD;
ALTER TABLE TLGNR_REGION_BS RENAME COLUMN COUNTRY_NAME TO REGION_NAME;

ALTER TABLE TLGNU_USER_REGION_ACC RENAME COLUMN COUNTRY_CD TO REGION_CD;
ALTER TABLE TLGNU_USER_REGION_ACC RENAME COLUMN USER_COUNTRY_ACC_ID TO USER_REGION_ACC_ID;
ALTER TABLE TLGNU_USER_REGION_ACC RENAME CONSTRAINT FK_TLGNR_COUNTRY_BS TO FK_TLGNR_REGION_BS;
ALTER TABLE TLGNU_USER_REGION_ACC RENAME CONSTRAINT FK_COUNTRY_ACC_USER TO FK_REGION_ACC_USER;
ALTER TABLE TLGNU_USER_REGION_ACC RENAME CONSTRAINT UQ_TLGNU_USER_COUNTRYCD_ACC TO UQ_TLGNU_USER_REGIONCD_ACC;

ALTER TABLE TLGNC_REGION_POOL RENAME COLUMN COUNTRY_CD TO REGION_CD;
ALTER TABLE TLGNC_REGION_POOL RENAME COLUMN COUNTRY_POOL_ID TO REGION_POOL_ID;
ALTER TABLE TLGNC_REGION_POOL RENAME CONSTRAINT FK_TLGNC_CP_TLGNR_COUNTRY_BS TO FK_TLGNC_RP_TLGNR_REGION_BS;
ALTER TABLE TLGNC_REGION_POOL RENAME CONSTRAINT UQ_TLGNC_COUNTRY_POOL TO UQ_TLGNC_REGION_POOL;
ALTER TABLE TLGNC_REGION_POOL RENAME CONSTRAINT FK_TLGNC_CP_TLGNC_POOL TO FK_TLGNC_RP_TLGNC_POOL;

ALTER TABLE TLGNR_COMPANYCODE_BS RENAME COLUMN COUNTRY_CD TO REGION_CD;

@../../refdata/tlgnr_region_bs.sql

ALTER TABLE TLGNR_COMPANYCODE_BS ADD CONSTRAINT "FK_TLGNR_COMCODE_BS_COUNTRY" FOREIGN KEY ("REGION_CD") REFERENCES "TLGNR_REGION_BS" ("REGION_CD");


prompt ************************************************************************
prompt *** END LOGON-9562 : Proof of concept: Introducing country into the authorization concept of pools
prompt ************************************************************************

prompt ************************************************************************
prompt *** BEGIN LOGON-9267 : LRQ 090.1 Refine archiving flag subject
prompt ************************************************************************

@@create_table_tlgnr_archiving_status_bs.sql
@@../../refdata/tlgnr_archiving_status_bs.sql
ALTER TABLE TLGNC_ORDER ADD ARCHIVING_STATUS_CD NUMBER(2);
ALTER TABLE         TLGNC_ORDER
    ADD CONSTRAINT  FK_TLGNR_ORDER_ARCH_STATUS
    FOREIGN KEY     (ARCHIVING_STATUS_CD)
    REFERENCES      TLGNR_ARCHIVING_STATUS_BS (ARCHIVING_STATUS_CD);
UPDATE TLGNC_ORDER SET ARCHIVING_STATUS_CD = 1 WHERE IS_ARCHIVED_FLAG = 0;
UPDATE TLGNC_ORDER SET ARCHIVING_STATUS_CD = 2 WHERE IS_ARCHIVED_FLAG = 1;
ALTER TABLE TLGNC_ORDER MODIFY (ARCHIVING_STATUS_CD NOT NULL);

prompt ************************************************************************
prompt *** END LOGON-9267 : LRQ 090.1 Refine archiving flag subject
prompt ************************************************************************

prompt ************************************************************************
prompt *** BEGIN LOGON-9548 : Authorization concept: Part "User types" and "user admin lite optimizations"
prompt ************************************************************************

insert into tlgnu_func (func_id, func_name, func_descr) VALUES ('9150', 'Create User Type Crh','Access to create user with type Crh');
insert into tlgnu_func (func_id, func_name, func_descr) VALUES ('9151', 'Create User Type Customer', 'Access to create user with type Customer');
insert into tlgnu_func (func_id, func_name, func_descr) VALUES ('9152', 'Create User Type Vendor', 'Access to create user with type Vendor');
insert into tlgnu_func (func_id, func_name, func_descr) VALUES ('9153', 'Create User Type Driver', 'Access to create user with type Driver');
insert into tlgnu_func (func_id, func_name, func_descr) VALUES ('9154', 'Create User Type Admin', 'Access to create user with type Admin');

insert into tlgnu_func (func_id, func_name, func_descr) VALUES ('9143', 'User Admin Grant non default roles', 'User Admin: Right to grant roles besides the default ones');

alter table TLGNR_USER_TYPE_BS add FUNCTION_ID NUMBER(12,0) null;

--these are already existing records and tlgnr_user_type.sql would not update them
update TLGNR_USER_TYPE_BS
SET FUNCTION_ID = 9150
WHERE USER_TYPE_CD = 0;

update TLGNR_USER_TYPE_BS
SET FUNCTION_ID = 9151
WHERE USER_TYPE_CD = 1;

update TLGNR_USER_TYPE_BS
SET FUNCTION_ID = 9152
WHERE USER_TYPE_CD = 2;

@@../../refdata/tlgnr_user_type.sql

alter table TLGNR_USER_TYPE_BS modify FUNCTION_ID NUMBER(12,0) not null;

ALTER TABLE TLGNR_USER_TYPE_BS
ADD CONSTRAINT USER_TYPE_CREATE_FUNCTION
   FOREIGN KEY (FUNCTION_ID)
   REFERENCES TLGNU_FUNC (FUNC_ID);

--default values to keep old funcionalities of useradmin screen.
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  9143);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  2 /* User Admin          */,  9143);

insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  9150);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  9151);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  9152);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  9153);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin          */, 9154);

insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  2 /* User Admin          */,  9150);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  2 /* User Admin          */,  9151);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  2 /* User Admin          */,  9152);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  2 /* User Admin          */,  9153);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  2 /* User Admin          */,  9154);

insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 24 /* User Admin Lite     */,  9151);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 24 /* User Admin Lite     */,  9153);   
   
prompt ************************************************************************
prompt *** END LOGON-9548 : Authorization concept: Part "User types" and "user admin lite optimizations"
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
