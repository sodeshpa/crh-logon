-- $Revision$
-- $Date$
--
-- Updated by /share/doc/crh/logon_2_1/input/LRQ_001.3_Authorizations_with_New_Org_Support_Concept/20120516_LRQ001.3_Authorizations_and_Role_Actualization_JJF_V2.1_Tab_Modifications.pdf
-- Additional notes to Spec: 
--       Spec was wrong about 'Manual Test Fax/Sms' function (15050), as it is also a testing function, so 'Logon ADMIN Lite' is also not authorized to use it.
--       Spec was wrong about 'External Scheduler' (role_id = 22) it is still meant to be used.

-- Commented out 'insert into tlgnu_role_func' since LOGON-4668 for roles: 11, 14, 15, 17, 19

-- 'Logon ADMIN Lite' (role_id = 23) functions: 'Logon ADMIN Lite' is authorized to use the same functions as Logon Admin except:
--       'Logon ADMIN Lite' is not authorized to use 'Table browser' function (9200).
--       'Logon ADMIN Lite' is not authorized to use testing functions (15010, 15020, 15030, 15040, 15050).
--       'Logon ADMIN Lite' is not authorized to do user administration (needs role 'User Admin/User Admin Lite' in addition, as explained in LOGON-5972).

-- 'User ADMIN Lite' (role_id = 24) functions: 'Logon ADMIN Lite' is authorized to use the same functions as Logon Admin except:
--       'User ADMIN Lite' is not authorized to use 'User Admin Grant role-set 1 Special' function (9141).
--       'User ADMIN Lite' is not authorized to use 'Table browser' function function (9200).

prompt '--------- initialdata/tlgnu_role_func.sql ----------'

prompt INSERTING into TLGNU_ROLE_FUNC

-- func 1000 = Order
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1  /* LOGON Admin        */,  1000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23  /* LOGON Admin Lite   */,  1000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6  /* Auftragsannahme    */,  1000);

-- func 1001 = Pool Assignment
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1  /* LOGON Admin        */,  1001);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23  /* LOGON Admin Lite   */,  1001);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3  /* Sales Admin        */,  1001);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6  /* Auftragsannahme    */,  1001);

-- func 2000 = Web Order
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  2000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  2000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 12 /* Kunde               */,  2000);

-- func 2001 = Web Order View Deleted Orders
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  2001);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  2001);

-- func 3000 = Dispatching R
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  3000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  3000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */,  3000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  7 /* Disposition Strass  */,  3000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 16 /* Poolfuehrer         */,  3000);
-- Added by Christoph in 1.0
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 20 /* Display Dispatching */,  3000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 22 /* External scheduler  */,  3000);

-- func 3001 = Dispatching W
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  3001);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  3001);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  7 /* Disposition Strass  */,  3001);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 22 /* External scheduler  */,  3001);

-- func 3002 = Dispatching Pool W
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  3002);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  3002);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 16 /* Poolfuehrer         */,  3002);

-- func 3003 = Dispatching Int Ord W
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  3003);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  3003);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 16 /* Poolfuehrer         */,  3003);

-- func 3004 = Dispatching Ext Ord Unrestricted Display
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  3004);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  3004);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 22 /* External scheduler  */,  3004);

-- func 4000 = Shipping R
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  4000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  4000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */,  4000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  9 /* Spedition Strasse   */,  4000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 10 /* Spedition Bahn      */,  4000);

-- func 4001 = Shipping W
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  4001);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  4001);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  9 /* Spedition Strasse   */,  4001);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 10 /* Spedition Bahn      */,  4001);

-- func 5100 = Rail Dipatch R
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  5100);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  5100);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */,  5100);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */,  5100);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 10 /* Spedition Bahn      */,  5100);

-- func 5101 = Rail Dispatch W
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  5101);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  5101);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */,  5101);

-- func 5102 = Rail Dispatch SW
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  5102);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  5102);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  5 /* Rail Admin          */,  5102);

