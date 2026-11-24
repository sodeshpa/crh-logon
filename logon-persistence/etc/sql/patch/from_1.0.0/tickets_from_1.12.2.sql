----------------------------------------------------------------------------------------------------------------|
-- CRHLOGON-1444 Itrack alerting finetuning                                                                                |
----------------------------------------------------------------------------------------------------------------|

prompt '---------- CRHLOGON-1444 -  refdata/tlgnr_text.sql ----------'

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
execute upsert_bs('unloading.different.location.email.subject', 1, 1);
execute upsert_bs('unloading.different.location.email.text', 3, 1);
execute upsert_bs('unloading.different.location.sms.text', 2, 1);
execute upsert_bs('unloads.for.target.error.email.subject', 1, 1);
execute upsert_bs('unloads.for.target.error.email.text', 2, 1);
execute upsert_bs('unloads.for.target.ok.email.subject', 1, 1);
execute upsert_bs('unloads.for.target.ok.email.text', 2, 1);
execute upsert_bs('unloads.for.target.deadline.email.subject', 1, 1);
execute upsert_bs('unloads.for.target.deadline.email.text', 2, 1);
execute upsert_bs('unloads.for.target.reloaded.email.subject', 1, 1);
execute upsert_bs('unloads.for.target.reloaded.email.text', 2, 1);
execute upsert_bs('unloads.for.target.conn.back.email.subject', 1, 1);
execute upsert_bs('unloads.for.target.conn.back.email.text', 2, 1);


