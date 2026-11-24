prompt '---------- new_tlgnr_text.sql ----------'

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

execute upsert_bs('load.confirm.email.subject', 1, 1);
execute upsert_bs('load.confirm.email.order_item', 3, 1);
execute upsert_bs('load.confirm.email.message', 15, 1);
execute upsert_bs('load.confirm.fax.message', 15, 1);
execute upsert_bs('load.confirm.sms.order_item', 3, 1);
execute upsert_bs('load.confirm.sms.message', 15, 1);
execute upsert_bs('load.confirm.sms.message.1', 15, 1);
execute upsert_bs('load.confirm.sms.message.2', 15, 1);

prompt INSERTING into TLGNR_TEXT_TX - EN

execute upsert_tx('load.confirm.email.subject', 'en', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'en', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'en', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.fax.message', 'en', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.sms.message', 'en', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'en', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'en', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');

prompt INSERTING into TLGNR_TEXT_TX - DE

execute upsert_tx('load.confirm.email.subject', 'de', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'de', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'de', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.fax.message', 'de', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.sms.message', 'de', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'de', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'de', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');

prompt INSERTING into TLGNR_TEXT_TX - FR

execute upsert_tx('load.confirm.email.subject', 'fr', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'fr', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'fr', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.fax.message', 'fr', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.sms.message', 'fr', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'fr', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'fr', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');

prompt INSERTING into TLGNR_TEXT_TX - IT

execute upsert_tx('load.confirm.email.subject', 'it', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'it', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'it', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.fax.message', 'it', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.sms.message', 'it', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'it', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'it', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');

prompt INSERTING into TLGNR_TEXT_TX - ES

execute upsert_tx('load.confirm.sms.message', 'es', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'es', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'es', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.fax.message', 'es', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.email.subject', 'es', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'es', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'es', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');

prompt INSERTING into TLGNR_TEXT_TX - CS

execute upsert_tx('load.confirm.email.subject', 'cs', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'cs', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'cs', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.fax.message', 'cs', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.sms.message', 'cs', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'cs', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'cs', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');

prompt INSERTING into TLGNR_TEXT_TX - SK

execute upsert_tx('load.confirm.email.subject', 'sk', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'sk', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'sk', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.fax.message', 'sk', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.sms.message', 'sk', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'sk', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'sk', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');

prompt INSERTING into TLGNR_TEXT_TX - HU

execute upsert_tx('load.confirm.email.subject', 'hu', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'hu', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'hu', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.fax.message', 'hu', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.sms.message', 'hu', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'hu', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'hu', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');

prompt INSERTING into TLGNR_TEXT_TX - RO

execute upsert_tx('load.confirm.email.subject', 'ro', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'ro', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'ro', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.fax.message', 'ro', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.sms.message', 'ro', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'ro', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'ro', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');

prompt INSERTING into TLGNR_TEXT_TX - BG

execute upsert_tx('load.confirm.email.subject', 'bg', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'bg', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'bg', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.fax.message', 'bg', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.sms.message', 'bg', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'bg', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'bg', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');

prompt INSERTING into TLGNR_TEXT_TX - RU

execute upsert_tx('load.confirm.email.subject', 'ru', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'ru', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'ru', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.fax.message', 'ru', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.sms.message', 'ru', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'ru', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'ru', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');

prompt INSERTING into TLGNR_TEXT_TX - PL

execute upsert_tx('load.confirm.email.subject', 'pl', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'pl', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'pl', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.fax.message', 'pl', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.sms.message', 'pl', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'pl', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'pl', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');

prompt INSERTING into TLGNR_TEXT_TX - AZ

execute upsert_tx('load.confirm.email.subject', 'az', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'az', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'az', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.fax.message', 'az', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.sms.message', 'az', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'az', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'az', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');

prompt INSERTING into TLGNR_TEXT_TX - HR

execute upsert_tx('load.confirm.email.subject', 'hr', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'hr', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'hr', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.fax.message', 'hr', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.sms.message', 'hr', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'hr', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'hr', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');

prompt INSERTING into TLGNR_TEXT_TX - SR
execute upsert_tx('load.confirm.email.subject', 'sr', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'sr', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'sr', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.fax.message', 'sr', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.sms.message', 'sr', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'sr', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'sr', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');

prompt INSERTING into TLGNR_TEXT_TX - TR
execute upsert_tx('load.confirm.email.subject', 'tr', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'tr', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'tr', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.fax.message', 'tr', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.sms.message', 'tr', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'tr', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'tr', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');


commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