-- func 5210 Rail car R
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  5210);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  5210);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */,  5210);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */,  5210);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 10 /* Spedition Bahn      */,  5210);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 12 /* Kunde               */,  5210);

-- func 5211 Rail car Freimelden
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  5211);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  5211);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */,  5211);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */,  5211);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 12 /* Kunde               */,  5211);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 21 /* Bahnwagen Instandhaltung*/, 5211);

-- func 5212 Rail car W
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  5212);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  5212);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */,  5212);

-- func 5220 Rail car return R
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  5220);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  5220);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */,  5220);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 10 /* Spedition Bahn      */,  5220);

-- func 5221 Rail car return W
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  5221);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  5221);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */,  5221);

-- func 523i Rail car view i
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  5231);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  5231);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */,  5231);

insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  5232);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  5232);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */,  5232);

insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  5233);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  5233);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */,  5233);
-- more?

-- func 5300 = Train stations R
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  5300);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  5300);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */,  5300);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */,  5300);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 10 /* Spedition Bahn      */,  5300);

-- func 5301 = Train stations W
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  5301);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  5301);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */,  5301);

-- func 5400 = Block trains R
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  5400);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  5400);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 10 /* Spedition Bahn      */,  5400);
-- insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 11 /* Bahnwagenplanung    */,  5400);

-- func 5401 = Block trains W
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  5401);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  5401);
-- insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 11 /* Bahnwagenplanung    */,  5401);

-- func 5500 = Rail Maintenance
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  5500);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  5500);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */,  5500);

-- func 5600 = Rail Wagon Maintenance 
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  5600);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  5600);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  5 /* Rail Admin          */,  5600);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 21 /* Bahnwagen Instandhaltung*/, 5600);

-- func 6100 = Order List
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  6100);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  6100);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */,  6100);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  7 /* Disposition Strass  */,  6100);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */,  6100);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  9 /* Spedition Strasse   */,  6100);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 10 /* Spedition Bahn      */,  6100);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 12 /* Kunde               */,  6100);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 16 /* Poolfuehrer         */,  6100);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 18 /* Management          */,  6100);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 22 /* External scheduler  */,  6100);

-- func 6101 = Order List For Pool
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  6101);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  6101);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */,  6101);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */,  6101);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  9 /* Spedition Strasse   */,  6101);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 10 /* Spedition Bahn      */,  6101);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 12 /* Kunde               */,  6101);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 16 /* Poolfuehrer         */,  6101);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 18 /* Management          */,  6101);

-- func 6102 = Order List Auftragsannahme
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  6102);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  6102);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */,  6102);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */,  6102);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  9 /* Spedition Strasse   */,  6102);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 10 /* Spedition Bahn      */,  6102);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 12 /* Kunde               */,  6102);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 18 /* Management          */,  6102);

-- func 6103 = External Order List
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  6103);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  6103);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 22 /* External scheduler  */,  6103);

-- func 6220 = Order View 2
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  6220);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  6220);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */,  6220);

-- func 6230 = Order View 3 Monitoring
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  6230);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  6230);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */,  6230);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  7 /* Disposition Strass  */,  6230);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */,  6230);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  9 /* Spedition Strasse   */,  6230);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 10 /* Spedition Bahn      */,  6230);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 16 /* Poolfuehrer         */,  6230);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 18 /* Management          */,  6230);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 22 /* External scheduler  */,  6230);

-- func 6233 = Order View 4 (Truck planning)
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1  /* LOGON Admin        */,  6233);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23  /* LOGON Admin Lite   */,  6233);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 16  /* Poolfuehrer        */,  6233);

-- func 6231 = Order View 3 Monitoring For Pool
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  6231);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  6231);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */,  6231);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */,  6231);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  9 /* Spedition Strasse   */,  6231);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 10 /* Spedition Bahn      */,  6231);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 16 /* Poolfuehrer         */,  6231);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 18 /* Management          */,  6231);

