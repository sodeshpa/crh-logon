-- Testdata does not seem to have any other purpose then just to have an entry in an otherwise empty DB, for user 'markus'.
-- Because there are now always realy soldto/shipto restriction, this script is actually not really required anymore.
-- The original soldo_sap_bp_i=55662 (random Logon reference! different on productive dump!!)
-- seems to be a random customer, with the actual SAP Number 130303 (bp130303)
prompt INSERTING into TLGNU_USER_SOLDTO_BP_ACC - AdNovum Testdata only!

insert into tlgnu_user_soldto_bp_acc (
 user_soldto_bp_acc_id,
 user_id            ,
 soldto_sap_bp_id   ,
 ctl_cre_uid           ,
 ctl_cre_ts             ,
 ctl_mod_uid             ,
 ctl_mod_ts               ,
 ctl_tcn                  
)
( select -1000,-14, bp.sap_bp_id, 1, sysdate, 1, sysdate, 0 from tlgns_sap_bp bp where bp.sap_id='130303');

commit;
