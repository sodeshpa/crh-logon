-- 2012.08.12: Commented out roles since LOGON-4668: 11, 14, 15, 17, 19.
prompt INSERTING into TLGNU_USER - AdNovum Testdata only!
prompt ERROR - If you see this message appearing within Crh, you will have a SECURITY ISSUE.

---- AdNovum Test users

-- Range below 1000 is reserved for technical purposes.
-- Negative IDs for TEST-DATA: This way we will never have conflict with a real DB from Crh.
-- User testdata can be loaded also on a DB dump we receive from the productive system.

-- PDA Login:
-- In test requests from Nufatron we received 120256. Adding to the main user for now.
-- Change if needed later, or add special users for PDA tests.
-- Note, that both User Number and PIN are (normally?) only number, BUT we have pin as text, because of possible leading zeros.
-- Do NOT use negative PDA User numbers, because that leads to possible duplicates in rfidobject.object_label.

-- Standard login for all roles, used by dev (not interested in authorization aspects)
Insert into TLGNU_USER 
  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
  -11,  -11, 0, null, null,
  1,'logonadmin','logonadmin','logonadmin@CRH.CH', 120256, null, '1234' ,null, 'en', 1 /* send daily reports for testing! */);

-- Used for SPNEGO (Kerberos) and security integration tests in AdNovum
Insert into TLGNU_USER
  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
  -12,  -12, 0, null, null,
  1,'jamesbond','jamesbond','jamesbond@CRH.CH', 200012, null, '1234', null, 'de', 0);

-- ancsa - initially also all roles, meant to play around with it.
Insert into TLGNU_USER
  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
  -13,  -13, 0, null, null,
  1,'ancsa','ancsa','ancsa@CRH.CH', 200013, null, '1234', null, 'de', 0);

-- markus - for testing user authorization / data access restrictions
Insert into TLGNU_USER
  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
  -14,  -14, 0, null, null,
  1,'markus','markus','markus@CRH.CH', 200014, null, '1234', null, 'de', 0);

-- Standard DE login for all roles, used by dev (not interested in authorization aspects)
Insert into TLGNU_USER 
  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
  -15,  -15, 0, null, null,
  1,'logonadmin_de','logonadmin_de','logonadmin_de@CRH.CH', 200015, null, '1234' ,null, 'de', 0);

-- Standard FR login for all roles, used by dev (not interested in authorization aspects)
Insert into TLGNU_USER 
  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
  -16,  -16, 0, null, null,
  1,'logonadmin_fr','logonadmin_fr','logonadmin_fr@CRH.CH', 200016, null, '1234' ,null, 'fr', 0);

-- Standard IT login for all roles, used by dev (not interested in authorization aspects)
Insert into TLGNU_USER 
  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
  -17,  -17, 0, null, null,
  1,'logonadmin_it','logonadmin_it','logonadmin_it@CRH.CH', 200017, null, '1234' ,null, 'it', 0);


-- Person/User which have one role exactly (for authorization tests)
Insert into TLGNU_USER
  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
  -101 ,-101, 0, null, null,
  1, 'adn_role_1', 'adn_role_1', 'adn_role_1@CRH.CH', 100001, null, '1234', null, 'de', 0);
Insert into TLGNU_USER
  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
  -102 ,-102, 0, null, null,
  1, 'adn_role_2', 'adn_role_2', 'adn_role_2@CRH.CH', 100002, null, '1234', null, 'de', 0);
Insert into TLGNU_USER
  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
  -103 ,-103, 0, null, null,
  1, 'adn_role_3', 'adn_role_3', 'adn_role_3@CRH.CH', 100003, null, '1234', null, 'de', 0);
Insert into TLGNU_USER
  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
  -104 ,-104, 0, null, null,
  1, 'adn_role_4', 'adn_role_4', 'adn_role_4@CRH.CH', 100004, null, '1234', null, 'de', 0);
Insert into TLGNU_USER
  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
  -105 ,-105, 0, null, null,
  1, 'adn_role_5', 'adn_role_5', 'adn_role_5@CRH.CH', 100005, null, '1234', null, 'de', 0);
Insert into TLGNU_USER
  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
  -106 ,-106, 0, null, null,
  1, 'adn_role_6', 'adn_role_6', 'adn_role_6@CRH.CH', 100006, null, '1234', null, 'de', 0);
Insert into TLGNU_USER
  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
  -107 ,-107, 0, null, null,
  1, 'adn_role_7', 'adn_role_7', 'adn_role_7@CRH.CH', 100007, null, '1234', null, 'de', 0);
Insert into TLGNU_USER
  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
  -108 ,-108, 0, null, null,
  1, 'adn_role_8', 'adn_role_8', 'adn_role_8@CRH.CH', 100008, null, '1234', null, 'de', 0);
Insert into TLGNU_USER
  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
  -109 ,-109, 0, null, null,
  1, 'adn_role_9', 'adn_role_9', 'adn_role_9@CRH.CH', 100009, null, '1234', null, 'de', 0);
Insert into TLGNU_USER
  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
  -110 ,-110, 0, null, null,
  1, 'adn_role_10', 'adn_role_10', 'adn_role_10@CRH.CH', 100010, null, '1234', null, 'de', 0);