-- func 6232 = Order View 3 Monitoring Auftragsannahme
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  6232);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  6232);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */,  6232);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */,  6232);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  9 /* Spedition Strasse   */,  6232);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 10 /* Spedition Bahn      */,  6232);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 18 /* Management          */,  6232);

-- MORE Order View i?

-- func 7100 = Live Map
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  7100);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  7100);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */,  7100);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  7 /* Disposition Strass  */,  7100);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */,  7100);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  9 /* Spedition Strasse   */,  7100);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 10 /* Spedition Bahn      */,  7100);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 12 /* Kunde               */,  7100);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 16 /* Poolfuehrer         */,  7100);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 18 /* Management          */,  7100);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 22 /* External scheduler  */,  7100);

-- func 7200 = Reporting Map
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  7200);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  7200);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 12 /* Kunde               */,  7200);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 18 /* Management          */,  7200);

-- func 7310 = Report Order List
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  7310);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  7310);

-- func 7320 = Order Data Per Customer
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  7320);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  7320);

-- func 7330 = Order Data Per Carrier
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  7330);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  7330);

-- func 7340 = SAP BW Export
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  7340);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  7340);

-- func 7350 = Report Order Data Per Route
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  7350);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  7350);

-- func 7360 = Report Order Data Per Route
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  7360);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  7360);

-- func 7370 = Report Order Data Per Pool
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  7370);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  7370);

-- func 7380 = Report Order Data Per SP Matpkg
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  7380);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  7380);

-- func 7390 = Report Order Data Per Plant Mat
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  7390);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  7390);

-- func 7400 = Report Definition
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  7400);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  7400);

-- func 7410 = Report Transportation per plant and delivery type
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  7410);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  7410);

-- func 7420 = Report Transportation per plant
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  7420);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  7420);

-- func 7430 = Report Sales per customer and material
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  7430);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  7430);

-- func 7440 = Report Sales per customer
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  7440);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  7440);

-- func 7450 = Report Sales per material
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  7450);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  7450);

-- func 7460 = Order list customer
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  7460);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  7460);

-- func 7470 = Order list carrier
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  7470);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  7470);

-- func 7480 = Order list crh
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  7480);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  7480);

-- func 7490 = Order list commercial
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  7490);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  7490);

-- func 7500 = Order WinSped
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  7500);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  7500);

-- func 7510 = Load shipping stats
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  7510);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  7510);

-- func 7520 = Report Freight Carrier Revenue PDF
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  7520);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  7520);

-- func 7530 = Report staging area errors
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  7530);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  7530);

-- func 7540 = Goods movement
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  7540);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  7540);

-- func 7550 = Report External Order
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  7550);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  7550);

-- func 7560 = Report TransPlus
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  7560);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  7560);

-- func 7570 = iTrack Reporting
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  7570);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  7570);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 1001 /* Reporting    */,  7570);

-- func 7571 = iTrack Reporting Service Level Report
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,
                                                                     7571);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,
                                                                     7571);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 1001 /* Reporting    */,  7571);

-- func 7572 = iTrack Reporting Distance Report
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,
                                                                     7572);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,
                                                                     7572);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 1001 /* Reporting    */,  7572);

-- func 7573 = iTrack Reporting Toll Fee Report
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,
                                                                     7573);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,
                                                                     7573);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 1001 /* Reporting    */,  7573);

-- func 8100 = MD Shipto Card R
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  8100);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8100);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */,  8100);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */,  8100);

-- func 8101 = MD Shipto Card W
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  8101);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8101);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */,  8101);

-- func 8200 = MD Carrier assignment
-- This screen is disabled because of poor performance.
-- insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 16 /* Poolfuehrer         */,  8200);

-- func 8310 = MD RFID Maintenance
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8310);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* Logon Admin Lite    */,  8310);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */,  8310);

