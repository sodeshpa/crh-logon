-- Persons
Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
    -51,'LogonAdmin ADN AD01','AdNovum','+41 44 272 61 11',null,'pegasus-dev@adnovum.ch',1);
    
Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
    -52,'LogonAdmin ADN AD02','AdNovum','+41 44 272 61 11',null,'pegasus-dev@adnovum.ch',1);
    
Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
    -53,'LogonAdmin ADN ZA01','AdNovum','+41 44 272 61 11',null,'pegasus-dev@adnovum.ch',1);
    
-- Users
Insert into TLGNU_USER 
  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
  -51,  -51, 0, null, null,
  1,'logonadmin_ad01','logonadmin_ad01','logonadmin_ad01@CRH.CH', null, null, null,null, 'en', 0);
  
Insert into TLGNU_USER 
  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
  -52,  -52, 0, null, null,
  1,'logonadmin_ad02','logonadmin_ad02','logonadmin_ad02@CRH.CH', null, null, null,null, 'en', 0);

Insert into TLGNU_USER 
  (USER_ID,PERSON_ID,USER_TYPE_CD,SAP_BP_ID,SAP_VENDOR_ID,
  EXT_LOGIN_ACTIVE_FLAG,EXT_LOGIN,EXT_LOGIN_PWD,LDAP_HOLSPACE,
  PDA_USER_NO,PDA_USER_RFIDOBJECT_ID,PDA_PIN,PDA_PIN_VALID_TO_DAT, APPLICATION_LANG_CD, SEND_DAILY_REPORTS_FLAG) values (
  -53,  -53, 0, null, null,
  1,'logonadmin_za01','logonadmin_za01','logonadmin_za01@CRH.CH', null, null, null,null, 'en', 0);

-- Company Codes  
insert into tlgnu_user_companycode_acc (user_companycode_acc_id, user_id,
  companycode_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn 
) values (slgnu_user_companycode_acc.nextval, -51, 'AD01', 1, sysdate, 1, sysdate, 0);

insert into tlgnu_user_companycode_acc (user_companycode_acc_id, user_id,
  companycode_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn 
) values (slgnu_user_companycode_acc.nextval, -52, 'AD02', 1, sysdate, 1, sysdate, 0);

insert into tlgnu_user_companycode_acc (user_companycode_acc_id, user_id,
  companycode_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn 
) values (slgnu_user_companycode_acc.nextval, -53, 'ZA01', 1, sysdate, 1, sysdate, 0);


-- Country

insert into tlgnu_user_region_acc (user_region_acc_id, user_id,
  region_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn 
) values (-51, -51, 'AD', 1, sysdate, 1, sysdate, 0);

insert into tlgnu_user_region_acc (user_region_acc_id, user_id,
  region_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn 
) values (-52, -52, 'AD', 1, sysdate, 1, sysdate, 0);

insert into tlgnu_user_region_acc (user_region_acc_id, user_id,
  region_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn 
) values (-53, -53, 'ZA', 1, sysdate, 1, sysdate, 0);


-- Pools
insert into tlgnu_user_pool_acc (user_pool_acc_id, user_id,
  pool_id, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn 
)
( select slgnu_user_pool_acc.nextval, -51, pool_id, 1, sysdate, 1, sysdate, 0
  from tlgnc_pool_tx
  where lang_cd = 'en' and name like '%Andorra%'
);

insert into tlgnu_user_pool_acc (user_pool_acc_id, user_id,
  pool_id, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn 
)
( select slgnu_user_pool_acc.nextval, -52, pool_id, 1, sysdate, 1, sysdate, 0
  from tlgnc_pool_tx
  where lang_cd = 'en' and name like '%Andorra%'
);

insert into tlgnu_user_pool_acc (user_pool_acc_id, user_id,
  pool_id, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn 
)
( select slgnu_user_pool_acc.nextval, -53, pool_id, 1, sysdate, 1, sysdate, 0
  from tlgnc_pool_tx
  where lang_cd = 'en' and name like '%South Africa%'
);

-- Roles
insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
( select SLGNU_USER_ROLE.nextval, -51, role_id
  from tlgnu_role
);

insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
( select SLGNU_USER_ROLE.nextval, -52, role_id
  from tlgnu_role
);

insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
( select SLGNU_USER_ROLE.nextval, -53, role_id
  from tlgnu_role
);