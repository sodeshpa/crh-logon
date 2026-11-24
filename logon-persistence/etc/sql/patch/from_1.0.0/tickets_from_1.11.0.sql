prompt ************************************************************************
prompt *** BEGIN CRHLOGON-1415 Enabling and disabling the functionality for iTrack
prompt ************************************************************************

ALTER TABLE TLGNC_SAREA ADD ITRACK_AVAILABLE_FLAG NUMBER(1,0);
ALTER TABLE TLGNC_TRUCK ADD ITRACK_AVAILABLE_FLAG NUMBER(1,0);
ALTER TABLE TLGNC_ORDER ADD ITRACK_AVAILABLE_FLAG NUMBER(1,0);

UPDATE TLGNC_SAREA SET ITRACK_AVAILABLE_FLAG = 0;
UPDATE TLGNC_TRUCK SET ITRACK_AVAILABLE_FLAG = 0;
UPDATE TLGNC_ORDER SET ITRACK_AVAILABLE_FLAG = 0;

COMMENT ON COLUMN TLGNC_SAREA.ITRACK_AVAILABLE_FLAG is
	'Set if the trucks in the sales area are equipped with iTrack. If set the order gets to Shipping status after loading and order will be included in iTrack related report';
COMMENT ON COLUMN TLGNC_TRUCK.ITRACK_AVAILABLE_FLAG is
    'Set if the trucks in the sales area are equipped with iTrack. If set the order gets to Shipping status after loading and order will be included in iTrack related report';
COMMENT ON COLUMN TLGNC_ORDER.ITRACK_AVAILABLE_FLAG is
    'Set to true in case the order is tracked in the external system iTrack.';

prompt ************************************************************************
prompt *** END CRHLOGON-1415 Enabling and disabling the functionality for iTrack
prompt ************************************************************************

prompt ************************************************************************
prompt *** BEGIN CRHLOGON-1419 Track the unloading of the trucks equipped with iTrack
prompt ************************************************************************

ALTER TABLE TLGNC_ORDER ADD CHECK_UNLOAD_FLAG NUMBER(1,0);

COMMENT ON COLUMN TLGNC_ORDER.CHECK_UNLOAD_FLAG is
    'Set if the shipto received from iTrack does not match the designated shipto of the order';

prompt ************************************************************************
prompt *** END CRHLOGON-1419 Track the unloading of the trucks equipped with iTrack
prompt ************************************************************************

prompt ************************************************************************
prompt *** START CRHLOGON-1404 Reports
prompt ************************************************************************

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


alter table tlgnc_order add (
    toll_fee                             number(12,4)          null
);

COMMENT ON COLUMN TLGNC_ORDER.toll_fee is
    'Toll fee for the truck received from iTrack.';


alter table TLGNC_ORDER add (
    itrack_distance_km                      number(12)           null
);

COMMENT ON COLUMN TLGNC_ORDER.itrack_distance_km is
    'Calculated as indexkilometer of truck at the time of Unloading -  indexkilometer of truck at the time of Loading (received from iTrack)';


alter table tlgnc_truck add (
    itrack_target_id                 number(12)           null
);

COMMENT ON COLUMN TLGNC_TRUCK.itrack_target_id is
    'External ID of the truck in the ITrack external system. Might change if the truck is sold to a different carrier.';


prompt ************************************************************************
prompt *** END CRHLOGON-1404 Reports
prompt ************************************************************************

prompt ************************************************************************
prompt *** START CRHLOGON-1409 Toll Fee
prompt ************************************************************************
prompt '---------- refdata/tlgnr_text.sql ----------'

set define off;

-------------------------------------------------------------------------------
-- is_different_varchar2
-------------------------------------------------------------------------------
-- compares two varchars.
-- They are identical either if a) both are null, b) they are equal by "="
-- They are different if a) either is null and the other not, b) different by "="
create or replace function is_different_varchar2 (
	i_a in varchar2,   i_b in varchar2)
	return number is
begin
	if ((i_a != i_b)
		or (i_a is null and i_b is not null)
		or (i_a is not null and i_b is null)) then
		return 1;
	end if;
	return 0;
end;
/
show errors;