-- func 8420 = MD Glob Zone map (bag)
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8420);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8420);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */,  8420);

-- func 8440 = MD Glob Shippingpoint
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8440);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8440);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */,  8440);

-- func 8441 = MD Glob Shippingpoint Create
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8441);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8441);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */,  8441);

-- func 8450 = MD Glob Material
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8450);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8450);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */,  8450);

-- func 8470 = MD Product Change Matrix View
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8470);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8470);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 10 /* Spedition Bahn      */,  8470);

-- func 8480 = Master Data: Global Edit Sales Areas
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8480);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8480);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */,  8480);

-- func 8510 = MD Rd Pool
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8510);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8510);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  4 /* Road Admin          */,  8510);

-- func 8530 = MD Rd Truck
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8530);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8530);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */,  8530);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  4 /* Road Admin          */,  8530);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  7 /* Disposition Strass  */,  8530);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 16 /* Poolfuehrer         */,  8530);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 22 /* External scheduler  */,  8530);

-- func 8531 = MD Rd Truck Update
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8531);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8531);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */,  8531);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  4 /* Road Admin          */,  8531);
-- LOGON-6232, added for role scheduler
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  7 /* Disposition Strass  */,  8531);

-- func 8532 = MD Rd Planning Truck Create 
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8532);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8532);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */,  8532);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 16 /* Poolfuehrer         */,  8532);

-- func 8533 = MD Rd Generic Truck Create 
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8533);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8533);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */,  8533);

-- func 8540 = MD Rd Trailer
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8540);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8540);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */,  8540);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  4 /* Road Admin          */,  8540);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  7 /* Disposition Strass  */,  8540);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 16 /* Poolfuehrer         */,  8540);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 22 /* External scheduler  */,  8540);

-- func 8541 = MD Rd Trailer Update
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8541);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8541);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */,  8541);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  4 /* Road Admin          */,  8541);
-- LOGON-6232, added for role scheduler
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  7 /* Disposition Strass  */,  8541);

-- func 8541 = MD Rd Planning Trailer Create 
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8542);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8542);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  4 /* Road Admin          */,  8542);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 16 /* Poolfuehrer         */,  8542);

-- func 8542 = MD Rd Generic Trailer Create 
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8543);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8543);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */,  8543);

-- func 8550 = MD Rd Container
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8550);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8550);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  4 /* Road Admin          */,  8550);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  7 /* Disposition Strass  */,  8550);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 22 /* External scheduler  */,  8550);

-- func 8551 = MD Rd Container Create
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8551);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8551);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  4 /* Road Admin          */,  8551);

-- func 8555 = MD Rd Simple Vehicle
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8555);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8555);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 25 /* Simple Vehicle Admin*/,  8555);

-- func 8556 = MD Rd Simple Vehicle Create
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8556);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8556);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 25 /* Simple Vehicle Admin*/,  8556);

-- func 8560 = MD Rd Truck Route R
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8560);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8560);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  4 /* Road Admin          */,  8560);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 16 /* Poolfuehrer         */,  8560);

-- func 8561 = MD Rd Truck Route W
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8561);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8561);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 16 /* Poolfuehrer         */,  8561);

-- func 8570 = MD Rd Silo
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8570);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8570);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  4 /* Road Admin          */,  8570);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  7 /* Disposition Strass  */,  8570);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 22 /* External scheduler  */,  8570);

-- func 8571 = MD Rd Silo Create
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8571);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8571);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  4 /* Road Admin          */,  8571);

-- func 8580 = MD Rd External Material R
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8580);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8580);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 22 /* External scheduler  */,  8580);

-- func 8581 = MD Rd External Material W
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8581);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8581);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 22 /* External scheduler  */,  8581);

-- func 8585 = MD Rd External Site R
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8585);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8585);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 22 /* External scheduler  */,  8585);

