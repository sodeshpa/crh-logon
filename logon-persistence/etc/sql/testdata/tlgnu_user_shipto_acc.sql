-- Testdata does not seem to have any other purpose then just to have an entry in an otherwise empty DB, for user 'markus'.
-- Because there are now always realy soldto/shipto restriction, this script is actually not really required anymore.
-- The original shipto_sap_bp_id=34201 (random Logon reference! different on productive dump!!)
-- seems to be a random shipto, with the actual SAP Number 1369895 (Jardin de Belmont)
prompt INSERTING into TLGNU_USER_SHIPTO_ACC - AdNovum Testdata only!


insert into tlgnu_user_shipto_acc (
 USER_SHIPTO_ACC_ID,
 USER_ID            ,
 SHIPTO_SAP_BP_ID    ,
 USER_SOLDTO_BP_ACC_ID,
 CTL_CRE_UID           ,
 CTL_CRE_TS             ,
 CTL_MOD_UID             ,
 CTL_MOD_TS               ,
 CTL_TCN                  
)
( select -1000, -14, bp.sap_bp_id, -1000, 1, sysdate, 1, sysdate, 0 from tlgns_sap_bp bp where bp.sap_id='1369895');
commit;