-------------------------------------------------------------------------------
-- is_different_number
-------------------------------------------------------------------------------
-- compares two numbers.
-- They are identical either if a) both are null, b) they are equal by "="
-- They are different if a) either is null and the other not, b) different by "="
create or replace function is_different_number (
	i_a in number,   i_b in number)
	return number is
begin
	if ((i_a != i_b)
		or (i_a is null and i_b is not null)
		or (i_a is not null and i_b is null)) then
		return 1;
	end if;
	return 0;
end;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_text_bs
-------------------------------------------------------------------------------
create or replace
	procedure upsert_bs (
	p_text_cd                   in varchar2,   p_placeholder_count         in number,   p_ctl_act_cd                in number)
is
	v_table_name    varchar2(48) := 'tlgnr_text_bs';
	v_exists_flag   number;  -- number of existing records of that key (0 or 1)

begin
	dbms_output.enable(100000);
	dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_text_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

	select count(bs.text_cd)
		   into v_exists_flag
	from tlgnr_text_bs bs
	where bs.text_cd = p_text_cd;

	if (v_exists_flag > 0) then
		if (p_ctl_act_cd != 1) then
			dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_text_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
		else
			update tlgnr_text_bs set
/* text_cd: cannot update because this is the key */
									 ctl_act_cd                     = p_ctl_act_cd,           placeholder_count              = p_placeholder_count,           ctl_mod_uid                    = 1,           ctl_mod_ts                     = systimestamp
			where
					text_cd = p_text_cd
			;
			dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_text_cd || ').');
		end if;
	else
		insert into tlgnr_text_bs (
			text_cd,         placeholder_count,         ctl_act_cd,         ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
		) values (
					 p_text_cd,         p_placeholder_count,         p_ctl_act_cd,         1, systimestamp, 1, systimestamp
				 );
		dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_text_cd || ').');
	end if;

end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_text_tx
-------------------------------------------------------------------------------
create or replace
	procedure upsert_tx (
	p_text_cd in varchar2,   p_lang_cd                 in varchar2,   p_text                    in varchar2)
is
	v_table_name      varchar2(48) := 'tlgnr_text_tx';
	v_exists_flag     number;        -- number of existing records of that key (0 or 1)
	v_exists_bs_flag  number;        -- number of existing BASE records

begin
	dbms_output.enable(100000);
	dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_text_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

	----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
	select count(bs.text_cd) into v_exists_bs_flag
	from tlgnr_text_bs bs
	where bs.text_cd = p_text_cd;

	if (v_exists_bs_flag > 0) then
		select count(tx.text_cd) into v_exists_flag
		from tlgnr_text_tx tx
		where tx.text_cd = p_text_cd and tx.lang_cd = p_lang_cd;

		if (v_exists_flag > 0) then
			update tlgnr_text_tx set
/* p_text_cd and lang_cd: cannot update because these are the keys */
									 txt                            = p_text,           ctl_mod_uid                    = 1,           ctl_mod_ts                     = systimestamp
			where
					text_cd = p_text_cd and
					lang_cd = p_lang_cd         and
					1 = is_different_varchar2(txt, p_text)
			;
			dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_text_cd || ', LANG=' || p_lang_cd || ').');

		else
			insert into tlgnr_text_tx (
				text_cd,           lang_cd,           txt,           ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
			) values (
						 p_text_cd,           p_lang_cd,           p_text,           1, systimestamp, 1, systimestamp
					 );
			dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_text_cd || ', LANG=' || p_lang_cd || ').');
		end if;
	else
		dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_text_cd || ').');
	end if;

end upsert_tx;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_text_tx
-------------------------------------------------------------------------------
create or replace
	procedure upsert_tx (
	p_text_cd in varchar2,   p_lang_cd                 in varchar2,   p_text                    in varchar2)
is
	v_table_name      varchar2(48) := 'tlgnr_text_tx';
	v_exists_flag     number;        -- number of existing records of that key (0 or 1)
	v_exists_bs_flag  number;        -- number of existing BASE records

begin
	dbms_output.enable(100000);
	dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_text_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

	----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
	select count(bs.text_cd) into v_exists_bs_flag
	from tlgnr_text_bs bs
	where bs.text_cd = p_text_cd;

	if (v_exists_bs_flag > 0) then
		select count(tx.text_cd) into v_exists_flag
		from tlgnr_text_tx tx
		where tx.text_cd = p_text_cd and tx.lang_cd = p_lang_cd;

		if (v_exists_flag > 0) then
			update tlgnr_text_tx set