-- func 8586 = MD Rd External Site W
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8586);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8586);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 22 /* External scheduler  */,  8586);

-- func 8610 = MD Rail cars
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8610);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8610);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  5 /* Rail Admin          */,  8610);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */,  8610);

-- func 8640 = MD Rail Timetbl
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8640);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8640);

-- func 8641 = MD Rail Timetbl Distance
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8641);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  5 /* Rail Admin          */,  8641);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8641);

-- func 8700 = MD Dispocard update view
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8700);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8700);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */,  8700);

-- func 8701 = MD Dispocard update export
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8701);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8701);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */,  8701);

-- func 8800 = MD Master Data Carrier view
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  8800);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  8800);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */,  8800);

-- func 9110 = User admin
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  9110);
-- LOGON-5972: No! Logon Admin Lite would need User Admin/Lite in addition, see LOGON-5972. -- insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  9110);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  2 /* User Admin          */,  9110);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 24 /* User Admin Lite     */,  9110);

-- func 9120 = User admin ext.
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  9120);
-- LOGON-5972: No! Logon Admin Lite would need User Admin/Lite in addition, see LOGON-5972. -- insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  9120);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  2 /* User Admin          */,  9120);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 24 /* User Admin Lite     */,  9120);
-- here is something missing in the excel ...

-- func 9130 = User admin PDA
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  9130);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */,  9130);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  2 /* User Admin          */,  9130);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 24 /* User Admin Lite     */,  9130);

-- func 9141 = User Admin Grant role-set 1 Special
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  9141);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  2 /* User Admin          */,  9141);
-- func 9142 = User Admin Grant role-set 2 Normal
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  9142);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  2 /* User Admin          */,  9142);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 24 /* User Admin Lite     */,  9142);

insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  9143);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  2 /* User Admin          */,  9143);

insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  9150);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  9151);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  9152);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  9153);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin          */, 9154);

insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  2 /* User Admin          */,  9150);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  2 /* User Admin          */,  9151);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  2 /* User Admin          */,  9152);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  2 /* User Admin          */,  9153);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  2 /* User Admin          */,  9154);

insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 24 /* User Admin Lite     */,  9151);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 24 /* User Admin Lite     */,  9153);

-- func 9200 = Table Browser
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  9200);
-- insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  2 /* User Admin          */,  9200);
-- LOGON Admin Lite is not added by reason since LOGON-4668

-- func 11100 = Order Detail R
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */, 11000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  7 /* Disposition Strass  */, 11000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */, 11000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  9 /* Spedition Strasse   */, 11000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 10 /* Spedition Bahn      */, 11000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 12 /* Kunde               */, 11000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 18 /* Management          */, 11000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 21 /* Bahnwagen Instandhaltung*/, 11000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 22 /* External scheduler  */, 11000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 26 /* Del. Time Adjust.   */, 11000);

-- func 11101 = Order Detail W
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11001);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11001);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11001);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */, 11001);

-- func 11102 = Order Det deliv dat
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11002);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11002);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11002);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */, 11002);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 12 /* Kunde               */, 11002);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 26 /* Del. Time Adjust.   */, 11002);

-- func 11003 = Order Detail Overrule Lead Time Check
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */,  11003);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */,  11003);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Order Entry         */,  11003);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  23 /* LOGON Admin Lite     */,  11003);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  26 /* Del. Time Adjust.   */,  11003);

-- func 11110 = OD Mat R
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11110);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11110);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11110);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */, 11110);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  7 /* Disposition Strass  */, 11110);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */, 11110);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  9 /* Spedition Strasse   */, 11110);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 10 /* Spedition Bahn      */, 11110);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 12 /* Kunde               */, 11110);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 18 /* Management          */, 11110);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 22 /* External scheduler  */, 11110);

-- func 11111 = OD Mat W
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11111);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11111);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11111);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */, 11111);

