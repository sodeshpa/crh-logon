prompt ************************************************************************
prompt *** BEGIN CRHLOGON-1419 Track the unloading of the trucks equipped with iTrack
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

execute upsert_bs('unloading.different.location.sms.text', 2, 1);
execute upsert_bs('unloads.for.target.error.email.subject', 0, 1);
execute upsert_bs('unloads.for.target.error.email.text', 3, 1);
execute upsert_bs('unloads.for.target.ok.email.subject', 0, 1);
execute upsert_bs('unloads.for.target.ok.email.text', 2, 1);


prompt INSERTING into TLGNR_TEXT_TX - EN
execute upsert_tx('unloading.different.location.sms.text', 'en', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - DE
execute upsert_tx('unloading.different.location.sms.text', 'de', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - FR
execute upsert_tx('unloading.different.location.sms.text', 'fr', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - IT
execute upsert_tx('unloading.different.location.sms.text', 'it', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - ES
execute upsert_tx('unloading.different.location.sms.text', 'es', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - CS
execute upsert_tx('unloading.different.location.sms.text', 'cs', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - SK
execute upsert_tx('unloading.different.location.sms.text', 'sk', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - HU
execute upsert_tx('unloading.different.location.sms.text', 'hu', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - RO
execute upsert_tx('unloading.different.location.sms.text', 'ro', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - BG
execute upsert_tx('unloading.different.location.sms.text', 'bg', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - RU
execute upsert_tx('unloading.different.location.sms.text', 'ru', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - PL
execute upsert_tx('unloading.different.location.sms.text', 'pl', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - AZ
execute upsert_tx('unloading.different.location.sms.text', 'az', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - HR
execute upsert_tx('unloading.different.location.sms.text', 'hr', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - SR
execute upsert_tx('unloading.different.location.sms.text', 'sr', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - TR
execute upsert_tx('unloading.different.location.sms.text', 'tr', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - EN
execute upsert_tx('unloads.for.target.error.email.subject', 'en', 'Error happened during Unloading process');
execute upsert_tx('unloads.for.target.error.email.text', 'en', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject', 'en', 'Unloading process failure has been resolved');
execute upsert_tx('unloads.for.target.ok.email.text', 'en', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - DE
execute upsert_tx('unloads.for.target.error.email.subject', 'de', 'Error happened during Unloading process');
execute upsert_tx('unloads.for.target.error.email.text', 'de', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject', 'de', 'Unloading process failure has been resolved');
execute upsert_tx('unloads.for.target.ok.email.text', 'de', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - FR
execute upsert_tx('unloads.for.target.error.email.subject', 'fr', 'Error happened during Unloading process');
execute upsert_tx('unloads.for.target.error.email.text', 'fr', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject', 'fr', 'Unloading process failure has been resolved');
execute upsert_tx('unloads.for.target.ok.email.text', 'fr', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - IT
execute upsert_tx('unloads.for.target.error.email.subject', 'it', 'Error happened during Unloading process');
execute upsert_tx('unloads.for.target.error.email.text', 'it', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject', 'it', 'Unloading process failure has been resolved');
execute upsert_tx('unloads.for.target.ok.email.text', 'it', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - ES
execute upsert_tx('unloads.for.target.error.email.subject', 'es', 'Error happened during Unloading process');
execute upsert_tx('unloads.for.target.error.email.text', 'es', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject', 'es', 'Unloading process failure has been resolved');
execute upsert_tx('unloads.for.target.ok.email.text', 'es', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - CS
execute upsert_tx('unloads.for.target.error.email.subject', 'cs', 'Error happened during Unloading process');
execute upsert_tx('unloads.for.target.error.email.text', 'cs', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject', 'cs', 'Unloading process failure has been resolved');
execute upsert_tx('unloads.for.target.ok.email.text', 'cs', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - SK
execute upsert_tx('unloads.for.target.error.email.subject', 'sk', 'Error happened during Unloading process');
execute upsert_tx('unloads.for.target.error.email.text', 'sk', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject', 'sk', 'Unloading process failure has been resolved');
execute upsert_tx('unloads.for.target.ok.email.text', 'sk', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - HU
execute upsert_tx('unloads.for.target.error.email.subject', 'hu', 'Error happened during Unloading process');
execute upsert_tx('unloads.for.target.error.email.text', 'hu', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject', 'hu', 'Unloading process failure has been resolved');
execute upsert_tx('unloads.for.target.ok.email.text', 'hu', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - RO
execute upsert_tx('unloads.for.target.error.email.subject', 'ro', 'Error happened during Unloading process');
execute upsert_tx('unloads.for.target.error.email.text', 'ro', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject', 'ro', 'Unloading process failure has been resolved');
execute upsert_tx('unloads.for.target.ok.email.text', 'ro', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - BG
execute upsert_tx('unloads.for.target.error.email.subject', 'bg', 'Error happened during Unloading process');
execute upsert_tx('unloads.for.target.error.email.text', 'bg', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject', 'bg', 'Unloading process failure has been resolved');
execute upsert_tx('unloads.for.target.ok.email.text', 'bg', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - RU
execute upsert_tx('unloads.for.target.error.email.subject', 'ru', 'Error happened during Unloading process');
execute upsert_tx('unloads.for.target.error.email.text', 'ru', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject', 'ru', 'Unloading process failure has been resolved');
execute upsert_tx('unloads.for.target.ok.email.text', 'ru', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - PL
execute upsert_tx('unloads.for.target.error.email.subject', 'pl', 'Error happened during Unloading process');
execute upsert_tx('unloads.for.target.error.email.text', 'pl', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject', 'pl', 'Unloading process failure has been resolved');
execute upsert_tx('unloads.for.target.ok.email.text', 'pl', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - AZ
execute upsert_tx('unloads.for.target.error.email.subject', 'az', 'Error happened during Unloading process');
execute upsert_tx('unloads.for.target.error.email.text', 'az', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject', 'az', 'Unloading process failure has been resolved');
execute upsert_tx('unloads.for.target.ok.email.text', 'az', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - HR
execute upsert_tx('unloads.for.target.error.email.subject', 'hr', 'Error happened during Unloading process');
execute upsert_tx('unloads.for.target.error.email.text', 'hr', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject', 'hr', 'Unloading process failure has been resolved');
execute upsert_tx('unloads.for.target.ok.email.text', 'hr', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - SR
execute upsert_tx('unloads.for.target.error.email.subject', 'sr', 'Error happened during Unloading process');
execute upsert_tx('unloads.for.target.error.email.text', 'sr', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject', 'sr', 'Unloading process failure has been resolved');
execute upsert_tx('unloads.for.target.ok.email.text', 'sr', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');

prompt INSERTING into TLGNR_TEXT_TX - TR
execute upsert_tx('unloads.for.target.error.email.subject', 'tr', 'Error happened during Unloading process');
execute upsert_tx('unloads.for.target.error.email.text', 'tr', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject', 'tr', 'Unloading process failure has been resolved');
execute upsert_tx('unloads.for.target.ok.email.text', 'tr', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');
commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

CREATE TABLE "TLGNR_ITRACK_STATUS_BS"
	(	"ITRACK_STATUS_CD" NUMBER(1,0) NOT NULL ENABLE,
		 "ITRACK_STATUS_NAME" VARCHAR2(40 CHAR),
		 "CTL_ACT_CD" NUMBER(1,0) NOT NULL ENABLE,
		 "CTL_CRE_UID" NUMBER(12,0) DEFAULT 1 NOT NULL ENABLE,
		 "CTL_CRE_TS" TIMESTAMP (6) DEFAULT systimestamp NOT NULL ENABLE,
		 "CTL_MOD_UID" NUMBER(12,0) DEFAULT 1 NOT NULL ENABLE,
		 "CTL_MOD_TS" TIMESTAMP (6) DEFAULT systimestamp NOT NULL ENABLE,
		 PRIMARY KEY ("ITRACK_STATUS_CD"));

COMMENT ON COLUMN "TLGNR_ITRACK_STATUS_BS"."ITRACK_STATUS_CD" IS 'Itrack Status code';
COMMENT ON COLUMN "TLGNR_ITRACK_STATUS_BS"."ITRACK_STATUS_NAME" IS 'Full name of the Itrack Status';
COMMENT ON COLUMN "TLGNR_ITRACK_STATUS_BS"."CTL_ACT_CD" IS 'Active Flag. When new data is entered, only reference data with active flag are available for selection normally.';
COMMENT ON COLUMN "TLGNR_ITRACK_STATUS_BS"."CTL_CRE_UID" IS 'Technical field. The principal name (corresponding field of javax.security.Principal) of the user who created the record.';
COMMENT ON COLUMN "TLGNR_ITRACK_STATUS_BS"."CTL_CRE_TS" IS 'Technical field. The time when the record got created.';
COMMENT ON COLUMN "TLGNR_ITRACK_STATUS_BS"."CTL_MOD_UID" IS 'Technical field. The principal name (corresponding field of javax.security.Principal) of the user who last modified the record.';
COMMENT ON COLUMN "TLGNR_ITRACK_STATUS_BS"."CTL_MOD_TS" IS 'Technical field. The time when the record was last modified.';

@../../refdata/tlgnr_itrack_status_bs.sql

ALTER TABLE TLGNC_ORDER ADD ITRACK_STATUS_CD NUMBER(1);
ALTER TABLE         TLGNC_ORDER
	ADD CONSTRAINT  FK_TLGNR_ORDER_ITRACK_STATUS
    FOREIGN KEY     (ITRACK_STATUS_CD)
    REFERENCES      TLGNR_ITRACK_STATUS_BS (ITRACK_STATUS_CD);

UPDATE TLGNC_ORDER
SET ITRACK_STATUS_CD = 2
WHERE CHECK_UNLOAD_FLAG = 1;

ALTER TABLE TLGNC_ORDER
DROP COLUMN CHECK_UNLOAD_FLAG;
prompt ************************************************************************
prompt *** END CRHLOGON-1409 Toll Fee
prompt ************************************************************************
