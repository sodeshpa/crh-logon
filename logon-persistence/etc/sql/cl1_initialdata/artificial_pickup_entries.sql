-- $Revision$
-- $Date$
--
--Origin of this: Ticket ADN75373

-- The IDs must correspond to the ones used in JAVA, i.e.
--   ARTIFICIAL_PICKUP_CARRIER_ID
--   ARTIFICIAL_PICKUP_POOL_ID
--   ARTIFICIAL_PICKUP_BAG_TRUCK_ID
--   ARTIFICIAL_PICKUP_BULK_TRUCK_ID
-- see refdata/artificial_pickup_entries.sql

-- Vendor (1), Carrier (1), Pool (10), Pool_Tx, Pool_Carrier, Truck : see refdata/artificial_pickup_entries

prompt Connect pickup carrier: 1 vendor_porg per porg of cluster + 1 comm + 1 exec carrier partner function
-- reason: LOGON-5255, prevent from getting inactivated, BUT also, because data authorization may require it!

-- we use sequences here, because it makes the statements so much shorter, and no manual number management needed.
-- Note that this is NOT testdata, therefore, IDs in the POSITIVE range definitely make sense!
insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, last_successful_hjid, last_failed_hjid, marked_for_deletion, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
( select slgns_sap_vendor_porg.nextval, 1, purchasingorg_cd, null, null, 0, 1, systimestamp, 1, systimestamp, 0
   from tlgnr_purchasingorg_bs where purchasingorg_cd in ('CE01', 'CH01', 'HG01', 'IT01', 'NG01', 'SG01')
);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
( select sap_vendor_porg_id, sap_vendor_porg_id, purchasingorg_cd,  1, systimestamp, 1, systimestamp, 0
   from tlgns_sap_vendor_porg where sap_vendor_id = 1 and purchasingorg_cd in ('CE01', 'CH01', 'HG01', 'IT01', 'NG01', 'SG01')
);
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
( select sap_vendor_porg_id, sap_vendor_porg_id, purchasingorg_cd,  1, systimestamp, 1, systimestamp, 0
   from tlgns_sap_vendor_porg where sap_vendor_id = 1 and purchasingorg_cd in ('CE01', 'CH01', 'HG01', 'IT01', 'NG01', 'SG01')
);

prompt Connect generic carrier: 1 vendor_porg per porg of cluster + 1 comm + 0 exec carrier partner function
-- LOGON-5132
insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, last_successful_hjid, last_failed_hjid, marked_for_deletion, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
( select slgns_sap_vendor_porg.nextval, 2, purchasingorg_cd, null, null, 0, 1, systimestamp, 1, systimestamp, 0
   from tlgnr_purchasingorg_bs where purchasingorg_cd in ('CE01', 'CH01', 'HG01', 'IT01', 'NG01', 'SG01')
);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
( select sap_vendor_porg_id, sap_vendor_porg_id, purchasingorg_cd,  1, systimestamp, 1, systimestamp, 0
   from tlgns_sap_vendor_porg where sap_vendor_id = 2 and purchasingorg_cd in ('CE01', 'CH01', 'HG01', 'IT01', 'NG01', 'SG01')
);

commit;
