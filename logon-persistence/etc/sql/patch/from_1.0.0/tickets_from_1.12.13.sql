----------------------------------------------------------------------------------------------------------------|
-- CRHLOGON-1453 iTrack CR: Support for unloads which last less than 15 minutes                                                                              |
----------------------------------------------------------------------------------------------------------------|

prompt '---------- CRHLOGON-1453 -  refdata/tlgnr_text.sql ----------'

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


prompt INSERTING into TLGNR_TEXT_BS
execute upsert_bs('unloads.for.target.short.unload.email.subject', 1, 1);
execute upsert_bs('unloads.for.target.short.unload.email.text', 2, 1);

execute upsert_tx('unloads.for.target.short.unload.email.subject', 'en', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'en', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

execute upsert_tx('unloads.for.target.short.unload.email.subject', 'de', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'de', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

execute upsert_tx('unloads.for.target.short.unload.email.subject', 'fr', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'fr', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

execute upsert_tx('unloads.for.target.short.unload.email.subject', 'it', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'it', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

execute upsert_tx('unloads.for.target.short.unload.email.subject', 'es', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'es', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

execute upsert_tx('unloads.for.target.short.unload.email.subject', 'cs', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'cs', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

execute upsert_tx('unloads.for.target.short.unload.email.subject', 'sk', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'sk', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

execute upsert_tx('unloads.for.target.short.unload.email.subject', 'hu', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'hu', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

execute upsert_tx('unloads.for.target.short.unload.email.subject', 'ro', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'ro', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

execute upsert_tx('unloads.for.target.short.unload.email.subject', 'bg', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'bg', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

execute upsert_tx('unloads.for.target.short.unload.email.subject', 'ru', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'ru', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

execute upsert_tx('unloads.for.target.short.unload.email.subject', 'pl', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'pl', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

execute upsert_tx('unloads.for.target.short.unload.email.subject', 'az', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'az', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

execute upsert_tx('unloads.for.target.short.unload.email.subject', 'hr', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'hr', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

execute upsert_tx('unloads.for.target.short.unload.email.subject', 'sr', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'sr', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

execute upsert_tx('unloads.for.target.short.unload.email.subject', 'tr', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'tr', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;