/* p_text_cd and lang_cd: cannot update because these are the keys */
									 txt                            = p_text,           ctl_mod_uid                    = 1,           ctl_mod_ts                     = systimestamp
			where
					text_cd = p_text_cd and
					lang_cd = p_lang_cd         and
					1 = is_different_varchar2(txt, p_text)
			;
			dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_text_cd || ', LANG=' || p_lang_cd || ').');

		else
			insert into tlgnr_text_tx (
				text_cd,           lang_cd,           txt,           ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
			) values (
						 p_text_cd,           p_lang_cd,           p_text,           1, systimestamp, 1, systimestamp
					 );
			dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_text_cd || ', LANG=' || p_lang_cd || ').');
		end if;
	else
		dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_text_cd || ').');
	end if;

end upsert_tx;
/
show errors;

execute upsert_bs('unloading.different.location.email.subject', 0, 1);
execute upsert_bs('unloading.different.location.email.text', 2, 1);

prompt INSERTING into TLGNR_TEXT_TX - EN
execute upsert_tx('unloading.different.location.email.subject', 'en', 'Unloading happened at a different location');
execute upsert_tx('unloading.different.location.email.text', 'en', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - DE
execute upsert_tx('unloading.different.location.email.subject', 'de', 'Unloading happened at a different location');
execute upsert_tx('unloading.different.location.email.text', 'de', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - FR
execute upsert_tx('unloading.different.location.email.subject', 'fr', 'Unloading happened at a different location');
execute upsert_tx('unloading.different.location.email.text', 'fr', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - IT
execute upsert_tx('unloading.different.location.email.subject', 'it', 'Unloading happened at a different location');
execute upsert_tx('unloading.different.location.email.text', 'it', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - ES
execute upsert_tx('unloading.different.location.email.subject', 'es', 'Unloading happened at a different location');
execute upsert_tx('unloading.different.location.email.text', 'es', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - CS
execute upsert_tx('unloading.different.location.email.subject', 'cs', 'Unloading happened at a different location');
execute upsert_tx('unloading.different.location.email.text', 'cs', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - SK
execute upsert_tx('unloading.different.location.email.subject', 'sk', 'Unloading happened at a different location');
execute upsert_tx('unloading.different.location.email.text', 'sk', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - HU
execute upsert_tx('unloading.different.location.email.subject', 'hu', 'Unloading happened at a different location');
execute upsert_tx('unloading.different.location.email.text', 'hu', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - RO
execute upsert_tx('unloading.different.location.email.subject', 'ro', 'Unloading happened at a different location');
execute upsert_tx('unloading.different.location.email.text', 'ro', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - BG
execute upsert_tx('unloading.different.location.email.subject', 'bg', 'Unloading happened at a different location');
execute upsert_tx('unloading.different.location.email.text', 'bg', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - RU
execute upsert_tx('unloading.different.location.email.subject', 'ru', 'Unloading happened at a different location');
execute upsert_tx('unloading.different.location.email.text', 'ru', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - PL
execute upsert_tx('unloading.different.location.email.subject', 'pl', 'Unloading happened at a different location');
execute upsert_tx('unloading.different.location.email.text', 'pl', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - AZ
execute upsert_tx('unloading.different.location.email.subject', 'az', 'Unloading happened at a different location');
execute upsert_tx('unloading.different.location.email.text', 'az', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - HR
execute upsert_tx('unloading.different.location.email.subject', 'hr', 'Unloading happened at a different location');
execute upsert_tx('unloading.different.location.email.text', 'hr', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - SR
execute upsert_tx('unloading.different.location.email.subject', 'sr', 'Unloading happened at a different location');
execute upsert_tx('unloading.different.location.email.text', 'sr', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - TR
execute upsert_tx('unloading.different.location.email.subject', 'tr', 'Unloading happened at a different location');
execute upsert_tx('unloading.different.location.email.text', 'tr', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');
commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

prompt ************************************************************************
prompt *** END CRHLOGON-1409 Toll Fee
prompt ************************************************************************
