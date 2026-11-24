-- test vendors based on mail Vendors 09/14/11 17:41

-- Inserting Carrier for ITC1 Testing Cycle
-- Source Roland Schuler 
-- carrier_id starting from 2000


prompt '---------- fbnl_testdata/tlgnc_carrier.sql ----------'

prompt INSERTING into TLGNC_CARRIER

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, transmit_to_carrier_flag, carrier_requires_planning_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-2000, 'TRANSPORTBETON NV',	1, -2000, 1, 0, 1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, transmit_to_carrier_flag, carrier_requires_planning_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-2001, 'DOUCY TRANSPORTS',	1, -2001, 0, 0, 1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, transmit_to_carrier_flag, carrier_requires_planning_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-2002, 'TRANSEST',			1, -2002, 0, 0, 1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, transmit_to_carrier_flag, carrier_requires_planning_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-2003, 'LIMPENS JW ZN BV',	1, -2003, 0, 0, 1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, transmit_to_carrier_flag, carrier_requires_planning_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-2004, 'TRANSPORTS LAMORY',	1, -2004, 0, 0, 1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, transmit_to_carrier_flag, carrier_requires_planning_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-2005, 'DE LUYKER NV',		1, -2005, 0, 0, 1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, transmit_to_carrier_flag, carrier_requires_planning_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-2006, 'DE DIJCKER NV',		1, -2006, 0, 0, 1, systimestamp, 1, systimestamp, 0);

commit;