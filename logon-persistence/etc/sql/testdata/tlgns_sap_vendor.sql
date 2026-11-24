-- Those are fake, to have more than 1 "valid" Carrier. Do we still want them, or remove completely?
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (-11, 'ADNTEST -1',		'ADNTEST -1',	'ZNOR', 0, 'Carrier -1',	null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', 	'22', 'de' ,null,1,1,1);
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (-12, 'ADNTEST -2',		'ADNTEST -2',	'ZNOR', 0, 'Carrier -2',	null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', 	'16', 'fr', null,1,1,1);
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (-13, 'ADNTEST -3',		'ADNTEST -3',	'ZNOR', 0, 'Carrier -3',	null, 'CH', '8005', 'Zürich', 'ZH', null, 'Luisenstrasse', 	'6', 'en', null,1,1,1);


Insert into TLGNS_SAP_VENDOR_PORG (SAP_VENDOR_PORG_ID,SAP_VENDOR_ID,PURCHASINGORG_CD,MARKED_FOR_DELETION) values (-11,-11,'AD01',0);
Insert into TLGNS_SAP_VENDOR_PORG (SAP_VENDOR_PORG_ID,SAP_VENDOR_ID,PURCHASINGORG_CD,MARKED_FOR_DELETION) values (-12,-12,'AD01',0);
Insert into TLGNS_SAP_VENDOR_PORG (SAP_VENDOR_PORG_ID,SAP_VENDOR_ID,PURCHASINGORG_CD,MARKED_FOR_DELETION) values (-13,-13,'AD01',0);

Insert into TLGNS_SAP_VN_PF_EXEC_CARRIER (SAP_VENDOR_PORG_ID,EXEC_SAP_VENDOR_PORG_ID,PURCHASINGORG_CD) values (-11,-11,'AD01');
Insert into TLGNS_SAP_VN_PF_EXEC_CARRIER (SAP_VENDOR_PORG_ID,EXEC_SAP_VENDOR_PORG_ID,PURCHASINGORG_CD) values (-12,-12,'AD01');
Insert into TLGNS_SAP_VN_PF_EXEC_CARRIER (SAP_VENDOR_PORG_ID,EXEC_SAP_VENDOR_PORG_ID,PURCHASINGORG_CD) values (-13,-13,'AD01');

commit;
