-- 2012.08.12: Commented out roles since LOGON-4668: 11, 14, 15, 17, 19.

prompt INSERTING into TLGNC_PERSON - AdNovum Testdata only!
prompt If you see this message appearing within Crh, you will have a SECURITY ISSUE.

---- Person data of AdNovum Test users

-- Range below 1000 is reserved for technical purposes.
-- Negative IDs for TEST-DATA: This way we will never have conflict with a real DB from Crh.
-- User testdata can be loaded also on a DB dump we receive from the productive system.

-- Standard login for all roles, used by dev (not interested in authorization aspects)
Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
    -11,'LogonAdmin ADN','AdNovum','+41 44 272 61 11',null,'logon-dev@adnovum.ch',1);

-- Used for SPNEGO (Kerberos) and security integration tests in AdNovum
Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
    -12,'Bond ADN','James','+41 44 272 61 11', null, 'zweifel@adnovum.ch', 1);

-- ancsa - initially also all roles, meant to play around with it.
Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
    -13,'Lakatos ADN','Aniko','+41 44 272 61 11', null, 'ancsa@adnovum.ch', 1);

-- markus - for testing user authorization / data access restrictions
Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
    -14,'Zemp ADN','Markus','+41 44 272 61 11', null, 'markus@adnovum.ch', 1);

-- As LogonAdmin, but DE : used by dev (not interested in authorization aspects).
Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
    -15,'LogonAdmin (de) ADN','AdNovum','+41 44 272 61 11',null,'logon-dev@adnovum.ch',1);

-- As LogonAdmin, but FR : used by dev (not interested in authorization aspects).
Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
    -16,'LogonAdmin (fr) ADN','AdNovum','+41 44 272 61 11',null,'logon-dev@adnovum.ch',1);

-- As LogonAdmin, but IT : used by dev (not interested in authorization aspects).
Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
    -17,'LogonAdmin (it) ADN','AdNovum','+41 44 272 61 11',null,'logon-dev@adnovum.ch',1);

-- Person/User which have one role exactly (for authorization tests)
Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
    -101,'Role 1','Logon Admin','+41 44 272 61 11', null, 'logon-dev@adnovum.ch', 1);
Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
    -102,'Role 2','User Admin','+41 44 272 61 11', null, 'logon-dev@adnovum.ch', 1);
Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
    -103,'Role 3','Sales Admin','+41 44 272 61 11', null, 'logon-dev@adnovum.ch', 1);
Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
    -104,'Role 4','Road Admin','+41 44 272 61 11', null, 'logon-dev@adnovum.ch', 1);
Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
    -105,'Role 5','Rail Admin','+41 44 272 61 11', null, 'logon-dev@adnovum.ch', 1);
Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
    -106,'Role 6','Auftragsannahme','+41 44 272 61 11', null, 'logon-dev@adnovum.ch', 1);
Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
    -107,'Role 7','Disposition Strasse','+41 44 272 61 11', null, 'logon-dev@adnovum.ch', 1);
Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
    -108,'Role 8','Disposition Bahn','+41 44 272 61 11', null, 'logon-dev@adnovum.ch', 1);
Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
    -109,'Role 9','Spedition Strasse','+41 44 272 61 11', null, 'logon-dev@adnovum.ch', 1);
Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
    -110,'Role 10','Spedition Bahn','+41 44 272 61 11', null, 'logon-dev@adnovum.ch', 1);
--Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
--    -111,'Role 11','Bahnwagenplanung','+41 44 272 61 11', null, 'logon-dev@adnovum.ch', 1);
Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
    -112,'Role 12','Kunde','+41 44 272 61 11', null, 'logon-dev@adnovum.ch', 1);
Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
    -113,'Role 13','Chauffeur','+41 44 272 61 11', null, 'logon-dev@adnovum.ch', 1);
--Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
--    -114,'Role 14','Staplerfahrer','+41 44 272 61 11', null, 'logon-dev@adnovum.ch', 1);
--Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
--    -115,'Role 15','RFID-Admin','+41 44 272 61 11', null, 'logon-dev@adnovum.ch', 1);
Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
    -116,'Role 16','Poolführer','+41 44 272 61 11', null, 'logon-dev@adnovum.ch', 1);
--Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
--    -117,'Role 17','PDA-Admin','+41 44 272 61 11', null, 'logon-dev@adnovum.ch', 1);
Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
    -118,'Role 18','Management','+41 44 272 61 11', null, 'logon-dev@adnovum.ch', 1);
--Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
--    -119,'Role 19','Test','+41 44 272 61 11', null, 'logon-dev@adnovum.ch', 1);
Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
    -120,'Role 20','Scheduling Display/(Readonly)','+41 44 272 61 11', null, 'logon-dev@adnovum.ch', 1);
Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
    -121,'Role 21','Bahnwagen instandhaltung','+41 44 272 61 11', null, 'logon-dev@adnovum.ch', 1);
Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
    -122,'Role 22','External scheduler','+41 44 272 61 11', null, 'logon-dev@adnovum.ch', 1);
Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
    -123,'Role 23','LOGON Admin Lite','+41 44 272 61 11', null, 'logon-dev@adnovum.ch', 1);
Insert into TLGNC_PERSON (PERSON_ID,NAME,FIRSTNAME,PHONE_NR,MOBILE_NR,EMAIL,ACTIVE_FLAG) values (
    -124,'Role 24','User Admin Lite','+41 44 272 61 11', null, 'logon-dev@adnovum.ch', 1);


-- Initial other persons (contact persons etc)
-- Insert here if needed.

commit;