-- func 11112 = OD Mat quantity uom
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11112);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11112);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11112);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */, 11112);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  9 /* Spedition Strasse   */, 11112);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 10 /* Spedition Bahn      */, 11112);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 12 /* Kunde               */, 11112);

-- func 11113 = OD Mat billto party
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11113);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11113);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11113);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */, 11113);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 12 /* Kunde               */, 11113);

-- func 11114 = Order Detail Material: change payer party
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11114);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11114);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11114);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */, 11114);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 12 /* Kunde               */, 11114);

-- func 11115 = OD Mat cust ser no
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11115);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11115);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11115);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */, 11115);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  7 /* Disposition Strass  */, 11115);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  9 /* Spedition Strasse   */, 11115);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 10 /* Spedition Bahn      */, 11115);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 22 /* External scheduler  */, 11115);

-- func 11116 = OD Mat fwd agent
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11116);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11116);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11116);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */, 11116);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  7 /* Disposition Strass  */, 11116);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 22 /* External scheduler  */, 11116);

-- func 11117 = OD Mat deliv note
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11117);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11117);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11117);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */, 11117);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  7 /* Disposition Strass  */, 11117);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */, 11117);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  9 /* Spedition Strasse   */, 11117);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 10 /* Spedition Bahn      */, 11117);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 12 /* Kunde               */, 11117);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 22 /* External scheduler  */, 11117);

-- func 11118 = OD Special update billto
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11118);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11118);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11118);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  21 /* Bahnwagen Instandhaltung*/, 11118);

-- func 11119 = OD Special delete in status: dispatched (only pickup), finished, delivered
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11119);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11119);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11119);

-- func 11120 = Order Correction: Weights and Quantities
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11120);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11120);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11120);


-- func 11121 = Order Detail Material View Unapproved Return Q
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11121);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11121);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11121);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 10 /* Spedition Bahn      */, 11121);

-- func 11122 = Order Adjustment Weights
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11122);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11122);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11122);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  9 /* Spedition Strasse   */, 11122);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 10 /* Spedition Bahn      */, 11122);

-- func 11123 = Credit Note Create
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11123);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11123);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11123);

-- func 11124 = Order Detail Material: change one-time soldto address
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11124);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11124);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11124);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  21 /* Bahnwagen Instandhaltung*/, 11124);

-- func 11125 = Order Detail Material: Edit LDS Delivery note number.
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11125);

-- func 11200 = Order Detail Map
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11200);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11200);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11200);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */, 11200);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  7 /* Disposition Strass  */, 11200);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */, 11200);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  9 /* Spedition Strasse   */, 11200);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 10 /* Spedition Bahn      */, 11200);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 12 /* Kunde               */, 11200);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 18 /* Management          */, 11200);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 22 /* External scheduler  */, 11200);

-- func 11300 = OD Rep R
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11300);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11300);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11300);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */, 11300);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  7 /* Disposition Strass  */, 11300);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */, 11300);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  9 /* Spedition Strasse   */, 11300);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 10 /* Spedition Bahn      */, 11300);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 18 /* Management          */, 11300);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 22 /* External scheduler  */, 11300);

-- func 11301 = OD Rep w
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11301);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11301);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11301);

-- func 11302 = OD Rep srv lev flag
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11302);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11302);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11302);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */, 11302);

-- func 11303 = OD Rep rating holc
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11303);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11303);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11303);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */, 11303);

-- func 11304 = OD Rep rating carr
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11304);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11304);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11304);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  7 /* Disposition Strass  */, 11304);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 22 /* External scheduler  */, 11304);

-- func 11305 = OD Rep cmnt dispat
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11305);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11305);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11305);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */, 11305);

-- func 11306 = OD Rep cmnt drive
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11306);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11306);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11306);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */, 11306);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  7 /* Disposition Strass  */, 11306);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */, 11306);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 22 /* External scheduler  */, 11306);

