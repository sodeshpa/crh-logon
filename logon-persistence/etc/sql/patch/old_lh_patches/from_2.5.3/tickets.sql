prompt
prompt ************************************************************************
prompt *** Ticket specific DB patch changes
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

prompt ************************************************************************
prompt *** BEGIN LOGON-9471 : Display LDS_DeliveryNote_No and Sequential_No in order details
prompt ************************************************************************

prompt Adding new function to Edit LDS delivery note number
insert into tlgnu_func (func_id, func_name, func_descr) VALUES ('11125', 'Order Detail Material LDS orderNo', 'Order Detail Material: Edit LDS Delivery note number.');
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  23 /* LOGON Admin Lite     */,  11125);

prompt ************************************************************************
prompt *** END LOGON-9471 : Display LDS_DeliveryNote_No and Sequential_No in order details
prompt ************************************************************************

prompt ************************************************************************
prompt *** BEGIN LOGON-9470 : LRQ xxx.x - Apply the "lead time" logic for web order in order details screen for customer users for Road Direct and Bimodal Road delivery types
prompt ************************************************************************

prompt Adding new function to Check Lead Time
insert into tlgnu_func (func_id, func_name, func_descr) VALUES ('11003', 'Order Detail Overrule Lead Time Check', 'Order Detail: Overrule Lead Time Check');
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  11003);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */,  11003);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Order Entry         */,  11003);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  23 /* LOGON Admin Lite     */,  11003);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  26 /* Del. Time Adjust.   */,  11003);

prompt ************************************************************************
prompt *** END LOGON-9470 : LRQ xxx.x - Apply the "lead time" logic for web order in order details screen for customer users for Road Direct and Bimodal Road delivery types
prompt ************************************************************************

prompt ************************************************************************
prompt *** BEGIN LOGON-9541 : Scheduling road: Customer order header changes
prompt ************************************************************************

prompt drop view VLGNC_DISPO_CUSTOR_SCHED_INFO;

prompt ************************************************************************
prompt *** END LOGON-9541 : Scheduling road: Customer order header changes
prompt ************************************************************************

prompt ************************************************************************
prompt *** BEGIN LOGON-9469 : LRQ 005.x? Customer Credit Block - Prevent saving of customer order depending on delivery type configuration
prompt ************************************************************************

ALTER TABLE TLGNR_DELIVERY_TYPE_BS ADD APPLY_BP_BLOCK_CHECK NUMBER(1,0) NULL;

UPDATE TLGNR_DELIVERY_TYPE_BS
SET APPLY_BP_BLOCK_CHECK = 0
WHERE DELIVERY_TYPE_CD IN (9,10);

UPDATE TLGNR_DELIVERY_TYPE_BS
SET APPLY_BP_BLOCK_CHECK = 1
WHERE DELIVERY_TYPE_CD NOT IN (9,10);

ALTER TABLE TLGNR_DELIVERY_TYPE_BS MODIFY APPLY_BP_BLOCK_CHECK NUMBER(1,0) NOT NULL;

prompt ************************************************************************
prompt *** END LOGON-9469 : LRQ 005.x? Customer Credit Block - Prevent saving of customer order depending on delivery type configuration
prompt ************************************************************************
prompt
