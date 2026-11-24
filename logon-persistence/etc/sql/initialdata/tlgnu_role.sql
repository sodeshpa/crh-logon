-- $Revision$
-- $Date$
--
-- List is based on 2009-01-20_LOGON_BerechtiungkonzeptV1.2.6.xls
-- 20120824:
-- - Updated by /share/doc/crh/logon_2_1/input/LRQ_001.3_Authorizations_with_New_Org_Support_Concept/20120516_LRQ001.3_Authorizations_and_Role_Actualization_JJF_V2.1_Tab_Modifications.pdf
-- - Translated the Descriptions to English. Possibly also need to change the Name, check what's displayed in the UI.

-- Functions:
-- 9141      ; User Admin Grant role-set 1 Special               ; User Admin: Right to grant role-set 1 special roles (i.e. strong roles such as LOGON Admin/Lite, User Admin)
-- 9142      ; User Admin Grant role-set 2 Normal                ; User Admin: Right to grant role-set 2 normal roles (i.e. standard business roles)

prompt '--------- initialdata/tlgnu_role.sql ----------'

prompt INSERTING into TLGNU_ROLE
insert into tlgnu_role (role_id, role_name, role_descr, grantable_by_func_id) values (  1, 'LOGON Admin',                         'LOGON Admin',                         9141);
insert into tlgnu_role (role_id, role_name, role_descr, grantable_by_func_id) values (  2, 'User Admin',                          'User Admin',                          9141);
insert into tlgnu_role (role_id, role_name, role_descr, grantable_by_func_id) values (  3, 'Sales Admin',                         'Sales Admin',                         9141);
insert into tlgnu_role (role_id, role_name, role_descr, grantable_by_func_id) values (  4, 'Road Admin',                          'Road Admin',                          9141);
insert into tlgnu_role (role_id, role_name, role_descr, grantable_by_func_id) values (  5, 'Rail Admin',                          'Rail Admin',                          9141);
insert into tlgnu_role (role_id, role_name, role_descr, grantable_by_func_id) values (  6, 'Order Entry',                         'Order Entry',                         9141);
insert into tlgnu_role (role_id, role_name, role_descr, grantable_by_func_id) values (  7, 'Scheduling Road',                     'Scheduling Road',                     9141);
insert into tlgnu_role (role_id, role_name, role_descr, grantable_by_func_id) values (  8, 'Scheduling Rail',                     'Scheduling Rail',                     9141);
insert into tlgnu_role (role_id, role_name, role_descr, grantable_by_func_id) values (  9, 'Dispatching/Loading Road',            'Dispatching/Loading Road',            9141);
insert into tlgnu_role (role_id, role_name, role_descr, grantable_by_func_id) values ( 10, 'Dispatching/Loading Rail',            'Dispatching/Loading Rail',            9141);
insert into tlgnu_role (role_id, role_name, role_descr, grantable_by_func_id) values ( 12, 'Customer (Web Order)',                'Customer (Web Order)',                9142);
insert into tlgnu_role (role_id, role_name, role_descr, grantable_by_func_id) values ( 13, 'Driver',                              'Driver',                              9142);
insert into tlgnu_role (role_id, role_name, role_descr, grantable_by_func_id) values ( 16, 'Pool Manager',                        'Pool Manager',                        9141);
insert into tlgnu_role (role_id, role_name, role_descr, grantable_by_func_id) values ( 18, 'Management',                          'Management',                          9141);
insert into tlgnu_role (role_id, role_name, role_descr, grantable_by_func_id) values ( 20, 'Scheduling Display/(Readonly)',       'Scheduling Display/(Readonly)',       9141);
insert into tlgnu_role (role_id, role_name, role_descr, grantable_by_func_id) values ( 21, 'Railway wagon maintenance',           'Railway wagon maintenance',           9141);
insert into tlgnu_role (role_id, role_name, role_descr, grantable_by_func_id) values ( 22, 'Scheduling (external)',               'Scheduling (external)',               9141);
insert into tlgnu_role (role_id, role_name, role_descr, grantable_by_func_id) values ( 23, 'LOGON Admin Lite',                    'LOGON Admin Lite',                    9141);
insert into tlgnu_role (role_id, role_name, role_descr, grantable_by_func_id) values ( 24, 'User Admin Lite',                     'User Admin Lite',                     9141);
insert into tlgnu_role (role_id, role_name, role_descr, grantable_by_func_id) values ( 25, 'Simple Vehicle Admin',                'Simple Vehicle Admin',                9141);
insert into tlgnu_role (role_id, role_name, role_descr, grantable_by_func_id) values ( 26, 'Delivery Time Adjustment',            'Delivery Time Adjustment',            9141);
insert into tlgnu_role (role_id, role_name, role_descr, grantable_by_func_id) values ( 1000, 'Web Service',                       'Web Service',                         9141);
insert into tlgnu_role (role_id, role_name, role_descr, grantable_by_func_id) values ( 1001, 'Reporting',                         'Reporting',                           9141);
insert into tlgnu_role (role_id, role_name, role_descr, grantable_by_func_id) values ( 1021, 'Report TransPlus/Winsped',          'Report TransPlus/Winsped',            9141);

-- insert into tlgnu_role(role_id, role_name, role_descr) values (-1, 'Selenium test role', 'This role is used only in integration tests');
-- -> Moved into minimal_setup.sql

-- 20120824: Commented out 'insert into tlgnu_role' since LOGON-4668: 11, 14, 15, 17, 19.
-- Do NOT reuse these IDs unless you make 100% that you never have to worry about CHE/NGE prod data (migration!) or other lists with user-roles. DANGER of creating too powerful users!
-- insert into tlgnu_role (role_id, role_name, role_descr) values ( 11, 'Bahnwagenplanung',                 'Bahnwagenplanung');
-- insert into tlgnu_role (role_id, role_name, role_descr) values ( 14, 'Staplerfahrer',                    'Staplerfahrer');
-- insert into tlgnu_role (role_id, role_name, role_descr) values ( 15, 'RFID-Admin',                       'RFID-Admin');
-- insert into tlgnu_role (role_id, role_name, role_descr) values ( 17, 'PDA-Admin',                        'PDA-Admin');
-- insert into tlgnu_role (role_id, role_name, role_descr) values ( 19, 'Test',                             'Test');
-- 22 - Prescheduler -> had been renamed to "External scheduler" meanwhile, and it IS still required.

commit;