-- func 11307 = OD Rep cmnt shipp
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11307);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11307);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11307);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */, 11307);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  7 /* Disposition Strass  */, 11307);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */, 11307);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 22 /* External scheduler  */, 11307);

-- func 11400 = OD Order Mgmt
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11400);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11400);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11400);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */, 11400);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  7 /* Disposition Strass  */, 11400);

-- func 11401 = OD Order Mgmt: min qty charge flag
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11401);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11401);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11401);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */, 11401);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 16 /* Poolfuehrer         */, 11401);

-- func 11402 = OD Order Mgmt: no pda or rail event
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11402);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11402);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  7 /* Disposition Strass  */, 11402);

-- func 11500 = OD Commercial
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11500);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11500);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11500);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 18 /* Management          */, 11500);

-- func 11600 = OD Order Log
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11600);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11600);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11600);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */, 11600);

-- func 11700 = OD Truck Log
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11700);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11700);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11700);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */, 11700);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  7 /* Disposition Strass  */, 11700);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */, 11700);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  9 /* Spedition Strasse   */, 11700);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 10 /* Spedition Bahn      */, 11700);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 18 /* Management          */, 11700);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 22 /* External scheduler  */, 11700);

-- func 11800 = Order Detail Repair Maintenance Full Access
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11800);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11800);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  5 /* Rail Admin          */, 11800);

-- func 11900 = Order Detail change and store the special processing indicator
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11900);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11900);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11900);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */, 11900);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 16 /* Poolfuehrer         */, 11900);

-- func 11950 = Order Detail Services/Extra costs tab: Set service materials, waiting time and extra costs before order status finished.
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11950);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11950);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11950);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */, 11950);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 16 /* Poolfuehrer         */, 11950);

-- func 11951 = Order Detail Services/Extra costs tab: Set service materials, waiting time and extra costs after order status finished.
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 11951);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 11951);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 11951);

-- func 12000 = Railcar Det Base R
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 12000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 12000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  5 /* Rail Admin          */, 12000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */, 12000);

-- func 12001 = Railcar Det Base W
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 12001);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 12001);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  5 /* Disposition Bahn    */, 12001);

-- func 12010 = Railcar Det Order R
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 12010);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 12010);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  5 /* Rail Admin          */, 12010);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */, 12010);

-- func 12011 = Railcar Det Order W
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 12011);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 12011);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */, 12011);

-- func 12100 = Trainst Det Base R

insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 12100);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 12100);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  5 /* Rail Admin          */, 12100);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */, 12100);

-- func 12101 = Trainst Det Base W
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */, 12101);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 12101);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  5 /* Rail Admin          */, 12101);

-- func 12110 = Trainst Det Order R
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */, 12110);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 12110);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  5 /* Rail Admin          */, 12110);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */, 12110);

-- func 12111 = Trainst Det Order W
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */, 12111);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 12111);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */, 12111);

-- func 12200 = Ext. Orders (Sped.)
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */, 12200);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 12200);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  7 /* Disposition Strass  */, 12200);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 22 /* External scheduler  */, 12200);

-- func 13010 = F01 Unlock Par Term
-- (no assignment known yet, so: at lieast for LOGON admin)
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 13010);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 13010);


-- func 13020 = F02 Close Order
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 13020);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 13020);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */, 13020);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 12 /* Kunde               */, 13020);

-- func 13030 = F03 Ordermaster
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 13030);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 13030);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 13030);

-- func 14010 = P01 PDA Blk/Bag Hdl
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 14010);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 14010);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 13 /* Chauffeur           */, 14010);

-- func 14020 = P02 PDA Term/Stapl
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 14020);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 14020);
-- insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 14 /* Staplerfahrer       */, 14020);

-- func 14030 = P03 PDA RFID-Admin
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 14030);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 14030);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 14030);

-- func 14040 = P04 PDA PDA-Admin
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 14040);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 14040);
-- insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 17 /* PDA Admin           */, 14040);

