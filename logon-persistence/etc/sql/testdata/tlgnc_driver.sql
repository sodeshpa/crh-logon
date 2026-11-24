-- Status: Very minimal, for first rounds of testing (only main users)

prompt INSERTING into TLGNC_DRIVER

---- All main test users plus the users with the relevant roles are initially also drivers.

Insert into TLGNC_DRIVER (DRIVER_ID,CURRENT_TRUCK_ID,CERTIFIED_FROM_DAT,CERTIFIED_TO_DAT,ACTIVE_FLAG,DRIVER_COMMENT, USER_ID) values (-11,null,to_date('20060101','YYYYMMDD'),to_date('20211231','YYYYMMDD'),1,'User -11 is testuser logonadmin.', -11);
Insert into TLGNC_DRIVER (DRIVER_ID,CURRENT_TRUCK_ID,CERTIFIED_FROM_DAT,CERTIFIED_TO_DAT,ACTIVE_FLAG,DRIVER_COMMENT, USER_ID) values (-12,-202,to_date('20060101','YYYYMMDD'),to_date('20211231','YYYYMMDD'),1,'User -12 is testuser jamesbond', -12);
Insert into TLGNC_DRIVER (DRIVER_ID,CURRENT_TRUCK_ID,CERTIFIED_FROM_DAT,CERTIFIED_TO_DAT,ACTIVE_FLAG,DRIVER_COMMENT, USER_ID) values (-13,null,to_date('20060101','YYYYMMDD'),to_date('20211231','YYYYMMDD'),1,'User -13 is testuser ancsa', -13);

Insert into TLGNC_DRIVER (DRIVER_ID,CURRENT_TRUCK_ID,CERTIFIED_FROM_DAT,CERTIFIED_TO_DAT,ACTIVE_FLAG,DRIVER_COMMENT, USER_ID) values (-101,null,to_date('20060101','YYYYMMDD'),to_date('20211231','YYYYMMDD'),1,'User -101 is adn_role_1, LOGON Admin', -101);
Insert into TLGNC_DRIVER (DRIVER_ID,CURRENT_TRUCK_ID,CERTIFIED_FROM_DAT,CERTIFIED_TO_DAT,ACTIVE_FLAG,DRIVER_COMMENT, USER_ID) values (-113,null,to_date('20060101','YYYYMMDD'),to_date('20211231','YYYYMMDD'),1,'User -113 is adn_role_13, Chauffeur', -113);
-- Insert into TLGNC_DRIVER (DRIVER_ID,CURRENT_TRUCK_ID,CERTIFIED_FROM_DAT,CERTIFIED_TO_DAT,ACTIVE_FLAG,DRIVER_COMMENT, USER_ID) values (-114,null,to_date('20060101','YYYYMMDD'),to_date('20111231','YYYYMMDD'),1,'User -114 is adn_role_14, Staplerfahrer', -114);

commit;
