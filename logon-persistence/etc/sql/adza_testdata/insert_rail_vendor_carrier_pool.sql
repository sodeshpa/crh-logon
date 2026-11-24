-- Some additional RAIL related initial/testdata
-- - vendor
-- - carrier
-- - pool
-- - pool-carrier
-- All based on CHE prod DB, with "1:-1" mapping & renaming: Prefix with ADN, and multiply IDs with -1 (=> identical ID, but negative)
-- (Easiest way to add more necessary data that automatically fits later on)
--
-- Origin of data: Constructed 18.8.2011 for LOGON 2.0 EBM ADZA.
--
-- The truck vendors (together with carriers, their pools and pool-carrier are currently (historically)
-- done in 'minimal_setup.sql'. Keep there, unless there is a need to add many more, or modify.


prompt '---------- adza_testdata/insert_rail_vendor_carrier_pool.sql ----------'

prompt INSERTING into TLGNS_SAP_VENDOR

prompt tlgns_sap_vendor: ADN83275 - ADN Cargo AG (Ex SBB)

insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion,
  name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,
  vn_is_comm_carrier, vn_is_exec_carrier, vn_is_internal,
  ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1064, 'ADN83275', 'ADN0083275', 'ZNOR', '0',
  'ADN Cargo AG (Ex SBB)', null, 'CH', '4051', 'BASEL', null, 'BS', 'Centralbahnstrasse', '4', 'de', '495381',
  1, 1, 0,
  1, systimestamp, 1, systimestamp, 0);

prompt tlgns_sap_vendor: ADN82495 - ADN Cargo AG (Ex SBB) - TTC-OFF

insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion,
  name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,
  vn_is_comm_carrier, vn_is_exec_carrier, vn_is_internal,
  ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1065, 'ADN82495', 'ADN0082495', 'ZNOR', '0',
  'ADN Cargo AG (Ex SBB) - TTC-OFF', null, 'CH', '1700', 'FRIBOURG', null, 'FR', 'Rue de la Carriére', '2a', 'de', '495381',
  1, 1, 0,
  1, systimestamp, 1, systimestamp, 0);

prompt tlgnc_carrier: -1064 - AdNovum SBB TTC-ON
insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1064, 'AdNovum SBB TTC-ON', 1, -1064, null, 1, 0, null, 1, systimestamp, 1, systimestamp, 0);

prompt tlgnc_carrier: -1065 - AdNovum SBB TTC-OFF
insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1065, 'AdNovum SBB TTC-OFF', 1, -1065, null, 0, 0, null, 1, systimestamp, 1, systimestamp, 0);

prompt tlgnc_pool/_tx: -7 - Pool Eisenbahn Normalspur
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-7, '50', 1, 1, systimestamp, 1, systimestamp, 0);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-7, 'de', 'ADZA Eisenbahn Normalspur'     , 1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-7, 'en', 'ADZA Rail Standard Gauge'      , 1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-7, 'fr', 'ADZA Rail écartement normal'   , 1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-7, 'it', 'ADZA Ferrovia scartamento nor.', 1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-7, 'es', 'ADZA Rail Standard Gauge (es)' , 1, systimestamp, 1, systimestamp, 0);

prompt tlgnc_pool_carrier: -7 <-> AdNovum SBB TTC-ON
insert into tlgnc_pool_carrier (pool_carrier_id, pool_id, carrier_id, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1068, -7, -1064, 1, systimestamp, 1, systimestamp, 0);

prompt tlgnc_pool_carrier: -7 <-> AdNovum SBB TTC-OFF
insert into tlgnc_pool_carrier (pool_carrier_id, pool_id, carrier_id, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1641, -7, -1065, 1, systimestamp, 1, systimestamp, 0);

-- If a vendor is a "commercial" or "executing" carrier, it should have partner functions defined. Also, in data authorization maybe necessary later.
-- For this, first add all PORG of andorra/southafrica:
prompt tlgns_sap_vendor_porg: Invented by AdNovum: for all AD/ZA purchasing organisations.