-- func 15010 = Manual Test PDA
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 15010);
-- insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  19 /* Test	            */, 15010);
-- LOGON Admin Lite is not added by reason since LOGON-4668

-- func 15020 = Manual Test Webservice
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 15020);
-- insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  19 /* Test       		    */, 15020);
-- LOGON Admin Lite is not added by reason since LOGON-4668

-- func 15030 = Test Versioncheck
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 15030);
-- LOGON Admin Lite is not added by reason since LOGON-4668

-- func 15040 = Test CronJobs
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 15040);
-- LOGON Admin Lite is not added by reason since LOGON-4668

-- func 15050 = Manual Test Fax/Sms
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 15050);
-- insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  19 /* Test       		    */, 15050);

-- func 16000 = Pickup Order Terminal R
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 16000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 16000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 12 /* Kunde               */, 16000);

-- func 16001 = Pickup Order Terminal Delivery Note 
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 16001);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 16001);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 12 /* Kunde               */, 16001);

-- func 16002 = Pickup Order Terminal Create New Order 
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 16002);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 16002);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 12 /* Kunde               */, 16002);

-- func 16003 = Pickup Order Terminal W  
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 16003);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 16003);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 12 /* Kunde               */, 16003);

-- func 17000 = Bag Order Terminal R
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 17000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 17000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 13 /* Chauffeur           */, 17000);

-- func 17001 = Bag Order Terminal Change Truck
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 17001);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 17001);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 13 /* Chauffeur           */, 17001);

-- func 17001 = Bag Order Terminal Generate Delivery Note
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 17002);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 17002);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 13 /* Chauffeur           */, 17002);

-- func 18000 = Wagon Special Order Create/Edit
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 18000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 18000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 18000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  5 /* Rail Admin          */, 18000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */, 18000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */, 18000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 21 /* Bahnwagen Instandhaltung*/, 18000);

-- func 19000 = Rail car optimization
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 19000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 19000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 19000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  5 /* Rail Admin          */, 19000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */, 19000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */, 19000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 10 /* Spedition Bahn      */, 19000);

-- func 20000 = Bimodal Railcar monitoring
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 20000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 20000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Auftragsannahme     */, 20000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 18 /* Management          */, 20000);

-- 21000 = Simple Vehicle Dispatching: read access
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 21000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 21000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  4 /* Road Admin          */, 21000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  5 /* Rail Admin          */, 21000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  7 /* Disposition Strass  */, 21000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */, 21000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 20 /* Display Dispatching */, 21000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 21000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 25 /* Simple Vehicle Admin */,21000);

-- 21001 = Simple Vehicle Dispatching: write acces
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 21001);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */, 21001);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  4 /* Road Admin          */, 21001);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  5 /* Rail Admin          */, 21001);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  7 /* Disposition Strass  */, 21001);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  8 /* Disposition Bahn    */, 21001);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 21001);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 25 /* Simple Vehicle Admin */,21001);

-- func 22000 = Container Monitoring
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* Logon Admin         */, 22000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 23 /* LOGON Admin Lite    */, 22000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval, 18 /* Management          */, 22000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Order Entry         */, 22000);

-- func 24000 = Order Detail On-Hold functionality
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  24000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  3 /* Sales Admin         */,  24000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Order Entry         */,  24000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  12 /* Kunden (Web Order) */,  24000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  23 /* Web Service        */,  24000);

-- func 25000 = Demand Planning
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  25000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Order Entry         */,  25000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  12 /* Kunden (Web Order) */,  25000);

-- func 25001 = Demand Planning Expected Demand
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  25001);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Order Entry         */,  25001);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  12 /* Kunden (Web Order) */,  25001);

-- func 25002 = Demand Planning Expected vs Realized
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  25002);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Order Entry         */,  25002);

-- func 25010 = Demand Planning Unlimited
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  25010);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Order Entry         */,  25010);

commit;
