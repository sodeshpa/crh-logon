-- These are 300 Vendor / Carrier / Porg + partnerfunctions, originally used for LOGON-5639, but possibly reusable for other loadtests.

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

-- Inserting 1 vendor + 1 carrier + 1 vendor_porg + 1 commercial carrier PF + 1 executing carrier PF. This block can be repeated 300 times...
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (slgns_sap_vendor.nextval, 'ADNV'||slgns_sap_vendor.currval, 'ADNV'||slgns_sap_vendor.currval, 'ZNOR', 0, 'Carrier'||slgns_sap_vendor.currval, null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', '22', 'de' ,null, 1, 1, 1);

insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag, carrier_requires_planning_flag, freight_revenue_currency_cd) 
values (slgnc_carrier.nextval, 'ADNV'||slgns_sap_vendor.currval, 1, slgns_sap_vendor.currval, null, 0, 0, null);

insert into tlgns_sap_vendor_porg (sap_vendor_porg_id, sap_vendor_id, purchasingorg_cd, marked_for_deletion) values (slgns_sap_vendor_porg.nextval, slgns_sap_vendor.currval, 'AD01', 0);
insert into tlgns_sap_vn_pf_comm_carrier (sap_vendor_porg_id, comm_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');
insert into tlgns_sap_vn_pf_exec_carrier (sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd) values (slgns_sap_vendor_porg.currval, slgns_sap_vendor_porg.currval, 'AD01');

commit;
