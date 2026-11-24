-- test vendors based on mail Vendors 09/14/11 17:41

-- Inserting Carrier for ITC1 Testing Cycle
-- Source Roland Schuler
-- sap_vendor_id starting from 2000


prompt '---------- fbnl_testdata/tlgns_sap_vendor.sql ----------'

prompt INSERTING into TLGNS_SAP_VENDOR
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, name1, 
country_cd, postal_code, city, location_district, street, house_number, lang_cd,
vn_is_comm_carrier, vn_is_exec_carrier, vn_is_internal,
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-2000, 'ITC1_757',	'ITC1_757',	'ZNOR', 'TRANSPORTBETON NV',
'BE', '1081',	'Brussel',		'BE', 'Example', '1', 'en',
1, 1, 0,
1, systimestamp, 1, systimestamp, 0);

insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, name1, 
country_cd, postal_code, city, location_district, street, house_number, lang_cd,
vn_is_comm_carrier, vn_is_exec_carrier, vn_is_internal,
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-2001, 'ITC1_813',	'ITC1_813',	'ZNOR', 'DOUCY TRANSPORTS',
'FR', '59800',	'Lille',	'FR', 'Example', '1', 'en',
1, 1, 0,
1, systimestamp, 1, systimestamp, 0);

insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, name1, 
country_cd, postal_code, city, location_district, street, house_number, lang_cd,
vn_is_comm_carrier, vn_is_exec_carrier, vn_is_internal,
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-2002, 'ITC1_860',	'ITC1_860',	'ZNOR', 'TRANSEST',
'FR', '59800',	'Lille',	'FR', 'Example', '1', 'en',
1, 1, 0,
1, systimestamp, 1, systimestamp, 0);

insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, name1, 
country_cd, postal_code, city, location_district, street, house_number, lang_cd,
vn_is_comm_carrier, vn_is_exec_carrier, vn_is_internal,
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-2003, 'ITC1_873',	'ITC1_873',	'ZNOR', 'LIMPENS JW ZN BV',
'NL', '1000',	'Amsterdamm','NL', 'Example', '1', 'en',
1, 1, 0,
1, systimestamp, 1, systimestamp, 0);

insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, name1, 
country_cd, postal_code, city, location_district, street, house_number, lang_cd,
vn_is_comm_carrier, vn_is_exec_carrier, vn_is_internal,
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-2004, 'ITC1_1010', 'ITC1_1010', 'ZNOR', 'TRANSPORTS LAMORY',
'FR', '59800',	'Lille',	'FR', 'Example', '1', 'en',
1, 1, 0,
1, systimestamp, 1, systimestamp, 0);

insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, name1, 
country_cd, postal_code, city, location_district, street, house_number, lang_cd,
vn_is_comm_carrier, vn_is_exec_carrier, vn_is_internal,
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-2005, 'ITC1_1074',	'ITC1_1074', 'ZNOR', 'DE LUYKER NV',
'BE', '1081',	'Brussel',	'BE', 'Example', '1', 'en',
1, 1, 0,
1, systimestamp, 1, systimestamp, 0);

insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, name1, country_cd,
postal_code, city, location_district, street, house_number, lang_cd,
vn_is_comm_carrier, vn_is_exec_carrier, vn_is_internal,
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-2006, 'ITC1_1095', 'ITC1_1095', 'ZNOR', 'DE DIJCKER NV',
'BE', '1081',	'Brussel',	'BE', 'Example', '1', 'en',
1, 1, 0,
1, systimestamp, 1, systimestamp, 0);

commit;