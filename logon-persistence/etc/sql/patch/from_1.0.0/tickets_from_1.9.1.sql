prompt ************************************************************************
prompt *** BEGIN CRHLOGON-1354 Append PIN of order in the SMS sent to driver in case of order release
prompt ************************************************************************

prompt *** Change ONLY relevant tlgnr_text_bs and tlgnr_text_tx entries for CRHLOGON-1354

-------BELOW COPIED OVER tlgnr_text.sql relevant rows. Since the tlgnr_text_tx and tlgnr_text_bs entries are not anymore the same as the ones in the  tlgnr_text.sql change only the relevant rows. See also CRHLOGON-1322
----------------------------------

-- $Revision$
-- $Date$
--
-- This is a half internal table:
-- - Extending the possible values requires code changes
-- - Changing the localized values possible without code changes, as long as number of placeholders stays the same.

--LOGON-4819: Added Spanish language support (2012.05.16)
--LOGON-5893: Replaced the text for pdf.deliverynote.deliverytype to one space in order to not show the text anymore (2012.11.22)

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

prompt INSERTING into TLGNR_TEXT_BS
execute upsert_bs('order.released.sms.message', 12, 1);

prompt INSERTING into TLGNR_TEXT_TX - EN
execute upsert_tx('order.released.sms.message', 'en', 'ORDER {0} / Start: {1} / Time: {2}({3}) - {4}({5}) / Ship-to: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');

prompt INSERTING into TLGNR_TEXT_TX - DE
execute upsert_tx('order.released.sms.message', 'de', 'ORDER {0} / Start: {1} / Time: {2}({3}) - {4}({5}) / Ship-to: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');

prompt INSERTING into TLGNR_TEXT_TX - FR
execute upsert_tx('order.released.sms.message', 'fr', 'ORDER {0} / Start: {1} / Time: {2}({3}) - {4}({5}) / Ship-to: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');

prompt INSERTING into TLGNR_TEXT_TX - IT
execute upsert_tx('order.released.sms.message', 'it', 'ORDER {0} / Start: {1} / Time: {2}({3}) - {4}({5}) / Ship-to: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');

prompt INSERTING into TLGNR_TEXT_TX - ES
execute upsert_tx('order.released.sms.message', 'es', 'ORDER {0} / Start: {1} / Time: {2}({3}) - {4}({5}) / Ship-to: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');

prompt INSERTING into TLGNR_TEXT_TX - CS
execute upsert_tx('order.released.sms.message', 'cs', 'Zakázka {0} / Start: {1} / Čas: {2}({3}) - {4}({5}) / Příjemce: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');

prompt INSERTING into TLGNR_TEXT_TX - SK
execute upsert_tx('order.released.sms.message', 'sk', 'Objednávka {0} / Štart: {1} / Čas: {2}({3}) - {4}({5}) / Príjemca tovaru: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');

prompt INSERTING into TLGNR_TEXT_TX - HU
execute upsert_tx('order.released.sms.message', 'hu', 'Megrendelés: {0}/ Kezdés: {1}/ Idő: {2}({3}) - {4}({5}) /Ship-to: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');

prompt INSERTING into TLGNR_TEXT_TX - RO
execute upsert_tx('order.released.sms.message', 'ro', 'COMANDA {0} / Start: {1} / Timp: {2}({3}) - {4}({5}) / Destinatar: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');

prompt INSERTING into TLGNR_TEXT_TX - BG
execute upsert_tx('order.released.sms.message', 'bg', 'Поръчка {0} / Начало: {1} / Време: {2}({3}) - {4}({5}) / Обект: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');

prompt INSERTING into TLGNR_TEXT_TX - RU
execute upsert_tx('order.released.sms.message', 'ru', 'ЗАКАЗ {0} / Начало: {1} / Время: {2}({3}) - {4}({5}) / Грузополучатель: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');

prompt INSERTING into TLGNR_TEXT_TX - PL
execute upsert_tx('order.released.sms.message', 'pl', 'ZAMÓWIENIE {0} / Start: {1} / Czas: {2}({3}) - {4}({5}) / Odbiorca towaru: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');

prompt INSERTING into TLGNR_TEXT_TX - AZ
execute upsert_tx('order.released.sms.message', 'az', 'SİFARİŞ {0} / Başlanğıc: {1} / Vaxt: {2}({3}) - {4}({5}) / Gəmi-hara: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');

prompt INSERTING into TLGNR_TEXT_TX - HR
execute upsert_tx('order.released.sms.message', 'hr', 'NALOG {0} / Početak: {1} / vrijeme: {2}({3}) - {4}({5}) / Odredište: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');

prompt INSERTING into TLGNR_TEXT_TX - SR
execute upsert_tx('order.released.sms.message', 'sr', 'NALOG {0} / Početak: {1} / vreme: {2}({3}) - {4}({5}) / Odredište: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');

prompt INSERTING into TLGNR_TEXT_TX - TR
execute upsert_tx('order.released.sms.message', 'tr', 'ORDER {0} / Start: {1} / Time: {2}({3}) - {4}({5}) / Ship-to: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');
commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;


-------ABOVE COPIED OVER tlgnr_text.sql relevant rows. Since the tlgnr_text_tx and tlgnr_text_bs entries are not anymore the same as the ones in the  tlgnr_text.sql change only the relevant rows. See also CRHLOGON-1322
----------------------------------

prompt ************************************************************************
prompt *** END CRHLOGON-1354 Append PIN of order in the SMS sent to driver in case of order release
prompt ************************************************************************