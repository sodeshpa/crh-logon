-- 2012.08.12: Commented out roles since LOGON-4668: 11, 14, 15, 17, 19.
prompt INSERTING into TLGNU_USER_ROLE - AdNovum Testdata only!
prompt If you see this message appearing within Crh, you will have a SECURITY ISSUE.

---- AdNovum Test users

-- logonadmin - all roles
insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
( select SLGNU_USER_ROLE.nextval, -11, role_id
  from tlgnu_role
);

-- jamesbond - also all roles, but in a format which can easily be reused for other users which will not have all roles
insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
( select SLGNU_USER_ROLE.nextval, -12, role_id
  from tlgnu_role
  where role_id in (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13, 16, 18, 20, 21, 22, 23, 24)
);

-- ancsa - initially also all roles, meant to play around with it.
insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
( select SLGNU_USER_ROLE.nextval, -13, role_id
  from tlgnu_role
  where role_id in (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13, 16, 18, 20, 21, 22, 23, 24)
);

-- markus - for testing user authorization / data access restrictions
insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
( select SLGNU_USER_ROLE.nextval, -14, role_id
  from tlgnu_role
  where role_id in (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13, 16, 18, 20, 21, 22, 23, 24)
);

-- logonadmin_de - all roles
insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
( select SLGNU_USER_ROLE.nextval, -15, role_id
  from tlgnu_role
);

-- logonadmin_fr - all roles
insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
( select SLGNU_USER_ROLE.nextval, -16, role_id
  from tlgnu_role
);

-- logonadmin_it - all roles
insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
( select SLGNU_USER_ROLE.nextval, -17, role_id
  from tlgnu_role
);


-- Special users which have exactly one role (to test authorization)
insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
  ( select SLGNU_USER_ROLE.nextval, -101, role_id from tlgnu_role where role_id in (1) );
insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
  ( select SLGNU_USER_ROLE.nextval, -102, role_id from tlgnu_role where role_id in (2) );
insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
  ( select SLGNU_USER_ROLE.nextval, -103, role_id from tlgnu_role where role_id in (3) );
insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
  ( select SLGNU_USER_ROLE.nextval, -104, role_id from tlgnu_role where role_id in (4) );
insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
  ( select SLGNU_USER_ROLE.nextval, -105, role_id from tlgnu_role where role_id in (5) );
insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
  ( select SLGNU_USER_ROLE.nextval, -106, role_id from tlgnu_role where role_id in (6) );
insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
  ( select SLGNU_USER_ROLE.nextval, -107, role_id from tlgnu_role where role_id in (7) );
insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
  ( select SLGNU_USER_ROLE.nextval, -108, role_id from tlgnu_role where role_id in (8) );
insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
  ( select SLGNU_USER_ROLE.nextval, -109, role_id from tlgnu_role where role_id in (9) );
insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
  ( select SLGNU_USER_ROLE.nextval, -110, role_id from tlgnu_role where role_id in (10) );
--insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
--  ( select SLGNU_USER_ROLE.nextval, -111, role_id from tlgnu_role where role_id in (11) );
insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
  ( select SLGNU_USER_ROLE.nextval, -112, role_id from tlgnu_role where role_id in (12) );
insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
  ( select SLGNU_USER_ROLE.nextval, -113, role_id from tlgnu_role where role_id in (13) );
--insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
--  ( select SLGNU_USER_ROLE.nextval, -114, role_id from tlgnu_role where role_id in (14) );
--insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
--  ( select SLGNU_USER_ROLE.nextval, -115, role_id from tlgnu_role where role_id in (15) );
insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
  ( select SLGNU_USER_ROLE.nextval, -116, role_id from tlgnu_role where role_id in (16) );
--insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
--  ( select SLGNU_USER_ROLE.nextval, -117, role_id from tlgnu_role where role_id in (17) );
insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
  ( select SLGNU_USER_ROLE.nextval, -118, role_id from tlgnu_role where role_id in (18) );
--insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
--  ( select SLGNU_USER_ROLE.nextval, -119, role_id from tlgnu_role where role_id in (19) );
insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
  ( select SLGNU_USER_ROLE.nextval, -120, role_id from tlgnu_role where role_id in (20) );
insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
  ( select SLGNU_USER_ROLE.nextval, -121, role_id from tlgnu_role where role_id in (21) );
insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
  ( select SLGNU_USER_ROLE.nextval, -122, role_id from tlgnu_role where role_id in (22) );
insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
  ( select SLGNU_USER_ROLE.nextval, -123, role_id from tlgnu_role where role_id in (23) );
insert into TLGNU_USER_ROLE (USER_ROLE_ID,USER_ID,ROLE_ID)
  ( select SLGNU_USER_ROLE.nextval, -124, role_id from tlgnu_role where role_id in (24) );
commit;