-- we use sequences here, because it makes the statements so much shorter, and no manual number management needed.
-- They are no longer in the negative range, but nothing gets broken due to this.
insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, last_successful_hjid, last_failed_hjid, marked_for_deletion, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
( select slgns_sap_vendor_porg.nextval, -1064, purchasingorg_cd, null, null, 0, 1, systimestamp, 1, systimestamp, 0
   from tlgnr_purchasingorg_bs where purchasingorg_cd in ('AD01', 'AD02', 'ADRF', 'ZA01')
);
insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, last_successful_hjid, last_failed_hjid, marked_for_deletion, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
( select slgns_sap_vendor_porg.nextval, -1065, purchasingorg_cd, null, null, 0, 1, systimestamp, 1, systimestamp, 0
   from tlgnr_purchasingorg_bs where purchasingorg_cd in ('AD01', 'AD02', 'ADRF', 'ZA01')
);

prompt tlgns_sap_sap_vn_pf_comm_carrier: Invented by AdNovum: for all AD/ZA purchasing organisations for 2 rail carriers

insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
( select sap_vendor_porg_id, sap_vendor_porg_id, purchasingorg_cd,  1, systimestamp, 1, systimestamp, 0
   from tlgns_sap_vendor_porg where sap_vendor_id = -1064 and purchasingorg_cd in ('AD01', 'AD02', 'ADRF', 'ZA01')
);

insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
( select sap_vendor_porg_id, sap_vendor_porg_id, purchasingorg_cd,  1, systimestamp, 1, systimestamp, 0
   from tlgns_sap_vendor_porg where sap_vendor_id = -1065 and purchasingorg_cd in ('AD01', 'AD02', 'ADRF', 'ZA01')
);

prompt tlgns_sap_sap_vn_pf_exec_carrier: Invented by AdNovum: for all AD/ZA purchasing organisations for 2 rail carriers

insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
( select sap_vendor_porg_id, sap_vendor_porg_id, purchasingorg_cd,  1, systimestamp, 1, systimestamp, 0
   from tlgns_sap_vendor_porg where sap_vendor_id = -1064 and purchasingorg_cd in ('AD01', 'AD02', 'ADRF', 'ZA01')
);

insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
( select sap_vendor_porg_id, sap_vendor_porg_id, purchasingorg_cd,  1, systimestamp, 1, systimestamp, 0
   from tlgns_sap_vendor_porg where sap_vendor_id = -1065 and purchasingorg_cd in ('AD01', 'AD02', 'ADRF', 'ZA01')
);


prompt tlgns_sap_vendor: ADN Block Train AG

insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion,
  name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,
  vn_is_comm_carrier, vn_is_exec_carrier, vn_is_internal,
  ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1020, 'ADN0001020', 'ADN0001020', 'ZNOR', '0',
  'ADN Block Train AG', null, 'CH', '3000', 'BERN', null, 'FR', 'Brückfeldstrasse', '16', 'de', '495382',
  1, 1, 0,
  1, systimestamp, 1, systimestamp, 0);

prompt tlgns_sap_vendor: ADN Cargo AG - Ship

insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion,
  name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,
  vn_is_comm_carrier, vn_is_exec_carrier, vn_is_internal,
  ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1030, 'ADN0001030', 'ADN0001030', 'ZNOR', '0',
  'ADN Sea Freight AG',null, 'CH', '4057', 'BASEL', null, 'BS', 'Hafenstrasse', '1', 'de', '495383',
  1, 1, 0,
  1, systimestamp, 1, systimestamp, 0);

prompt tlgnc_carrier: -1020 - AdNovum Block Train
insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1020, 'AdNovum Block Train', 1, -1020, null, 0, 0, null, 1, systimestamp, 1, systimestamp, 0);


prompt tlgnc_carrier: -1030 - AdNovum Sea Freight
insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1030, 'AdNovum Sea Freight', 1, -1030, null, 0, 0, null, 1, systimestamp, 1, systimestamp, 0);


prompt tlgnc_pool/_tx: -1020 - Pool block train
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1020, '20', 1, 1, systimestamp, 1, systimestamp, 0);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-1020, 'de', 'ADZA Blockzug'     , 1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-1020, 'en', 'ADZA Block train'      , 1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-1020, 'fr', 'ADZA Train en bloc'   , 1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-1020, 'it', 'ADZA Ferrovia in blocco', 1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-1020, 'es', 'ADZA Tren en bloque' , 1, systimestamp, 1, systimestamp, 0);

