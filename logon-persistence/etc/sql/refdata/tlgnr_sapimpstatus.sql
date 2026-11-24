-- $Revision$
-- $Date$
--
prompt '---------- refdata/tlgnr_sapimpstatus.sql ----------'

set define off
set serveroutput on

-------------------------------------------------------------------------------
-- is_different_varchar2
-------------------------------------------------------------------------------
-- compares two varchars.
-- They are identical either if a) both are null, b) they are equal by "="
-- They are different if a) either is null and the other not, b) different by "="
create or replace function is_different_varchar2 (
  i_a in varchar2,
  i_b in varchar2)
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
  i_a in number,
  i_b in number)
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
-- tlgnr_sapimpstatus_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_sapimpstatus_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_sapimpstatus_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_sapimpstatus_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.sapimpstatus_cd)
    into v_exists_flag
    from tlgnr_sapimpstatus_bs bs
    where bs.sapimpstatus_cd = p_sapimpstatus_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_sapimpstatus_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_sapimpstatus_bs set
          /* sapimpstatus_cd: cannot update because this is the key */
          ctl_act_cd = p_ctl_act_cd
        where
          sapimpstatus_cd = p_sapimpstatus_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_sapimpstatus_cd || ').');
      end if;
    else
      insert into tlgnr_sapimpstatus_bs (
        sapimpstatus_cd,
        ctl_act_cd
      ) values (
        p_sapimpstatus_cd,
        p_ctl_act_cd
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_sapimpstatus_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_sapimpstatus_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_sapimpstatus_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_sapimpstatus_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_sapimpstatus_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.sapimpstatus_cd) into v_exists_bs_flag
    from tlgnr_sapimpstatus_bs bs
    where bs.sapimpstatus_cd = p_sapimpstatus_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.sapimpstatus_cd) into v_exists_flag
      from tlgnr_sapimpstatus_tx tx
      where tx.sapimpstatus_cd = p_sapimpstatus_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_sapimpstatus_tx set
          /* p_sapimpstatus_cd and lang_cd: cannot update because these are the keys */
          txt = p_text
        where
          sapimpstatus_cd = p_sapimpstatus_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_sapimpstatus_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_sapimpstatus_tx (
          sapimpstatus_cd,
          lang_cd,
          txt
        ) values (
          p_sapimpstatus_cd,
          p_lang_cd,
          p_text
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_sapimpstatus_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_sapimpstatus_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_SAPIMPSTATUS_BS
execute upsert_bs( 0,  1);
execute upsert_bs( 1,  1);
execute upsert_bs( 2,  1);
execute upsert_bs( 3,  1);
execute upsert_bs( 8,  1);
execute upsert_bs( 9,  1);
execute upsert_bs(10,  1);
execute upsert_bs(11,  1);
execute upsert_bs(12,  1);
execute upsert_bs(19,  1);
execute upsert_bs(20,  1);

prompt INSERTING into TLGNR_SAPIMPSTATUS_TX - EN
-- Texts defined by AdNovum, based on needs of PL/SQL import procedures.
-- '3=partially' is needed because of import of BP which has to process in several iterations.
execute upsert_tx( 0 , 'en', 'Not yet processed');
execute upsert_tx( 1 , 'en', 'Skipped root element');
execute upsert_tx( 2 , 'en', 'Failed');
execute upsert_tx( 3 , 'en', 'Blocked');
execute upsert_tx( 8 , 'en', 'Blocked final');
execute upsert_tx( 9 , 'en', 'Failed final');
execute upsert_tx(10 , 'en', 'Processing partner functions');
execute upsert_tx(11 , 'en', 'Skipped partner functions');
execute upsert_tx(12 , 'en', 'Failed (PF)');
execute upsert_tx(19 , 'en', 'Failed final (PF)');
execute upsert_tx(20 , 'en', 'Imported to LOGON');

prompt INSERTING into TLGNR_SAPIMPSTATUS_TX - DE (ENGLISH COPY)
execute upsert_tx( 0 , 'de', 'Not yet processed');
execute upsert_tx( 1 , 'de', 'Skipped root element');
execute upsert_tx( 2 , 'de', 'Failed');
execute upsert_tx( 3 , 'de', 'Blocked');
execute upsert_tx( 8 , 'de', 'Blocked final');
execute upsert_tx( 9 , 'de', 'Failed final');
execute upsert_tx(10 , 'de', 'Processing partner functions');
execute upsert_tx(11 , 'de', 'Skipped partner functions');
execute upsert_tx(12 , 'de', 'Failed (PF)');
execute upsert_tx(19 , 'de', 'Failed final (PF)');
execute upsert_tx(20 , 'de', 'Imported to LOGON');

prompt INSERTING into TLGNR_SAPIMPSTATUS_TX - FR (ENGLISH COPY)
execute upsert_tx( 0 , 'fr', 'Not yet processed');
execute upsert_tx( 1 , 'fr', 'Skipped root element');
execute upsert_tx( 2 , 'fr', 'Failed');
execute upsert_tx( 3 , 'fr', 'Blocked');
execute upsert_tx( 8 , 'fr', 'Blocked final');
execute upsert_tx( 9 , 'fr', 'Failed final');
execute upsert_tx(10 , 'fr', 'Processing partner functions');
execute upsert_tx(11 , 'fr', 'Skipped partner functions');
execute upsert_tx(12 , 'fr', 'Failed (PF)');
execute upsert_tx(19 , 'fr', 'Failed final (PF)');
execute upsert_tx(20 , 'fr', 'Imported to LOGON');

prompt INSERTING into TLGNR_SAPIMPSTATUS_TX - IT (ENGLISH COPY)
execute upsert_tx( 0 , 'it', 'Not yet processed');
execute upsert_tx( 1 , 'it', 'Skipped root element');
execute upsert_tx( 2 , 'it', 'Failed');
execute upsert_tx( 3 , 'it', 'Blocked');
execute upsert_tx( 8 , 'it', 'Blocked final');
execute upsert_tx( 9 , 'it', 'Failed final');
execute upsert_tx(10 , 'it', 'Processing partner functions');
execute upsert_tx(11 , 'it', 'Skipped partner functions');
execute upsert_tx(12 , 'it', 'Failed (PF)');
execute upsert_tx(19 , 'it', 'Failed final (PF)');
execute upsert_tx(20 , 'it', 'Imported to LOGON');

prompt INSERTING into TLGNR_SAPIMPSTATUS_TX - ES (ENGLISH COPY)
execute upsert_tx( 0 , 'es', 'Not yet processed');
execute upsert_tx( 1 , 'es', 'Skipped root element');
execute upsert_tx( 2 , 'es', 'Failed');
execute upsert_tx( 3 , 'es', 'Blocked');
execute upsert_tx( 8 , 'es', 'Blocked final');
execute upsert_tx( 9 , 'es', 'Failed final');
execute upsert_tx(10 , 'es', 'Processing partner functions');
execute upsert_tx(11 , 'es', 'Skipped partner functions');
execute upsert_tx(12 , 'es', 'Failed (PF)');
execute upsert_tx(19 , 'es', 'Failed final (PF)');
execute upsert_tx(20 , 'es', 'Imported to LOGON');

prompt INSERTING into TLGNR_SAPIMPSTATUS_TX - CS (ENGLISH COPY)
execute upsert_tx( 0 , 'cs', 'Not yet processed');
execute upsert_tx( 1 , 'cs', 'Skipped root element');
execute upsert_tx( 2 , 'cs', 'Failed');
execute upsert_tx( 3 , 'cs', 'Blocked');
execute upsert_tx( 8 , 'cs', 'Blocked final');
execute upsert_tx( 9 , 'cs', 'Failed final');
execute upsert_tx(10 , 'cs', 'Processing partner functions');
execute upsert_tx(11 , 'cs', 'Skipped partner functions');
execute upsert_tx(12 , 'cs', 'Failed (PF)');
execute upsert_tx(19 , 'cs', 'Failed final (PF)');
execute upsert_tx(20 , 'cs', 'Imported to LOGON');

prompt INSERTING into TLGNR_SAPIMPSTATUS_TX - SK (ENGLISH COPY)
execute upsert_tx( 0 , 'sk', 'Not yet processed');
execute upsert_tx( 1 , 'sk', 'Skipped root element');
execute upsert_tx( 2 , 'sk', 'Failed');
execute upsert_tx( 3 , 'sk', 'Blocked');
execute upsert_tx( 8 , 'sk', 'Blocked final');
execute upsert_tx( 9 , 'sk', 'Failed final');
execute upsert_tx(10 , 'sk', 'Processing partner functions');
execute upsert_tx(11 , 'sk', 'Skipped partner functions');
execute upsert_tx(12 , 'sk', 'Failed (PF)');
execute upsert_tx(19 , 'sk', 'Failed final (PF)');
execute upsert_tx(20 , 'sk', 'Imported to LOGON');

prompt INSERTING into TLGNR_SAPIMPSTATUS_TX - HU (ENGLISH COPY)
execute upsert_tx( 0 , 'hu', 'Not yet processed');
execute upsert_tx( 1 , 'hu', 'Skipped root element');
execute upsert_tx( 2 , 'hu', 'Failed');
execute upsert_tx( 3 , 'hu', 'Blocked');
execute upsert_tx( 8 , 'hu', 'Blocked final');
execute upsert_tx( 9 , 'hu', 'Failed final');
execute upsert_tx(10 , 'hu', 'Processing partner functions');
execute upsert_tx(11 , 'hu', 'Skipped partner functions');
execute upsert_tx(12 , 'hu', 'Failed (PF)');
execute upsert_tx(19 , 'hu', 'Failed final (PF)');
execute upsert_tx(20 , 'hu', 'Imported to LOGON');

prompt INSERTING into TLGNR_SAPIMPSTATUS_TX - RO (ENGLISH COPY)
execute upsert_tx( 0 , 'ro', 'Not yet processed');
execute upsert_tx( 1 , 'ro', 'Skipped root element');
execute upsert_tx( 2 , 'ro', 'Failed');
execute upsert_tx( 3 , 'ro', 'Blocked');
execute upsert_tx( 8 , 'ro', 'Blocked final');
execute upsert_tx( 9 , 'ro', 'Failed final');
execute upsert_tx(10 , 'ro', 'Processing partner functions');
execute upsert_tx(11 , 'ro', 'Skipped partner functions');
execute upsert_tx(12 , 'ro', 'Failed (PF)');
execute upsert_tx(19 , 'ro', 'Failed final (PF)');
execute upsert_tx(20 , 'ro', 'Imported to LOGON');

prompt INSERTING into TLGNR_SAPIMPSTATUS_TX - BG (ENGLISH COPY)
execute upsert_tx( 0 , 'bg', 'Not yet processed');
execute upsert_tx( 1 , 'bg', 'Skipped root element');
execute upsert_tx( 2 , 'bg', 'Failed');
execute upsert_tx( 3 , 'bg', 'Blocked');
execute upsert_tx( 8 , 'bg', 'Blocked final');
execute upsert_tx( 9 , 'bg', 'Failed final');
execute upsert_tx(10 , 'bg', 'Processing partner functions');
execute upsert_tx(11 , 'bg', 'Skipped partner functions');
execute upsert_tx(12 , 'bg', 'Failed (PF)');
execute upsert_tx(19 , 'bg', 'Failed final (PF)');
execute upsert_tx(20 , 'bg', 'Imported to LOGON');

prompt INSERTING into TLGNR_SAPIMPSTATUS_TX - RU (ENGLISH COPY)
execute upsert_tx( 0 , 'ru', 'Not yet processed');
execute upsert_tx( 1 , 'ru', 'Skipped root element');
execute upsert_tx( 2 , 'ru', 'Failed');
execute upsert_tx( 3 , 'ru', 'Blocked');
execute upsert_tx( 8 , 'ru', 'Blocked final');
execute upsert_tx( 9 , 'ru', 'Failed final');
execute upsert_tx(10 , 'ru', 'Processing partner functions');
execute upsert_tx(11 , 'ru', 'Skipped partner functions');
execute upsert_tx(12 , 'ru', 'Failed (PF)');
execute upsert_tx(19 , 'ru', 'Failed final (PF)');
execute upsert_tx(20 , 'ru', 'Imported to LOGON');

prompt INSERTING into TLGNR_SAPIMPSTATUS_TX - PL (ENGLISH COPY)
execute upsert_tx( 0 , 'pl', 'Not yet processed');
execute upsert_tx( 1 , 'pl', 'Skipped root element');
execute upsert_tx( 2 , 'pl', 'Failed');
execute upsert_tx( 3 , 'pl', 'Blocked');
execute upsert_tx( 8 , 'pl', 'Blocked final');
execute upsert_tx( 9 , 'pl', 'Failed final');
execute upsert_tx(10 , 'pl', 'Processing partner functions');
execute upsert_tx(11 , 'pl', 'Skipped partner functions');
execute upsert_tx(12 , 'pl', 'Failed (PF)');
execute upsert_tx(19 , 'pl', 'Failed final (PF)');
execute upsert_tx(20 , 'pl', 'Imported to LOGON');

prompt INSERTING into TLGNR_SAPIMPSTATUS_TX - AZ (ENGLISH COPY)
execute upsert_tx( 0 , 'az', 'Not yet processed');
execute upsert_tx( 1 , 'az', 'Skipped root element');
execute upsert_tx( 2 , 'az', 'Failed');
execute upsert_tx( 3 , 'az', 'Blocked');
execute upsert_tx( 8 , 'az', 'Blocked final');
execute upsert_tx( 9 , 'az', 'Failed final');
execute upsert_tx(10 , 'az', 'Processing partner functions');
execute upsert_tx(11 , 'az', 'Skipped partner functions');
execute upsert_tx(12 , 'az', 'Failed (PF)');
execute upsert_tx(19 , 'az', 'Failed final (PF)');
execute upsert_tx(20 , 'az', 'Imported to LOGON');

prompt INSERTING into TLGNR_SAPIMPSTATUS_TX - HR (ENGLISH COPY)
execute upsert_tx( 0 , 'hr', 'Not yet processed');
execute upsert_tx( 1 , 'hr', 'Skipped root element');
execute upsert_tx( 2 , 'hr', 'Failed');
execute upsert_tx( 3 , 'hr', 'Blocked');
execute upsert_tx( 8 , 'hr', 'Blocked final');
execute upsert_tx( 9 , 'hr', 'Failed final');
execute upsert_tx(10 , 'hr', 'Processing partner functions');
execute upsert_tx(11 , 'hr', 'Skipped partner functions');
execute upsert_tx(12 , 'hr', 'Failed (PF)');
execute upsert_tx(19 , 'hr', 'Failed final (PF)');
execute upsert_tx(20 , 'hr', 'Imported to LOGON');

prompt INSERTING into TLGNR_SAPIMPSTATUS_TX - SR (ENGLISH COPY)
execute upsert_tx( 0 , 'sr', 'Not yet processed');
execute upsert_tx( 1 , 'sr', 'Skipped root element');
execute upsert_tx( 2 , 'sr', 'Failed');
execute upsert_tx( 3 , 'sr', 'Blocked');
execute upsert_tx( 8 , 'sr', 'Blocked final');
execute upsert_tx( 9 , 'sr', 'Failed final');
execute upsert_tx(10 , 'sr', 'Processing partner functions');
execute upsert_tx(11 , 'sr', 'Skipped partner functions');
execute upsert_tx(12 , 'sr', 'Failed (PF)');
execute upsert_tx(19 , 'sr', 'Failed final (PF)');
execute upsert_tx(20 , 'sr', 'Imported to LOGON');

prompt INSERTING into TLGNR_SAPIMPSTATUS_TX - TR
-- Texts defined by AdNovum, based on needs of PL/SQL import procedures.
-- '3=partially' is needed because of import of BP which has to process in several iterations.
execute upsert_tx( 0 , 'tr', 'Not yet processed');
execute upsert_tx( 1 , 'tr', 'Skipped root element');
execute upsert_tx( 2 , 'tr', 'Failed');
execute upsert_tx( 3 , 'tr', 'Blocked');
execute upsert_tx( 8 , 'tr', 'Blocked final');
execute upsert_tx( 9 , 'tr', 'Failed final');
execute upsert_tx(10 , 'tr', 'Processing partner functions');
execute upsert_tx(11 , 'tr', 'Skipped partner functions');
execute upsert_tx(12 , 'tr', 'Failed (PF)');
execute upsert_tx(19 , 'tr', 'Failed final (PF)');
execute upsert_tx(20 , 'tr', 'Imported to LOGON');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