execute upsert_tx('unloading.different.location.email.subject', 'en', '[iTrack] Unloading happened at a different location - {0}');
execute upsert_tx('unloading.different.location.email.text', 'en', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}, Shipto information received: {2}');
execute upsert_tx('unloading.different.location.sms.text', 'en', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.error.email.subject', 'en', '[iTrack] iTrack error occurred - {0}');
execute upsert_tx('unloads.for.target.error.email.text', 'en', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject', 'en', '[iTrack] iTrack error had been resolved - {0}');
execute upsert_tx('unloads.for.target.ok.email.text', 'en', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.deadline.email.subject', 'en', '[iTrack] No unload information received - {0}');
execute upsert_tx('unloads.for.target.deadline.email.text', 'en', 'No unload information was received until the  given deadline, thus the order had been set to delivered in Logon. Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.conn.back.email.subject', 'en', '[iTrack] Connection recovered - {0}');
execute upsert_tx('unloads.for.target.conn.back.email.text', 'en', ' Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject', 'en', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text', 'en', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');


execute upsert_tx('unloading.different.location.email.subject',  'de', '[iTrack] Unloading happened at a different location - {0}');
execute upsert_tx('unloading.different.location.email.text',  'de', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}, Shipto information received: {2}');
execute upsert_tx('unloading.different.location.sms.text',  'de', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.error.email.subject',  'de', '[iTrack] iTrack error occurred - {0}');
execute upsert_tx('unloads.for.target.error.email.text',  'de', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject',  'de', '[iTrack] iTrack error had been resolved - {0}');
execute upsert_tx('unloads.for.target.ok.email.text',  'de', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.deadline.email.subject',  'de', '[iTrack] No unload information received - {0}');
execute upsert_tx('unloads.for.target.deadline.email.text',  'de', 'No unload information was received until the  given deadline, thus the order had been set to delivered in Logon. Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.conn.back.email.subject',  'de', '[iTrack] Connection recovered - {0}');
execute upsert_tx('unloads.for.target.conn.back.email.text',  'de', ' Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject',  'de', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text',  'de', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');

execute upsert_tx('unloading.different.location.email.subject',  'fr', '[iTrack] Unloading happened at a different location - {0}');
execute upsert_tx('unloading.different.location.email.text',  'fr', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}, Shipto information received: {2}');
execute upsert_tx('unloading.different.location.sms.text',  'fr', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.error.email.subject',  'fr', '[iTrack] iTrack error occurred - {0}');
execute upsert_tx('unloads.for.target.error.email.text',  'fr', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject',  'fr', '[iTrack] iTrack error had been resolved - {0}');
execute upsert_tx('unloads.for.target.ok.email.text',  'fr', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.deadline.email.subject',  'fr', '[iTrack] No unload information received - {0}');
execute upsert_tx('unloads.for.target.deadline.email.text',  'fr', 'No unload information was received until the  given deadline, thus the order had been set to delivered in Logon. Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.conn.back.email.subject',  'fr', '[iTrack] Connection recovered - {0}');
execute upsert_tx('unloads.for.target.conn.back.email.text',  'fr', ' Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject',  'fr', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text',  'fr', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');

execute upsert_tx('unloading.different.location.email.subject',  'it', '[iTrack] Unloading happened at a different location - {0}');
execute upsert_tx('unloading.different.location.email.text',  'it', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}, Shipto information received: {2}');
execute upsert_tx('unloading.different.location.sms.text',  'it', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.error.email.subject',  'it', '[iTrack] iTrack error occurred - {0}');
execute upsert_tx('unloads.for.target.error.email.text',  'it', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject',  'it', '[iTrack] iTrack error had been resolved - {0}');
execute upsert_tx('unloads.for.target.ok.email.text',  'it', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.deadline.email.subject',  'it', '[iTrack] No unload information received - {0}');
execute upsert_tx('unloads.for.target.deadline.email.text',  'it', 'No unload information was received until the  given deadline, thus the order had been set to delivered in Logon. Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.conn.back.email.subject',  'it', '[iTrack] Connection recovered - {0}');
execute upsert_tx('unloads.for.target.conn.back.email.text',  'it', ' Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject',  'it', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text',  'it', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');


execute upsert_tx('unloading.different.location.email.subject',  'es', '[iTrack] Unloading happened at a different location - {0}');
execute upsert_tx('unloading.different.location.email.text',  'es', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}, Shipto information received: {2}');
execute upsert_tx('unloading.different.location.sms.text',  'es', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.error.email.subject',  'es', '[iTrack] iTrack error occurred - {0}');
execute upsert_tx('unloads.for.target.error.email.text',  'es', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject',  'es', '[iTrack] iTrack error had been resolved - {0}');
execute upsert_tx('unloads.for.target.ok.email.text',  'es', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.deadline.email.subject',  'es', '[iTrack] No unload information received - {0}');
execute upsert_tx('unloads.for.target.deadline.email.text',  'es', 'No unload information was received until the  given deadline, thus the order had been set to delivered in Logon. Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.conn.back.email.subject',  'es', '[iTrack] Connection recovered - {0}');
execute upsert_tx('unloads.for.target.conn.back.email.text',  'es', ' Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject',  'es', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text',  'es', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');


execute upsert_tx('unloading.different.location.email.subject',  'cs', '[iTrack] Unloading happened at a different location - {0}');
execute upsert_tx('unloading.different.location.email.text',  'cs', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}, Shipto information received: {2}');
execute upsert_tx('unloading.different.location.sms.text',  'cs', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.error.email.subject',  'cs', '[iTrack] iTrack error occurred - {0}');
execute upsert_tx('unloads.for.target.error.email.text',  'cs', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject',  'cs', '[iTrack] iTrack error had been resolved - {0}');
execute upsert_tx('unloads.for.target.ok.email.text',  'cs', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.deadline.email.subject',  'cs', '[iTrack] No unload information received - {0}');
execute upsert_tx('unloads.for.target.deadline.email.text',  'cs', 'No unload information was received until the  given deadline, thus the order had been set to delivered in Logon. Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.conn.back.email.subject',  'cs', '[iTrack] Connection recovered - {0}');
execute upsert_tx('unloads.for.target.conn.back.email.text',  'cs', ' Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject',  'cs', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text',  'cs', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');

execute upsert_tx('unloading.different.location.email.subject',  'sk', '[iTrack] Unloading happened at a different location - {0}');
execute upsert_tx('unloading.different.location.email.text',  'sk', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}, Shipto information received: {2}');
execute upsert_tx('unloading.different.location.sms.text',  'sk', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.error.email.subject',  'sk', '[iTrack] iTrack error occurred - {0}');
execute upsert_tx('unloads.for.target.error.email.text',  'sk', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject',  'sk', '[iTrack] iTrack error had been resolved - {0}');
execute upsert_tx('unloads.for.target.ok.email.text',  'sk', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.deadline.email.subject',  'sk', '[iTrack] No unload information received - {0}');
execute upsert_tx('unloads.for.target.deadline.email.text',  'sk', 'No unload information was received until the  given deadline, thus the order had been set to delivered in Logon. Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.conn.back.email.subject',  'sk', '[iTrack] Connection recovered - {0}');
execute upsert_tx('unloads.for.target.conn.back.email.text',  'sk', ' Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject',  'sk', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text',  'sk', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');


execute upsert_tx('unloading.different.location.email.subject',  'hu', '[iTrack] Unloading happened at a different location - {0}');
execute upsert_tx('unloading.different.location.email.text',  'hu', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}, Shipto information received: {2}');
execute upsert_tx('unloading.different.location.sms.text',  'hu', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.error.email.subject',  'hu', '[iTrack] iTrack error occurred - {0}');
execute upsert_tx('unloads.for.target.error.email.text',  'hu', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject',  'hu', '[iTrack] iTrack error had been resolved - {0}');
execute upsert_tx('unloads.for.target.ok.email.text',  'hu', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.deadline.email.subject',  'hu', '[iTrack] No unload information received - {0}');
execute upsert_tx('unloads.for.target.deadline.email.text',  'hu', 'No unload information was received until the  given deadline, thus the order had been set to delivered in Logon. Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.conn.back.email.subject',  'hu', '[iTrack] Connection recovered - {0}');
execute upsert_tx('unloads.for.target.conn.back.email.text',  'hu', ' Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject',  'hu', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text',  'hu', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');


execute upsert_tx('unloading.different.location.email.subject',  'ro', '[iTrack] Unloading happened at a different location - {0}');
execute upsert_tx('unloading.different.location.email.text',  'ro', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}, Shipto information received: {2}');
execute upsert_tx('unloading.different.location.sms.text',  'ro', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.error.email.subject',  'ro', '[iTrack] iTrack error occurred - {0}');
execute upsert_tx('unloads.for.target.error.email.text',  'ro', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject',  'ro', '[iTrack] iTrack error had been resolved - {0}');
execute upsert_tx('unloads.for.target.ok.email.text',  'ro', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.deadline.email.subject',  'ro', '[iTrack] No unload information received - {0}');
execute upsert_tx('unloads.for.target.deadline.email.text',  'ro', 'No unload information was received until the  given deadline, thus the order had been set to delivered in Logon. Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.conn.back.email.subject',  'ro', '[iTrack] Connection recovered - {0}');
execute upsert_tx('unloads.for.target.conn.back.email.text',  'ro', ' Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject',  'ro', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text',  'ro', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');

execute upsert_tx('unloading.different.location.email.subject',  'bg', '[iTrack] Unloading happened at a different location - {0}');
execute upsert_tx('unloading.different.location.email.text',  'bg', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}, Shipto information received: {2}');
execute upsert_tx('unloading.different.location.sms.text',  'bg', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.error.email.subject',  'bg', '[iTrack] iTrack error occurred - {0}');
execute upsert_tx('unloads.for.target.error.email.text',  'bg', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject',  'bg', '[iTrack] iTrack error had been resolved - {0}');
execute upsert_tx('unloads.for.target.ok.email.text',  'bg', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.deadline.email.subject',  'bg', '[iTrack] No unload information received - {0}');
execute upsert_tx('unloads.for.target.deadline.email.text',  'bg', 'No unload information was received until the  given deadline, thus the order had been set to delivered in Logon. Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.conn.back.email.subject',  'bg', '[iTrack] Connection recovered - {0}');
execute upsert_tx('unloads.for.target.conn.back.email.text',  'bg', ' Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject',  'bg', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text',  'bg', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');

execute upsert_tx('unloading.different.location.email.subject',  'ru', '[iTrack] Unloading happened at a different location - {0}');
execute upsert_tx('unloading.different.location.email.text',  'ru', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}, Shipto information received: {2}');
execute upsert_tx('unloading.different.location.sms.text',  'ru', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.error.email.subject',  'ru', '[iTrack] iTrack error occurred - {0}');
execute upsert_tx('unloads.for.target.error.email.text',  'ru', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject',  'ru', '[iTrack] iTrack error had been resolved - {0}');
execute upsert_tx('unloads.for.target.ok.email.text',  'ru', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.deadline.email.subject',  'ru', '[iTrack] No unload information received - {0}');
execute upsert_tx('unloads.for.target.deadline.email.text',  'ru', 'No unload information was received until the  given deadline, thus the order had been set to delivered in Logon. Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.conn.back.email.subject',  'ru', '[iTrack] Connection recovered - {0}');
execute upsert_tx('unloads.for.target.conn.back.email.text',  'ru', ' Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject',  'ru', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text',  'ru', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');

execute upsert_tx('unloading.different.location.email.subject',  'pl', '[iTrack] Unloading happened at a different location - {0}');
execute upsert_tx('unloading.different.location.email.text',  'pl', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}, Shipto information received: {2}');
execute upsert_tx('unloading.different.location.sms.text',  'pl', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.error.email.subject',  'pl', '[iTrack] iTrack error occurred - {0}');
execute upsert_tx('unloads.for.target.error.email.text',  'pl', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject',  'pl', '[iTrack] iTrack error had been resolved - {0}');
execute upsert_tx('unloads.for.target.ok.email.text',  'pl', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.deadline.email.subject',  'pl', '[iTrack] No unload information received - {0}');
execute upsert_tx('unloads.for.target.deadline.email.text',  'pl', 'No unload information was received until the  given deadline, thus the order had been set to delivered in Logon. Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.conn.back.email.subject',  'pl', '[iTrack] Connection recovered - {0}');
execute upsert_tx('unloads.for.target.conn.back.email.text',  'pl', ' Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject',  'pl', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text',  'pl', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');

execute upsert_tx('unloading.different.location.email.subject',  'az', '[iTrack] Unloading happened at a different location - {0}');
execute upsert_tx('unloading.different.location.email.text',  'az', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}, Shipto information received: {2}');
execute upsert_tx('unloading.different.location.sms.text',  'az', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.error.email.subject',  'az', '[iTrack] iTrack error occurred - {0}');
execute upsert_tx('unloads.for.target.error.email.text',  'az', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject',  'az', '[iTrack] iTrack error had been resolved - {0}');
execute upsert_tx('unloads.for.target.ok.email.text',  'az', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.deadline.email.subject',  'az', '[iTrack] No unload information received - {0}');
execute upsert_tx('unloads.for.target.deadline.email.text',  'az', 'No unload information was received until the  given deadline, thus the order had been set to delivered in Logon. Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.conn.back.email.subject',  'az', '[iTrack] Connection recovered - {0}');
execute upsert_tx('unloads.for.target.conn.back.email.text',  'az', ' Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject',  'az', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text',  'az', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');

execute upsert_tx('unloading.different.location.email.subject',  'hr', '[iTrack] Unloading happened at a different location - {0}');
execute upsert_tx('unloading.different.location.email.text',  'hr', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}, Shipto information received: {2}');
execute upsert_tx('unloading.different.location.sms.text',  'hr', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.error.email.subject',  'hr', '[iTrack] iTrack error occurred - {0}');
execute upsert_tx('unloads.for.target.error.email.text',  'hr', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject',  'hr', '[iTrack] iTrack error had been resolved - {0}');
execute upsert_tx('unloads.for.target.ok.email.text',  'hr', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.deadline.email.subject',  'hr', '[iTrack] No unload information received - {0}');
execute upsert_tx('unloads.for.target.deadline.email.text',  'hr', 'No unload information was received until the  given deadline, thus the order had been set to delivered in Logon. Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.conn.back.email.subject',  'hr', '[iTrack] Connection recovered - {0}');
execute upsert_tx('unloads.for.target.conn.back.email.text',  'hr', ' Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject',  'hr', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text',  'hr', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');

execute upsert_tx('unloading.different.location.email.subject',  'sr', '[iTrack] Unloading happened at a different location - {0}');
execute upsert_tx('unloading.different.location.email.text',  'sr', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}, Shipto information received: {2}');
execute upsert_tx('unloading.different.location.sms.text',  'sr', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.error.email.subject',  'sr', '[iTrack] iTrack error occurred - {0}');
execute upsert_tx('unloads.for.target.error.email.text',  'sr', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject',  'sr', '[iTrack] iTrack error had been resolved - {0}');
execute upsert_tx('unloads.for.target.ok.email.text',  'sr', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.deadline.email.subject',  'sr', '[iTrack] No unload information received - {0}');
execute upsert_tx('unloads.for.target.deadline.email.text',  'sr', 'No unload information was received until the  given deadline, thus the order had been set to delivered in Logon. Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.conn.back.email.subject',  'sr', '[iTrack] Connection recovered - {0}');
execute upsert_tx('unloads.for.target.conn.back.email.text',  'sr', ' Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject',  'sr', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text',  'sr', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');

execute upsert_tx('unloading.different.location.email.subject',  'tr', '[iTrack] Unloading happened at a different location - {0}');
execute upsert_tx('unloading.different.location.email.text',  'tr', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}, Shipto information received: {2}');
execute upsert_tx('unloading.different.location.sms.text',  'tr', 'Order might be unloaded at a different shipto. Please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.error.email.subject',  'tr', '[iTrack] iTrack error occurred - {0}');
execute upsert_tx('unloads.for.target.error.email.text',  'tr', 'Error occured during gathering information about the unload in I-Data! Cause of the issue is: {0}! Truck plate number: {1}, Order Number: {2}');
execute upsert_tx('unloads.for.target.ok.email.subject',  'tr', '[iTrack] iTrack error had been resolved - {0}');
execute upsert_tx('unloads.for.target.ok.email.text',  'tr', 'The Unloading process has been finished successfully! Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.deadline.email.subject',  'tr', '[iTrack] No unload information received - {0}');
execute upsert_tx('unloads.for.target.deadline.email.text',  'tr', 'No unload information was received until the  given deadline, thus the order had been set to delivered in Logon. Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.conn.back.email.subject',  'tr', '[iTrack] Connection recovered - {0}');
execute upsert_tx('unloads.for.target.conn.back.email.text',  'tr', ' Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject',  'tr', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text',  'tr', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

----------------------------------------------------------------------------------------------------------------|
-- CRHLOGON-1440 Audit itrackstatus cd
----------------------------------------------------------------------------------------------------------------|

create or replace
procedure insert_missing_audit_config
is
  l_audit_bizevent_cd NUMBER:= 0;
BEGIN
	dbms_output.enable(100000);
  	select audit_bizevent_config_cd into l_audit_bizevent_cd from tlgnr_audit_bizevent_config_bs where audit_bizevent_config_cd=1046;
    EXCEPTION
          WHEN no_data_found THEN
                      dbms_output.put_line('Inserting 1046 audit_bizevent_config_bs and tx entries');

      insert into tlgnr_audit_bizevent_config_bs (audit_bizevent_config_cd,ctl_act_cd,ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1046,1,1,systimestamp,1,systimestamp);
      insert into tlgnr_audit_bizevent_config_tx (audit_bizevent_config_cd,lang_cd,txt,ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1046,'en','iTrack status',1, systimestamp,1,systimestamp);
insert into tlgnr_audit_bizevent_config_tx (audit_bizevent_config_cd,lang_cd,txt,ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1046,'de','iTrack status',1, systimestamp,1,systimestamp);
insert into tlgnr_audit_bizevent_config_tx (audit_bizevent_config_cd,lang_cd,txt,ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1046,'fr','iTrack status',1, systimestamp,1,systimestamp);
insert into tlgnr_audit_bizevent_config_tx (audit_bizevent_config_cd,lang_cd,txt,ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1046,'it','iTrack status',1, systimestamp,1,systimestamp);
insert into tlgnr_audit_bizevent_config_tx (audit_bizevent_config_cd,lang_cd,txt,ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1046,'es','iTrack status',1, systimestamp,1,systimestamp);
insert into tlgnr_audit_bizevent_config_tx (audit_bizevent_config_cd,lang_cd,txt,ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1046,'cs','iTrack status',1, systimestamp,1,systimestamp);
insert into tlgnr_audit_bizevent_config_tx (audit_bizevent_config_cd,lang_cd,txt,ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1046,'sk','iTrack status',1, systimestamp,1,systimestamp);
insert into tlgnr_audit_bizevent_config_tx (audit_bizevent_config_cd,lang_cd,txt,ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1046,'hu','iTrack státusz',1, systimestamp,1,systimestamp);
insert into tlgnr_audit_bizevent_config_tx (audit_bizevent_config_cd,lang_cd,txt,ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1046,'ro','iTrack status',1, systimestamp,1,systimestamp);
insert into tlgnr_audit_bizevent_config_tx (audit_bizevent_config_cd,lang_cd,txt,ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1046,'bg','iTrack status',1, systimestamp,1,systimestamp);
insert into tlgnr_audit_bizevent_config_tx (audit_bizevent_config_cd,lang_cd,txt,ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1046,'ru','iTrack status',1, systimestamp,1,systimestamp);
insert into tlgnr_audit_bizevent_config_tx (audit_bizevent_config_cd,lang_cd,txt,ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1046,'pl','iTrack status',1, systimestamp,1,systimestamp);
insert into tlgnr_audit_bizevent_config_tx (audit_bizevent_config_cd,lang_cd,txt,ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1046,'az','iTrack status',1, systimestamp,1,systimestamp);
insert into tlgnr_audit_bizevent_config_tx (audit_bizevent_config_cd,lang_cd,txt,ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1046,'hr','iTrack status',1, systimestamp,1,systimestamp);
insert into tlgnr_audit_bizevent_config_tx (audit_bizevent_config_cd,lang_cd,txt,ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1046,'sr','iTrack status',1, systimestamp,1,systimestamp);
insert into tlgnr_audit_bizevent_config_tx (audit_bizevent_config_cd,lang_cd,txt,ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1046,'tr','iTrack status',1, systimestamp,1,systimestamp);
  END;
/
show errors;

execute insert_missing_audit_config;
commit;
drop procedure insert_missing_audit_config;