prompt tlgnc_pool_carrier: -1020 <-> AdNovum Block Train AG
insert into tlgnc_pool_carrier (pool_carrier_id, pool_id, carrier_id, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1020, -1020, -1020, 1, systimestamp, 1, systimestamp, 0);

prompt tlgnc_pool/_tx: -1030 - Pool ship
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1030, '30', 1, 1, systimestamp, 1, systimestamp, 0);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-1030, 'de', 'ADZA Schiff'     , 1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-1030, 'en', 'ADZA Ship'      , 1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-1030, 'fr', 'ADZA Navire'   , 1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-1030, 'it', 'ADZA Nave', 1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-1030, 'es', 'ADZA Nave' , 1, systimestamp, 1, systimestamp, 0);

prompt tlgnc_pool_carrier: -1030 <-> AdNovum Sea Freight AG
insert into tlgnc_pool_carrier (pool_carrier_id, pool_id, carrier_id, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1030, -1030, -1030, 1, systimestamp, 1, systimestamp, 0);

-- If a vendor is a "commercial" or "executing" carrier, it should have partner functions defined. Also, in data authorization maybe necessary later.
-- For this, first add all PORG of andorra/southafrica for the BOAT and BLOCK TRAIN vendors:
prompt tlgns_sap_vendor_porg: Invented by AdNovum: for all AD/ZA purchasing organisations - Block Train / Sea Freight.

-- we use sequences here, because it makes the statements so much shorter, and no manual number management needed.
-- They are no longer in the negative range, but nothing gets broken due to this.
insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, last_successful_hjid, last_failed_hjid, marked_for_deletion, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
( select slgns_sap_vendor_porg.nextval, -1020, purchasingorg_cd, null, null, 0, 1, systimestamp, 1, systimestamp, 0
   from tlgnr_purchasingorg_bs where purchasingorg_cd in ('AD01', 'AD02', 'ADRF', 'ZA01')
);
insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, last_successful_hjid, last_failed_hjid, marked_for_deletion, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
( select slgns_sap_vendor_porg.nextval, -1030, purchasingorg_cd, null, null, 0, 1, systimestamp, 1, systimestamp, 0
   from tlgnr_purchasingorg_bs where purchasingorg_cd in ('AD01', 'AD02', 'ADRF', 'ZA01')
);

prompt tlgns_sap_sap_vn_pf_comm_carrier: Invented by AdNovum: for all AD/ZA purchasing organisations for 2 Block Train / Sea Freight carriers

insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
( select sap_vendor_porg_id, sap_vendor_porg_id, purchasingorg_cd,  1, systimestamp, 1, systimestamp, 0
   from tlgns_sap_vendor_porg where sap_vendor_id = -1020 and purchasingorg_cd in ('AD01', 'AD02', 'ADRF', 'ZA01')
);

insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
( select sap_vendor_porg_id, sap_vendor_porg_id, purchasingorg_cd,  1, systimestamp, 1, systimestamp, 0
   from tlgns_sap_vendor_porg where sap_vendor_id = -1030 and purchasingorg_cd in ('AD01', 'AD02', 'ADRF', 'ZA01')
);

prompt tlgns_sap_sap_vn_pf_exec_carrier: Invented by AdNovum: for all AD/ZA purchasing organisations for 2 Block Train / Sea Freight carriers

insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
( select sap_vendor_porg_id, sap_vendor_porg_id, purchasingorg_cd,  1, systimestamp, 1, systimestamp, 0
   from tlgns_sap_vendor_porg where sap_vendor_id = -1020 and purchasingorg_cd in ('AD01', 'AD02', 'ADRF', 'ZA01')
);

insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
( select sap_vendor_porg_id, sap_vendor_porg_id, purchasingorg_cd,  1, systimestamp, 1, systimestamp, 0
   from tlgns_sap_vendor_porg where sap_vendor_id = -1030 and purchasingorg_cd in ('AD01', 'AD02', 'ADRF', 'ZA01')
);

commit;