--Insert into TLGNU_USER
--  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
--  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
--  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
--  -111 ,-111, 0, null, null,
--  1, 'adn_role_11', 'adn_role_11', 'adn_role_11@CRH.CH', 100011, null, '1234', null, 'de', 0);
Insert into TLGNU_USER
  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
  -112 ,-112, 0, null, null,
  1, 'adn_role_12', 'adn_role_12', 'adn_role_12@CRH.CH', 100012, null, '1234', null, 'de', 0);
Insert into TLGNU_USER
  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
  -113 ,-113, 0, null, null,
  1, 'adn_role_13', 'adn_role_13', 'adn_role_13@CRH.CH', 100013, null, '1234', null, 'de', 0);
--Insert into TLGNU_USER
--  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
--  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
--  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
--  -114 ,-114, 0, null, null,
--  1, 'adn_role_14', 'adn_role_14', 'adn_role_14@CRH.CH', 100014, null, '1234', null, 'de', 0);
--Insert into TLGNU_USER
--  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
--  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
--  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
--  -115 ,-115, 0, null, null,
--  1, 'adn_role_15', 'adn_role_15', 'adn_role_15@CRH.CH', 100015, null, '1234', null, 'de', 0);
Insert into TLGNU_USER
  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
  -116 ,-116, 0, null, null,
  1, 'adn_role_16', 'adn_role_16', 'adn_role_16@CRH.CH', 100016, null, '1234', null, 'de', 0);
-- Insert into TLGNU_USER
--  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
--  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
--  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
--  -117 ,-117, 0, null, null,
--  1, 'adn_role_17', 'adn_role_17', 'adn_role_17@CRH.CH', 100017, null, '1234', null, 'de', 0);
Insert into TLGNU_USER
  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
  -118 ,-118, 0, null, null,
  1, 'adn_role_18', 'adn_role_18', 'adn_role_18@CRH.CH', 100018, null, '1234', null, 'de', 0);
--Insert into TLGNU_USER
--  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
--  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
--  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
--  -119 ,-119, 0, null, null,
--  1, 'adn_role_19', 'adn_role_19', 'adn_role_19@CRH.CH', 100019, null, '1234', null, 'de', 0);
Insert into TLGNU_USER
  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
  -120 ,-120, 0, null, null,
  1, 'adn_role_20', 'adn_role_20', 'adn_role_20@CRH.CH', 100019, null, '1234', null, 'de', 0);
Insert into TLGNU_USER
  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
  -121 ,-121, 0, null, null,
  1, 'adn_role_21', 'adn_role_21', 'adn_role_21@CRH.CH', 100021, null, '1234', null, 'de', 0);
Insert into TLGNU_USER
  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
  -122 ,-122, 0, null, null,
  1, 'adn_role_22', 'adn_role_22', 'adn_role_22@CRH.CH', 100022, null, '1234', null, 'de', 0);
Insert into TLGNU_USER
  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
  -123 ,-123, 0, null, null,
  1, 'adn_role_23', 'adn_role_23', 'adn_role_23@CRH.CH', 100023, null, '1234', null, 'de', 0);
Insert into TLGNU_USER
  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
  -124 ,-124, 0, null, null,
  1, 'adn_role_24', 'adn_role_24', 'adn_role_24@CRH.CH', 100024, null, '1234', null, 'de', 0);
--- adn_role_12 is actually a "Customer", so it should be associated with a soldto.
-- LOGON 1.6 was using 56881, Toggenburger AG Beton-, Kies-, Trsp. Dispositon (it has a few shipto which Christoph also uses for testing)
-- Since LOGON 2.1: Use one of our own testdata BP.
prompt Update User: Link to customer 
update tlgnu_user set
  sap_bp_id = (select bp.sap_bp_id from tlgns_sap_bp bp where bp.sap_id = 'ADN73161')
  /* ctl_mod_ts = systimestamp, ctl_mod_uid=1, ctl_tcn = ctl_tcn+1 - no need to set, because not yet committed */
where
  ext_login = 'adn_role_12';

prompt Create user_soldto access control entry for this user.
insert into tlgnu_user_soldto_bp_acc (
  user_soldto_bp_acc_id,
  user_id,
  soldto_sap_bp_id,
  ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn 
)
( select slgnu_user_soldto_bp_acc.nextval, -112, bp.sap_bp_id,
  1, systimestamp, 1, systimestamp, 0
  from tlgns_sap_bp bp where bp.sap_id='ADN73161' or bp.sap_id='ADN73031');

--- adn_role_13 is actually a "Driver", so it should be associated with a vendor.
-- LOGON 1.6: was using 800040, Crh (Schweiz) AG (same as Markus Meier has)
-- Since LOGON 2.1: Use one of our own testdata carriers
prompt Update User: Link to vendor
update tlgnu_user set
  sap_vendor_id = (select v.sap_vendor_id from tlgns_sap_vendor v where v.sap_id = 'ADNTEST -1')
  /* ctl_mod_ts = systimestamp, ctl_mod_uid=1, ctl_tcn = ctl_tcn+1 - no need to set, because not yet committed */
where
  ext_login = 'adn_role_13';

prompt Create user_vendor access control entry for this user.
insert into tlgnu_user_sap_vendor_acc (
  user_sap_vendor_acc_id,
  user_id,
  sap_vendor_id,
  ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn 
)
( select slgnu_user_sap_vendor_acc.nextval, -113, v.sap_vendor_id,
  1, systimestamp, 1, systimestamp, 0
  from tlgns_sap_vendor v where v.sap_id='ADNTEST -1');

commit;
