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


prompt INSERTING into TLGNR_TEXT_BS
execute upsert_bs('help.useradmin.authorization_dep', 0, 1);
execute upsert_bs('pdf.collective_deliverynote.footer.text', 1, 1);
execute upsert_bs('pdf.collective_deliverynote.footer.title', 1, 1);
execute upsert_bs('pdf.deliverynote.footer.text', 0, 1);
execute upsert_bs('pdf.deliverynote.footer.title', 0, 1);
execute upsert_bs('pdf.deliverynote.ust-id', 0, 1);
execute upsert_bs('pdf.deliverynote.bimodal.truck', 0, 1);
execute upsert_bs('pdf.deliverynote.bimodal.qty', 0, 1);
execute upsert_bs('pdf.deliverynote.bimodal_ord_wagon_inf',0, 1);
execute upsert_bs('pdf.deliverynote.customs', 0, 1);
execute upsert_bs('pdf.delivnote.eichbeleg.intro', 0, 1);
execute upsert_bs('pdf.delivnote.eichbeleg.tare', 3, 1);
execute upsert_bs('pdf.delivnote.eichbeleg.gross', 3, 1);
execute upsert_bs('pdf.deliverynote.lds', 0, 1);
execute upsert_bs('pdf.deliverynote.lt', 0, 1);
execute upsert_bs('pdf.deliverynote.custrefno', 0, 1);
execute upsert_bs('pdf.deliverynote.contractno', 0, 1);
execute upsert_bs('pdf.deliverynote.deliverytype', 1, 1);
execute upsert_bs('marketinfo.email.subject', 3, 1);
execute upsert_bs('marketinfo.email.message', 23, 1);
execute upsert_bs('order.confirm.email.subject', 1, 1);
execute upsert_bs('order.confirm.email.order_item', 3, 1);
execute upsert_bs('order.confirm.email.message', 16, 1);
execute upsert_bs('order.confirm.fax.order_item', 3, 1);
execute upsert_bs('order.confirm.fax.message', 15, 1);
execute upsert_bs('order.confirm.sms.order_item', 3, 1);
execute upsert_bs('order.confirm.sms.message', 15, 1);
execute upsert_bs('order.confirm.sms.message.1', 15, 1);
execute upsert_bs('order.confirm.sms.message.2', 15, 1);

execute upsert_bs('load.confirm.email.subject', 1, 1);
execute upsert_bs('load.confirm.email.order_item', 3, 1);
execute upsert_bs('load.confirm.email.message', 15, 1);
execute upsert_bs('load.confirm.fax.message', 15, 1);
execute upsert_bs('load.confirm.sms.order_item', 3, 1);
execute upsert_bs('load.confirm.sms.message', 15, 1);
execute upsert_bs('load.confirm.sms.message.1', 15, 1);
execute upsert_bs('load.confirm.sms.message.2', 15, 1);

execute upsert_bs('order.delivery.email.subject', 1, 1);
execute upsert_bs('order.delivery.email.text', 1, 1);
execute upsert_bs('special.rail.order.email',18, 1);
execute upsert_bs('special.rail.order.email.offer',0, 1);
execute upsert_bs('special.rail.order.email.rep',0, 1);
execute upsert_bs('special.rail.order.email.subj',2, 1);
execute upsert_bs('new_password1.email.subject', 1, 1);
execute upsert_bs('new_password1.email.text', 2, 1);
execute upsert_bs('new_password2.email.subject', 0, 1);
execute upsert_bs('new_password2.email.text', 1, 1);
execute upsert_bs('password_reset_token.email.subject', 1, 1);
execute upsert_bs('password_reset_token.email.text', 2, 1);
execute upsert_bs('daily_reports.email.subject', 2, 1);
execute upsert_bs('daily_reports.email.text', 2, 1);
execute upsert_bs('staging_area_report.email.subject', 2, 1);
execute upsert_bs('staging_area_report.email.text', 1, 1);
execute upsert_bs('unlock_password.email.subject', 1, 1);
execute upsert_bs('unlock_password.email.text', 5, 1);
execute upsert_bs('unlock_pwdsucc.email.subject', 1, 1);
execute upsert_bs('unlock_pwdsucc.email.text', 4, 1);
execute upsert_bs('order.deallocate.sms.message', 1, 1);
execute upsert_bs('order.released.sms.message', 12, 1);
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
execute upsert_bs('unloads.for.target.short.unload.email.subject', 1, 1);
execute upsert_bs('unloads.for.target.short.unload.email.text', 2, 1);

prompt INSERTING into TLGNR_TEXT_TX - EN
execute upsert_tx('help.useradmin.authorization_dep', 'en', 'Company Code -> Sales Areas, Sales Areas -> Plants, Plants -> Shippingpoint' || chr(13) || chr(10) || 'Customers -> Ship To');
execute upsert_tx('pdf.collective_deliverynote.footer.text', 'en', 'Collective del. note footer text.');
execute upsert_tx('pdf.collective_deliverynote.footer.title', 'en', 'Collective del. note footer title');
execute upsert_tx('pdf.deliverynote.footer.title', 'en', 'The forwarding agent assures that');
execute upsert_tx('pdf.deliverynote.bimodal.truck', 'en', 'Bimodal delivery from rail order');
execute upsert_tx('pdf.deliverynote.bimodal.qty', 'en', 'Total amount of railway wagons');
execute upsert_tx('pdf.deliverynote.bimodal_ord_wagon_inf','en','Bimodal delivery of rail order (0), wagon (1), total amount on railway wagon: (2)');
execute upsert_tx('pdf.deliverynote.customs', 'en', 'Periodical collective application according to ZG 42 Abs 1  Bes. c, with request/application for preferential clearance. The exporter of the goods, to which are being referred to in this commercial document, states, that these goods are preferential CE originating products.');
execute upsert_tx('pdf.deliverynote.footer.text', 'en', 'The required permission to transport this freight is given. The load does not exceed the legally allowed gross weight of the vehicle. The delivery is intended for the declared goods recipient.');
execute upsert_tx('pdf.deliverynote.ust-id', 'en', 'DE142385541');
execute upsert_tx('pdf.delivnote.eichbeleg.intro', 'en', 'Measurements of free calibrated additional components. Basic calibration measurements can be reviewed.');
execute upsert_tx('pdf.delivnote.eichbeleg.tare', 'en', 'Basic calibration number: tare weight measurement {0}: {1} ({2}{3})');
execute upsert_tx('pdf.delivnote.eichbeleg.gross', 'en', 'Gross weight measurement: {0}: {1} ({2}{3})');
execute upsert_tx('pdf.deliverynote.lds', 'en', 'LDS');
execute upsert_tx('pdf.deliverynote.lt', 'en', 'LT');
execute upsert_tx('pdf.deliverynote.custrefno', 'en', 'Customer Ref. No');
execute upsert_tx('pdf.deliverynote.contractno', 'en', 'Contract');
execute upsert_tx('pdf.deliverynote.deliverytype', 'en', ' ');
execute upsert_tx('marketinfo.email.subject', 'en', 'Market information from {0} {1} ({2})');
execute upsert_tx('marketinfo.email.message', 'en', 'Dear recipient,'  || chr(13) || chr(10)  || chr(13) || chr(10) || 'The following market message was just created:' || chr(13) || chr(10) || chr(13) || chr(10) || '---' || chr(13) || chr(10) || '  {0}' || chr(13) || chr(10) || '---' || chr(13) || chr(10) || chr(13) || chr(10) || 'Sender Information:' || chr(13) || chr(10) || '  Sender:         {1} {2} ({3})' || chr(13) || chr(10) || '  Vehicle:        {4}, {5}' || chr(13) || chr(10) || '  Date:           {6,date,dd.MM.yy} {6,time,HH:mm}' || chr(13) || chr(10) || '  Place:          {7}maps?q={8},{9}&num=1&t=h&ie=UTF8&iwloc=near' || chr(13) || chr(10) || '  Mobile:         {10}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Current LOGON Order:' || chr(13) || chr(10) || '  Number:         {11}' || chr(13) || chr(10) || '  Customer:       {12} {13}' || chr(13) || chr(10) || '  Location:       {14} {15}, {16} {17}' || chr(13) || chr(10) || '  Region:         {18}/{19}' || chr(13) || chr(10) || '  Sales District: {20}' || chr(13) || chr(10) || '  Sales Office:   {21}' || chr(13) || chr(10) || '  Sales Group:    {22}' || chr(13) || chr(10) || chr(13) || chr(10) || 'This email was created automatically because of a PDA message. A direct response to this email Adress is not possible.' || chr(13) || chr(10) || 'For further information please contact the author of this message.' || chr(13) || chr(10) || chr(13) || chr(10) || 'with best regards,' || chr(13) || chr(10) || '{1} {2}');
execute upsert_tx('order.confirm.email.subject', 'en', 'Crh Order Confirmation {0}');
execute upsert_tx('order.confirm.email.order_item', 'en', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.email.message', 'en', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Many thanks for your order at Crh.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Order confirmation {0}:'  || chr(13) || chr(10) || chr(13) || chr(10) || 'Your reference {15}' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Delivery Time' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Delivery Location' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.email.subject', 'en', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'en', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'en', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.fax.order_item', 'en', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.fax.message', 'en', 'Crh Order Confirmation {0}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Many thanks for your order at Crh.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Order confirmation {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Delivery Time' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Delivery Location' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.fax.message', 'en', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.sms.order_item', 'en', '{0} {1} {2} ');
execute upsert_tx('order.confirm.sms.message', 'en', 'Crh order confirmation {0} / {1}/ {6} / {2,date,dd.MM.yy} {2,time,HH:mm} - {3,time,HH:mm} - Thank you.');
execute upsert_tx('order.confirm.sms.message.1', 'en', 'Crh order confirmation (1/2) {0} / {1}/ {6}');
execute upsert_tx('order.confirm.sms.message.2', 'en', 'Crh order confirmation (2/2) {2,date,dd.MM.yy} {2,time,HH:mm} - {3,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message', 'en', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'en', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'en', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('order.delivery.email.subject', 'en', 'Crh Delivery Note {0}');
execute upsert_tx('order.delivery.email.text', 'en', 'Dear recipient,' || chr(13) || chr(10)  || chr(13) || chr(10) || 'Attached please find the delivery note to your order {0}.' || chr(13) || chr(10) || chr(13) || chr(10) || 'with best regards,' || chr(13) || chr(10) || 'Crh');
execute upsert_tx('special.rail.order.email.subj','en','Order for wagon {0} ({1})');
execute upsert_tx('special.rail.order.email.offer','en','Please send us your offer');
execute upsert_tx('special.rail.order.email.rep','en','Repair/Maintenance Order: {0}' || chr(13) || chr(10) || 'Damage codes: {1}'|| chr(13) || chr(10));
execute upsert_tx('special.rail.order.email','en','Dear recipient,'|| chr(13) || chr(10) || chr(13) || chr(10) || 'The following order {0} was just created:'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Wagon:'|| chr(13) || chr(10) || '  {1}/{2}'|| chr(13) || chr(10) || '  {3} {4}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Recipient:'|| chr(13) || chr(10) || '  {5}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Transport:'|| chr(13) || chr(10) || '  {6} ({7}) -> {8} ({9})'|| chr(13) || chr(10) || chr(13) || chr(10) || '{10}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Remarks:'|| chr(13) || chr(10) || '{11}'|| chr(13) || chr(10) || chr(13) || chr(10) || chr(13) || chr(10) || '{12}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Contact person: {13}'|| chr(13) || chr(10) || 'with kind regards,'|| chr(13) || chr(10) || '  {14}'|| chr(13) || chr(10) || chr(13) || chr(10) || '------------------------------'|| chr(13) || chr(10) || '  {15}'|| chr(13) || chr(10) || '  {16}'|| chr(13) || chr(10) || '  {17}'|| chr(13) || chr(10) || 'This email was generated automatically. In case of questions please use the contact details above or reply to rail-ch@crh.com');
execute upsert_tx('new_password1.email.subject', 'en', 'Crh Logon Password reset ({0})');
execute upsert_tx('new_password1.email.text', 'en', 'Your new password for user ''''{0}'''' ({1}) will be sent in a separate email.');
execute upsert_tx('new_password2.email.subject', 'en', 'Password reset');
execute upsert_tx('new_password2.email.text', 'en', 'Your new password is:' || chr(13) || chr(10) || '{0}');

execute upsert_tx('password_reset_token.email.subject', 'en', 'Password reset instructions for user ''''{0}''''');
execute upsert_tx('password_reset_token.email.text', 'en', 'Dear user,'  || chr(13) || chr(10) || chr(13) || chr(10) || 'A password reset request has been made for the user {0}.'|| chr(13) || chr(10) || 'To reset your password, click the following link or paste it in your browser:' || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || chr(13) || chr(10) || 'If you did not request a password reset, please ignore this e-mail.' || chr(13) || chr(10) || chr(13) || chr(10) || 'With best regards,' || chr(13) || chr(10) || 'Crh');

execute upsert_tx('daily_reports.email.subject', 'en', 'Logon daily report {0,date,dd.MM.yy}');
execute upsert_tx('daily_reports.email.text', 'en', 'Please find the report ''''{1}'''' attached.' || chr(13) || chr(10) || chr(13) || chr(10) || '{2}');
execute upsert_tx('staging_area_report.email.subject', 'en', 'Staging area report ({0}) {1,date,dd.MM.yy}');
execute upsert_tx('staging_area_report.email.text', 'en', 'Please find the report ''''{0}'''' attached.');
execute upsert_tx('unlock_password.email.subject', 'en', 'Crh LOGON external login locked ({0})');
execute upsert_tx('unlock_password.email.text', 'en', 'Dear user,' || chr(13) || chr(10) || chr(13) || chr(10) || 'your account {0} in {1} has been locked on {2}, at {3} due to too many failed login attempts. You can unlock your account and reset your password via the following link:'|| chr(13) || chr(10) || chr(13) || chr(10) || '{4}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'You will receive your new password you have registered in {1} via email.' || chr(13) || chr(10) || chr(13) || chr(10) || 'with best regards,' || chr(13) || chr(10) || 'Crh');
execute upsert_tx('unlock_pwdsucc.email.subject', 'en', 'Crh LOGON external login unlocked ({0})');
execute upsert_tx('unlock_pwdsucc.email.text', 'en', 'Dear user,' || chr(13) || chr(10) || chr(13) || chr(10) || 'your account {0} in {1} was successfully unlocked on {2}, at {3}.' || chr(13) || chr(10) || chr(13) || chr(10) || 'with best regards,' || chr(13) || chr(10) || 'Crh');
execute upsert_tx('order.deallocate.sms.message', 'en', 'ORDER {0} has been deleted');
execute upsert_tx('order.released.sms.message', 'en', 'ORDER {0} / Start: {1} / Time: {2}({3}) - {4}({5}) / Ship-to: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');
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
execute upsert_tx('unloads.for.target.conn.back.email.text', 'en', 'Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject', 'en', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text', 'en', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.short.unload.email.subject', 'en', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'en', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');



prompt INSERTING into TLGNR_TEXT_TX - DE
execute upsert_tx('help.useradmin.authorization_dep', 'de', 'Company Code -> Vertriebsbereiche, Vertriebsbereiche -> Werke, Werke -> Versandstellen' || chr(13) || chr(10) || 'Kunden -> Warenempfänger');
execute upsert_tx('pdf.collective_deliverynote.footer.text', 'de', 'Collective del. note footer text (de - translate).');
execute upsert_tx('pdf.collective_deliverynote.footer.title', 'de', 'Collective del. note footer title (de - translate)');
execute upsert_tx('pdf.deliverynote.bimodal.truck', 'de', 'Kombilieferung von Bahnauftrag ');
execute upsert_tx('pdf.deliverynote.bimodal.qty', 'de', 'Menge Bahnwagen Total');
execute upsert_tx('pdf.deliverynote.bimodal_ord_wagon_inf','de','Kombilieferung von Bahnauftrag {0}, Bahnwagen {1}, Menge Bahnwagen Total: {2}');
execute upsert_tx('pdf.deliverynote.customs', 'de', 'Periodische Sammelanmeldung nach ZG 42 Abs 1  Bes. c,  mit Antrag auf Präferenzabfertigung. Der Ausführer der Waren, auf die sich dieses Handlespapier bezieht, erklärt, dass diese Waren soweit nicht anders angegeben, präferenzbegünstigte CE Urpsprungswaren sind.');
execute upsert_tx('pdf.deliverynote.footer.text', 'de', 'Die erforderliche Genehmigung für die Beförderung dieser Ladung liegt vor. Die Ladung überschreitet nicht das gesetzlich zulässige Gesamtgewicht des Fahrzeuges. Die Lieferung ist für den angegebenen Empfangsort bestimmt.');
execute upsert_tx('pdf.deliverynote.ust-id', 'de', 'DE142385541');
execute upsert_tx('pdf.delivnote.eichbeleg.intro', 'de', 'Messwerte aus frei programmierbarer Zusatzeinrichtung. Die geeichten Messwerte können eingesehen werden.');
execute upsert_tx('pdf.delivnote.eichbeleg.tare', 'de', 'Eichbelegnummer: Taramessung {0}: {1} ({2}{3})');
execute upsert_tx('pdf.delivnote.eichbeleg.gross', 'de', 'Bruttomessung: {0}: {1} ({2}{3})');
execute upsert_tx('pdf.deliverynote.lds', 'de', 'LDS');
execute upsert_tx('pdf.deliverynote.lt', 'de', 'LT');
execute upsert_tx('pdf.deliverynote.custrefno', 'de', 'Kundenreferenz');
execute upsert_tx('pdf.deliverynote.contractno', 'de', 'Vertrag');
execute upsert_tx('pdf.deliverynote.deliverytype', 'de', ' ');
execute upsert_tx('marketinfo.email.subject', 'de', 'Marktmeldung von {0} {1} ({2})');
execute upsert_tx('marketinfo.email.message', 'de', 'Sehr geehrter Empfänger,'  || chr(13) || chr(10)  || chr(13) || chr(10) || 'Soeben wurde folgende Marktmeldung erfasst:'  || chr(13) || chr(10)  || chr(13) || chr(10) || '---' || chr(13) || chr(10) || '  {0}' || chr(13) || chr(10) || '---' || chr(13) || chr(10) || chr(13) || chr(10) || 'Absenderinfromationen:' || chr(13) || chr(10) || '  Absender:       {1} {2} ({3})' || chr(13) || chr(10) || '  Fahrzeug:       {4}, {5}' || chr(13) || chr(10) || '  Datum:          {6,date,dd.MM.yy} {6,time,HH:mm}' || chr(13) || chr(10) || '  Ort:            {7}maps?q={8},{9}&num=1&t=h&ie=UTF8&iwloc=near' || chr(13) || chr(10) || '  Telefon:        {10}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Aktueller LOGON-Auftrag:' || chr(13) || chr(10) || '  Nummer:         {11}' || chr(13) || chr(10) || '  Kunde:          {12} {13}' || chr(13) || chr(10) || '  Lieferort:      {14} {15}, {16} {17}' || chr(13) || chr(10) || '  Region:         {18}/{19}' || chr(13) || chr(10) || '  Sales District: {20}' || chr(13) || chr(10) || '  Sales Office:   {21}' || chr(13) || chr(10) || '  Sales Group:    {22}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dieses Email wurde automatisch aufgrund einer PDA-Meldung generiert. Eine direkte Antwort auf diese Email-Adresse ist nicht möglich.' || chr(13) || chr(10) || 'Für weitere Informationen wenden sie sich bitte direkt an den Verfasser dieser Meldung.' || chr(13) || chr(10) || chr(13) || chr(10) || 'mit freundlichen Grüssen,' || chr(13) || chr(10) || '{1} {2}');
execute upsert_tx('order.confirm.email.subject', 'de', 'Crh Auftragsbestätigung {0}');
execute upsert_tx('order.confirm.email.order_item', 'de', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.email.message', 'de', 'Sehr geehrter Empfänger,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Vielen Dank für Ihre Bestellung bei Crh.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Auftragsbestätigung {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || 'Ihre Referenz {15}' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Lieferzeit' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Lieferort' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Besteller:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Weitere Details zu Ihrem Auftrag sowie den aktuellen Auftragsstatus finden Sie unter https://lpdv-logon.crh.com' || chr(13) || chr(10) || chr(13) || chr(10) || 'mit freundlichen Grüssen,' || chr(13) || chr(10) || 'Ihre Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'Diese Email wurde automatisch generiert. Eine direkte Antwort auf diese Email-Adresse ist nicht möglich. Bei Fragen wenden Sie sich bitte an Ihre Hotline oder den zuständigen Verkäufer.');
execute upsert_tx('load.confirm.email.subject', 'de', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'de', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'de', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.fax.order_item', 'de', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.fax.message', 'de', 'Crh Auftragsbestätigung' || chr(13) || chr(10) || chr(13) || chr(10) || 'Sehr geehrter Empfänger,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Vielen Dank für Ihre Bestellung bei Crh.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Auftragsbestätigung {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Lieferzeit' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Lieferort' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Besteller:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Weitere Details zu Ihrem Auftrag sowie den aktuellen Auftragsstatus finden Sie unter https://logon.crh.com' || chr(13) || chr(10) || chr(13) || chr(10) || 'mit freundlichen Grüssen,' || chr(13) || chr(10) || 'Ihre Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'Bei Fragen wenden Sie sich bitte an Ihre Hotline oder den zuständigen Verkäufer.');
execute upsert_tx('load.confirm.fax.message', 'de', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.sms.order_item', 'de', '{0} {1} {2} ');
execute upsert_tx('order.confirm.sms.message', 'de', 'Crh Bestellbestätigung {0} / {1}/ {6} / {2,date,dd.MM.yy} {2,time,HH:mm} - {3,time,HH:mm} - Besten Dank.');
execute upsert_tx('order.confirm.sms.message.1', 'de', 'Crh Bestellbestätigung (1/2) {0} / {1}/ {6}');
execute upsert_tx('order.confirm.sms.message.2', 'de', 'Crh Bestellbestätigung (2/2) {2,date,dd.MM.yy} {2,time,HH:mm} - {3,time,HH:mm} - Besten Dank.');
execute upsert_tx('load.confirm.sms.message', 'de', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'de', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'de', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('order.delivery.email.subject', 'de', 'Crh Lieferschein {0}');
execute upsert_tx('order.delivery.email.text', 'de', 'Sehr geehrter Empfänger,' || chr(13) || chr(10)  || chr(13) || chr(10) || 'Beiliegend der Lieferschein zu Ihrer Bestellung {0}.' || chr(13) || chr(10) || chr(13) || chr(10) || 'mit freundlichen Grüssen,' || chr(13) || chr(10) || 'Ihre Crh');
execute upsert_tx('special.rail.order.email.subj','de','Auftrag für Bahnwagen {0} ({1})');
execute upsert_tx('special.rail.order.email.offer','de','mit Bitte um eine Offertzustellung');
execute upsert_tx('special.rail.order.email.rep','de','Reparatur/Wartungsauftrag: {0}' || chr(13) || chr(10) || 'Schadencode: {1}'|| chr(13) || chr(10) );
execute upsert_tx('special.rail.order.email','de','Sehr geehrter Empfänger,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Soeben wurde der Auftrag {0} mit folgenden Daten erfasst oder aktualisiert:'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Bahnwagen:'|| chr(13) || chr(10) || '  {1}/{2}'|| chr(13) || chr(10) || '  {3} {4}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Empfänger:'|| chr(13) || chr(10) || '  {5}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Beföderungsauftrag:'|| chr(13) || chr(10) || '  {6} ({7}) -> {8} ({9})'|| chr(13) || chr(10) || chr(13) || chr(10) || '{10}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Bemerkung:'|| chr(13) || chr(10) || '{11}'|| chr(13) || chr(10) || chr(13) || chr(10) || chr(13) || chr(10) || '{12}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Gemeldet von: {13}'|| chr(13) || chr(10) || 'mit freundlichen Grüssen,'|| chr(13) || chr(10) || '  {14}'|| chr(13) || chr(10) || chr(13) || chr(10) || '------------------------------'|| chr(13) || chr(10) || '  {15}'|| chr(13) || chr(10) || '  {16}'|| chr(13) || chr(10) || '  {17}'|| chr(13) || chr(10) || 'Dieses Email wurde automatisch generiert. Bei Fragen wenden Sie sich bitte an obige Adresse oder an rail-ch@crh.com');
execute upsert_tx('new_password1.email.subject', 'de', 'Crh Logon Kennwort zurücksetzen ({0})');
execute upsert_tx('new_password1.email.text', 'de', 'Ihr neues Passwort für den Benutzer ''''{0}'''' von {1} wird im nächsten Email gesendet.');
execute upsert_tx('new_password2.email.subject', 'de', 'Kennwort zurücksetzen');
execute upsert_tx('new_password2.email.text', 'de', 'Ihr neues Passwort lautet:' || chr(13) || chr(10) || '{0}');
execute upsert_tx('password_reset_token.email.subject', 'de', 'Anleitung zum Zurücksetzen des Kennworts für Benutzer ''''{0}''''');
execute upsert_tx('password_reset_token.email.text', 'de', 'Liebe Benutzerin, lieber Benutzer,'  || chr(13) || chr(10) || chr(13) || chr(10) || 'Für den Benutzernamen {0} wurde ein Zurücksetzen des Passworts beantragt.'|| chr(13) || chr(10) || 'Um das Passwort zurückzusetzen, öffnen Sie bitte den folgenden Link in Ihrem Browser:' || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Falls Sie kein Zurücksetzen beantragt haben, können Sie diese e-mail einfach ignorieren.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Freundliche Grüsse,' || chr(13) || chr(10) || 'Crh');

execute upsert_tx('daily_reports.email.subject', 'de', 'Logon daily report {0,date,dd.MM.yy}');
execute upsert_tx('daily_reports.email.text', 'de', 'Im Anhang finden sie den Report ''''{1}'''' .' || chr(13) || chr(10) || chr(13) || chr(10) || '{2}');
execute upsert_tx('staging_area_report.email.subject', 'de', 'Staging area report ({0}) {1,date,dd.MM.yy}');
execute upsert_tx('staging_area_report.email.text', 'de', 'Sie finden den Report ''''{0}'''' im Anhang.');
execute upsert_tx('unlock_password.email.subject', 'de', 'Crh LOGON externer Login gesperrt ({0})');
execute upsert_tx('unlock_password.email.text', 'de', 'Liebe Benutzerin, lieber Benutzer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Ihr Account {0} auf {1} wurde aufgrund zu vielen fehlgeschlagenen Anmeldungen am {2}, {3} Uhr gesperrt. Sie können ihren Account unter folgendem Link entsperren und ihr Passwort zurücksetzen lassen:'|| chr(13) || chr(10) || chr(13) || chr(10) || '{4}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Das neue Passwort wird an die in {1} registrierte Emailadresse gesendet.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Freundliche Grüsse' || chr(13) || chr(10) || 'Crh');
execute upsert_tx('unlock_pwdsucc.email.subject', 'de', 'Crh LOGON externer Login entsperrt ({0})');
execute upsert_tx('unlock_pwdsucc.email.text', 'de', 'Liebe Benutzerin, lieber Benutzer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Ihr Account {0} auf {1} wurde am {2}, {3} Uhr erfolgreich entsperrt.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Freundliche Grüsse'|| chr(13) || chr(10) || 'Crh');
execute upsert_tx('order.deallocate.sms.message', 'de', 'ORDER {0} has been deleted');
execute upsert_tx('order.released.sms.message', 'de', 'ORDER {0} / Start: {1} / Time: {2}({3}) - {4}({5}) / Ship-to: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');
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
execute upsert_tx('unloads.for.target.conn.back.email.text',  'de', 'Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject',  'de', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text',  'de', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.short.unload.email.subject', 'de', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'de', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');


prompt INSERTING into TLGNR_TEXT_TX - FR
execute upsert_tx('help.useradmin.authorization_dep', 'fr', 'Company Code -> Sales Areas, Sales Areas -> Plants, Plants -> Shippingpoint' || chr(13) || chr(10) || 'Customers -> Ship To');
execute upsert_tx('pdf.collective_deliverynote.footer.text', 'fr', 'Collective del. note footer text (fr - translate).');
execute upsert_tx('pdf.collective_deliverynote.footer.title', 'fr', 'Collective del. note footer title (fr - translate)');
execute upsert_tx('pdf.deliverynote.footer.title', 'fr', 'Le transport est assuré avec l''accusé de réception');
execute upsert_tx('pdf.deliverynote.bimodal.truck', 'fr', 'Livraison bimodale de l''ordre ferroviaire');
execute upsert_tx('pdf.deliverynote.bimodal.qty', 'fr', 'Quantité totale des wagons ferroviaires');
execute upsert_tx('pdf.deliverynote.bimodal_ord_wagon_inf','fr','Livraison bimodale de l''ordre ferroviaire (0), Wagon (1), quantité totale sur wagon: (2)');
execute upsert_tx('pdf.deliverynote.customs', 'fr', 'Demande collective périodique selon ZG 42 Abs 1 Bes. c, avec demande/application d''attestation préférentiel. L''exportateur des marchandises, référencé dans le présent document commercial, indique que ces biens sont produits préférentiels CE originaires.');
execute upsert_tx('pdf.deliverynote.footer.text', 'fr', 'L''autorisation nécessaire pour le transport est disponible. La redevance pour le poids du véhicule ne dépasse pas le maximum légal autorisé. La livraison est destinée à l''adresse de réception mentionné.');
execute upsert_tx('pdf.deliverynote.ust-id', 'fr', 'DE142385541');
execute upsert_tx('pdf.delivnote.eichbeleg.intro', 'fr', 'Mesures des composants supplémentaire de calibrage libre. Les mesures de calibrage de base peuvent être révisées.');
execute upsert_tx('pdf.delivnote.eichbeleg.tare', 'fr', 'Numéro de calibrage de base: mesure du poids de tare {0}: {1} ({2}{3})');
execute upsert_tx('pdf.delivnote.eichbeleg.gross', 'fr', 'Mesure de poids brut: {0}: {1} ({2}{3})');
execute upsert_tx('pdf.deliverynote.lds', 'fr', 'LDS');
execute upsert_tx('pdf.deliverynote.lt', 'fr', 'LT');
execute upsert_tx('pdf.deliverynote.custrefno', 'fr', 'Numéro de référence du client');
execute upsert_tx('pdf.deliverynote.contractno', 'fr', 'Contrat');
execute upsert_tx('pdf.deliverynote.deliverytype', 'fr', ' ');
execute upsert_tx('marketinfo.email.subject', 'fr', 'Informations sur le marché de {0} {1} ({2})');
execute upsert_tx('marketinfo.email.message', 'fr', 'Cher destinataire,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Le message suivant vient d''''être crée:' || chr(13) || chr(10) || chr(13) || chr(10) || '---' || chr(13) || chr(10) || '  {0}' || chr(13) || chr(10) || '---' || chr(13) || chr(10) || chr(13) || chr(10) || 'Information sur l''''expéditeur:' || chr(13) || chr(10) || '  Expéditeur:           {1} {2} ({3})' || chr(13) || chr(10) || '  Véhicule:             {4}, {5}' || chr(13) || chr(10) || '  Date:                 {6,date,dd.MM.yy} {6,time,HH:mm}' || chr(13) || chr(10) || '  Lieu:                 {7}maps?q={8},{9}&num=1&t=h&ie=UTF8&iwloc=near' || chr(13) || chr(10) || '  Téléphone:            {10}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Commande actuelle LOGON:' || chr(13) || chr(10) || '  Numéro:               {11}' || chr(13) || chr(10) || '  Client:               {12} {13}' || chr(13) || chr(10) || '  Adresse de livraison: {14} {15}, {16} {17}' || chr(13) || chr(10) || '  Région:               {18}/{19}' || chr(13) || chr(10) || '  Région de vente:      {20}' || chr(13) || chr(10) || '  Bureau de vente:      {21}' || chr(13) || chr(10) || '  Groupe de vente:      {22}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Ce Mail a été généré automatiquement par un message créé par le PDA. Une réponse directe sur cette adresse E-mail n''''est pas possible.' || chr(13) || chr(10) || 'Pour toutes informations complémentaires, veuillez contacter l''''auteur de ce message.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Meilleures salutations,' || chr(13) || chr(10) || '{1} {2}');
execute upsert_tx('order.confirm.email.subject', 'fr', 'Confirmation de commande CRH {0}');
execute upsert_tx('order.confirm.email.order_item', 'fr', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.email.message', 'fr', 'Cher destinataire,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Merci pour votre commande chez EQIOM.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Confirmation de commande {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || 'Votre référence {15}' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Délai de livraison' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Adresse Client' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Acheteur:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Des informations complémentaires ainsi que le statut actuel de votre commande sont disponibles sur https://lpdv-logon.crh.com' || chr(13) || chr(10) || chr(13) || chr(10) || 'Meilleures salutations,' || chr(13) || chr(10) || 'EQIOM' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'Ce Mail a été généré automatiquement. Une réponse directe sur cette adresse E-mail n''''est pas possible. Pour toutes informations complémentaires, veuillez contacter votre représentant des ventes.');
execute upsert_tx('load.confirm.email.subject', 'fr', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'fr', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'fr', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.fax.order_item', 'fr', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.fax.message', 'fr', 'Confirmation de commande CRH {0}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Cher destinataire,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Merci pour votre commande chez Crh.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Confirmation de commande {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Délai de livraison' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Adresse Client' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Acheteur:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Des informations complémentaires ainsi que le statut actuel de votre commande sont disponibles sur https://logon.crh.com' || chr(13) || chr(10) || chr(13) || chr(10) || 'Meilleures salutations,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'Pour toutes informations complémentaires, veuillez contacter notre Hotline +41 0800 55 95 97 ou votre représentant des ventes.');
execute upsert_tx('load.confirm.fax.message', 'fr', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.sms.order_item', 'fr', '{0} {1} {2} ');
execute upsert_tx('order.confirm.sms.message', 'fr', 'Confirmation de commande CRH {0} / {1}/ {6} / {2,date,dd.MM.yy} {2,time,HH:mm} - {3,time,HH:mm} - Merci.');
execute upsert_tx('order.confirm.sms.message.1', 'fr', 'Confirmation de commande CRH (1/2) {0} / {1}/ {6}');
execute upsert_tx('order.confirm.sms.message.2', 'fr', 'Confirmation de commande CRH (2/2) {2,date,dd.MM.yy} {2,time,HH:mm} - {3,time,HH:mm} - Merci.');
execute upsert_tx('load.confirm.sms.message', 'fr', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'fr', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'fr', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('order.delivery.email.subject', 'fr', 'Bulletin de livraison Crh {0}');
execute upsert_tx('order.delivery.email.text', 'fr', 'Cher destinataire,' || chr(13) || chr(10)  || chr(13) || chr(10) || 'Vous trouverez ci-joint le bulletin de livraison de votre commande {0}.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Meilleures salutations,' || chr(13) || chr(10) || 'Crh');
-- No translation in FR: special.rail.order.email.subj
-- No translation in FR: special.rail.order.email.offer
-- No translation in FR: special.rail.order.email.rep
-- No translation in FR: special.rail.order.email
execute upsert_tx('new_password1.email.subject', 'fr', 'Réinitialisation de mot de passe d''ouverture de session Crh ({0})');
execute upsert_tx('new_password1.email.text', 'fr', 'Votre nouveau mot de passe pour l''utilisateur ''''{0}'''' ({1}) sera envoyé dans un email séparé.');
execute upsert_tx('new_password2.email.subject', 'fr', 'Réinitialisation du mot de passe');
execute upsert_tx('new_password2.email.text', 'fr', 'Votre nouveau mot de passe est:' || chr(13) || chr(10) || '{0}');

-- LOGON-5538 Translations for "password reset" mails (fr,es,it)
execute upsert_tx('password_reset_token.email.subject', 'fr', 'Instructions pour la réinitialisation du mot de passe ''''{0}''''');
execute upsert_tx('password_reset_token.email.text', 'fr', 'Cher utilisateur,'  || chr(13) || chr(10) || chr(13) || chr(10) || 'Une demande de réinitialisation de mot de passe a été faite pour l''''utilisateur {0}.'|| chr(13) || chr(10) || 'Afin de réinitialiser votre mot de passe, veuillez cliquer sur le lien ou le copier dans la barre d''''adresse:' || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Si vous n''''avez pas fait de demande de réinitialisation de mot de passe, veuillez ignorer cet email.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Avec nos meilleures salutations,' || chr(13) || chr(10) || 'Crh');

execute upsert_tx('daily_reports.email.subject', 'fr', 'Rapport journalier de Logon {0,date,dd.MM.yy}');
execute upsert_tx('daily_reports.email.text', 'fr', 'Vous trouverez le rapport ''''{1}'''' attaché.' || chr(13) || chr(10) || chr(13) || chr(10) || '{2}');
execute upsert_tx('staging_area_report.email.subject', 'fr', 'Rapport du Staging Area ({0}) {1,date,dd.MM.yy}');
execute upsert_tx('staging_area_report.email.text', 'fr', 'Veuillez trouver en piece jointe le rapport ''''{0}''''.');
execute upsert_tx('unlock_password.email.subject', 'fr', 'Crh LOGON login externe verrouillé ({0})');
execute upsert_tx('unlock_password.email.text', 'fr', 'Cher utilisateur,' || chr(13) || chr(10) || chr(13) || chr(10) || 'votre compte {0} dans {1} a été verrouillé sur {2}, à {3} en raison de nombreuses tentatives de connexion infructueuses. Vous pouvez débloquer votre compte et réinitialiser votre mot de passe via le lien suivant:'|| chr(13) || chr(10) || chr(13) || chr(10) || '{4}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Vous recevrez votre nouveau mot de passe que vous avez enregistré en {1} via email.' || chr(13) || chr(10) || chr(13) || chr(10) || 'meilleures salutations,' || chr(13) || chr(10) || 'Crh');
execute upsert_tx('unlock_pwdsucc.email.subject', 'fr', 'Crh LOGON login externe déverrouillé ({0})');
execute upsert_tx('unlock_pwdsucc.email.text', 'fr', 'Cher utilisateur,' || chr(13) || chr(10) || chr(13) || chr(10) || 'votre compte {0} dans {1} a été déverrouillé avec succès sur {2}, au {3}.' || chr(13) || chr(10) || chr(13) || chr(10) || 'meilleures salutations,' || chr(13) || chr(10) || 'Crh');
execute upsert_tx('order.deallocate.sms.message', 'fr', 'ORDER {0} has been deleted');
execute upsert_tx('order.released.sms.message', 'fr', 'ORDER {0} / Start: {1} / Time: {2}({3}) - {4}({5}) / Ship-to: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');
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
execute upsert_tx('unloads.for.target.conn.back.email.text',  'fr', 'Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject',  'fr', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text',  'fr', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.short.unload.email.subject', 'fr', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'fr', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');


prompt INSERTING into TLGNR_TEXT_TX - IT
execute upsert_tx('help.useradmin.authorization_dep', 'it', 'Company Code -> Sales Areas, Sales Areas -> Plants, Plants -> Shippingpoint' || chr(13) || chr(10) || 'Customers -> Ship To');
execute upsert_tx('pdf.collective_deliverynote.footer.text', 'it', 'Collective del. note footer text (it - translate).');
execute upsert_tx('pdf.collective_deliverynote.footer.title', 'it', 'Collective del. note footer title (it - translate)');
execute upsert_tx('pdf.deliverynote.footer.title', 'it', 'Con la ricevuta si conferma l''avvenuto trasporto.');
execute upsert_tx('pdf.deliverynote.bimodal.truck', 'it', 'Spedizione bimodale di un ordine ferroviario ');
execute upsert_tx('pdf.deliverynote.bimodal.qty', 'it', 'Numero totale di vagone ferroviari');
execute upsert_tx('pdf.deliverynote.bimodal_ord_wagon_inf','it','Spedizione bimodale di un ordine ferroviario (0), vagone (1), numero totale di vagone ferroviari: (2)');
execute upsert_tx('pdf.deliverynote.customs', 'it', 'Periodica applicazione collettiva in accordo con ZG 42 Abs 1 Bes.c, con la richiesta di autorizzazione preferenziale. L''esportatore dei prodotti, al quale si fa riferimento in questo documentop commerciale, afferma, che questi prodotti sono prodotti originali CE.');
execute upsert_tx('pdf.deliverynote.footer.text', 'it', 'È disponibile l''autorizzazione necessaria al trasporto di merci. È disponibile l''autorizzazione necessaria al trasporto di merci. La fornitura è assegnata ad una specifica destinazione.');
execute upsert_tx('pdf.deliverynote.ust-id', 'it', 'DE142385541');
execute upsert_tx('pdf.delivnote.eichbeleg.intro', 'it', 'Misure prive di componenti calibrati addizionali. Misurazioni calibrate di base possone essere riviste.');
execute upsert_tx('pdf.delivnote.eichbeleg.tare', 'it', 'Numero di calibratura di base: misurazione tara {0}: {1} ({2}{3})');
execute upsert_tx('pdf.delivnote.eichbeleg.gross', 'it', 'Misurazione peso lordo: {0}: {1} ({2}{3})');
execute upsert_tx('pdf.deliverynote.lds', 'it', 'LDS');
execute upsert_tx('pdf.deliverynote.lt', 'it', 'LT');
execute upsert_tx('pdf.deliverynote.custrefno', 'it', 'Numero di riferimento del cliente');
execute upsert_tx('pdf.deliverynote.contractno', 'it', 'Contratto');
execute upsert_tx('pdf.deliverynote.deliverytype', 'it', ' ');
execute upsert_tx('marketinfo.email.subject', 'it', 'Informazioni di mercato dall''''utilizzatore {0} {1} ({2})');
execute upsert_tx('marketinfo.email.message', 'it', 'Gentile Destinatario,' || chr(13) || chr(10) || chr(13) || chr(10) || 'È stato creato il seguente messaggio:' || chr(13) || chr(10) || chr(13) || chr(10) || '---' || chr(13) || chr(10) || '  {0}' || chr(13) || chr(10) || '---' || chr(13) || chr(10) || chr(13) || chr(10) || 'Informazioni sulla spedizione:' || chr(13) || chr(10) || '  Speditore:         {1} {2} ({3})' || chr(13) || chr(10) || '  Veicolo:           {4}, {5}' || chr(13) || chr(10) || '  Data:              {6,date,dd.MM.yy} {6,time,HH:mm}' || chr(13) || chr(10) || '  Luogo:             {7}maps?q={8},{9}&num=1&t=h&ie=UTF8&iwloc=near' || chr(13) || chr(10) || '  Telefono:          {10}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Attuale Ordine Logon:' || chr(13) || chr(10) || '  Numero:            {11}' || chr(13) || chr(10) || '  Cliente:           {12} {13}' || chr(13) || chr(10) || '  Luogo di scarico:  {14} {15}, {16} {17}' || chr(13) || chr(10) || '  Regione:           {18}/{19}' || chr(13) || chr(10) || '  Distretto Vendite: {20}' || chr(13) || chr(10) || '  Ufficio Vendite:   {21}' || chr(13) || chr(10) || '  Gruppo Vendite:    {22}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Questo E-mail é stato generato automaticamente, causa un messaggio proveniente dall''''utilizzatore del PDA. Una riposta a questo indirizzo E-mail é impossibile.' || chr(13) || chr(10) || 'Per ulteriori informazioni prendere contatto direttamente con l''''autista.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Porgo i migliori saluti,' || chr(13) || chr(10) || '{1} {2}');
execute upsert_tx('order.confirm.email.subject', 'it', 'Conferma d''''ordine Crh {0}');
execute upsert_tx('order.confirm.email.order_item', 'it', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.email.message', 'it', 'Gentile Destinatario,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Grazie mille per l''''ordine presso Crh.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Conferma d''''ordine {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || 'Il vostro riferimento {15}' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Tempo di consegna' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Destinazione Cliente' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Ordinato da:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Ulteriori dettagli sul vostro ordine così come lo stato dello stesso può trovarli su https://lpdv-logon.crh.com' || chr(13) || chr(10) || chr(13) || chr(10) || 'Coi migliori saluti,' || chr(13) || chr(10) || 'La vostra Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'Questo E-mail é stato generato automaticamente. Una riposta a questo indirizzo E-mail é impossibile. Per ulteriori informazioni prendere contatto direttamente con l''''Hotline o con il servizio di vendita.');
execute upsert_tx('load.confirm.email.subject', 'it', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'it', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'it', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.fax.order_item', 'it', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.fax.message', 'it', 'Conferma d''''ordine Crh {0}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Gentile Destinatario,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Grazie mille per l''''ordine presso Crh.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Conferma d''''ordine {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Tempo di consegna' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Destinazione Cliente' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Ordinato da:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Ulteriori dettagli sul vostro ordine così come lo stato dello stesso può trovarli su https://logon.crh.com' || chr(13) || chr(10) || chr(13) || chr(10) || 'Coi migliori saluti,' || chr(13) || chr(10) || 'La vostra Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'Per ulteriori informazioni prendere contatto direttamente con l''''Hotline o con il servizio di vendita.');
execute upsert_tx('load.confirm.fax.message', 'it', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.sms.order_item', 'it', '{0} {1} {2} ');
execute upsert_tx('order.confirm.sms.message', 'it', 'Conferma d''''ordine Crh {0} / {1}/ {6} / {2,date,dd.MM.yy} {2,time,HH:mm} - {3,time,HH:mm} - Grazie.');
execute upsert_tx('order.confirm.sms.message.1', 'it', 'Conferma d''''ordine Crh (1/2) {0} / {1}/ {6}');
execute upsert_tx('order.confirm.sms.message.2', 'it', 'Conferma d''''ordine Crh (2/2) {2,date,dd.MM.yy} {2,time,HH:mm} - {3,time,HH:mm} - Grazie.');
execute upsert_tx('load.confirm.sms.message', 'it', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'it', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'it', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('order.delivery.email.subject', 'it', 'Bolla di Consegna Crh {0}');
execute upsert_tx('order.delivery.email.text', 'it', 'Gentile Destinatario,' || chr(13) || chr(10)  || chr(13) || chr(10) || 'In allegato la bolla di consegna del suo ordine {0}.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Coi migliori saluti,' || chr(13) || chr(10) || 'La sua Crh');
-- No translation in IT: special.rail.order.email.subj
-- No translation in IT: special.rail.order.email.offer
-- No translation in IT: special.rail.order.email.rep
-- No translation in IT: special.rail.order.email
execute upsert_tx('new_password1.email.subject', 'it', 'Crh Logon reimpostazione della parola d''ordine ({0})');
execute upsert_tx('new_password1.email.text', 'it', 'La nuova parola d''ordine per l''utente ''''{0}'''' ({1}) serà inviata in una separata email.');
execute upsert_tx('new_password2.email.subject', 'it', 'Reimpostazione della parola d''ordine');
execute upsert_tx('new_password2.email.text', 'it', 'La tua nuova parola d''''ordine è:' || chr(13) || chr(10) || '{0}');

-- LOGON-5538 Translations for "password reset" mails (fr,es,it)
execute upsert_tx('password_reset_token.email.subject', 'it', 'Istruzioni utente per il reset  della password ''''{0}''''');
execute upsert_tx('password_reset_token.email.text', 'it', 'Caro utente,'  || chr(13) || chr(10) || chr(13) || chr(10) || 'È arrivata una richiesta di reset della password per l''''utente {0}.'|| chr(13) || chr(10) || 'Per resettare la password, clicca sul seguente collegamento o copialo come indirizzo nel tuo browser:' || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Se non hai richiesto il reset della password, si prega di ignorare questa email.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Cordiali saluti,' || chr(13) || chr(10) || 'Crh');

execute upsert_tx('daily_reports.email.subject', 'it', 'Logon rapporto quotidiano {0,date,dd.MM.yy}');
execute upsert_tx('daily_reports.email.text', 'it', 'Si prega di trovare il rapporto ''''{1}'''' allegato.' || chr(13) || chr(10) || chr(13) || chr(10) || '{2}');
execute upsert_tx('staging_area_report.email.subject', 'it', 'Report del Staging Area ({0}) {1,date,dd.MM.yy}');
execute upsert_tx('staging_area_report.email.text', 'it', 'In allegato il report ''''{0}''''.');
execute upsert_tx('unlock_password.email.subject', 'it', 'Crh LOGON login esterno bloccato ({0})');
execute upsert_tx('unlock_password.email.text', 'it', 'Gentile utente,' || chr(13) || chr(10) || chr(13) || chr(10) || 'il tuo conto {0} in {1} é stato bloccato sulla {2}, a {3} a causa di troppi tentativi. È possibile sbloccare il tuo conto e reimpostare la tua parola d''''ordine via il seguente link:'|| chr(13) || chr(10) || chr(13) || chr(10) || '{4}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Riceverai la tua nuova parola d''''ordine che si è registrato nel {1} via email.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Coi migliori saluti,' || chr(13) || chr(10) || 'Crh');
execute upsert_tx('unlock_pwdsucc.email.subject', 'it', 'Crh LOGON login esterno sbloccato ({0})');
execute upsert_tx('unlock_pwdsucc.email.text', 'it', 'Gentile utente,' || chr(13) || chr(10) || chr(13) || chr(10) || 'il tuo account {0} in {1} è stato correttamente sbloccato su {2}, a {3}.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Coi migliori saluti,' || chr(13) || chr(10) || 'Crh');
execute upsert_tx('order.deallocate.sms.message', 'it', 'ORDER {0} has been deleted');
execute upsert_tx('order.released.sms.message', 'it', 'ORDER {0} / Start: {1} / Time: {2}({3}) - {4}({5}) / Ship-to: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');
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
execute upsert_tx('unloads.for.target.conn.back.email.text',  'it', 'Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject',  'it', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text',  'it', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.short.unload.email.subject', 'it', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'it', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');


prompt INSERTING into TLGNR_TEXT_TX - ES
execute upsert_tx('help.useradmin.authorization_dep', 'es', 'Company Code -> Sales Areas, Sales Areas -> Plants, Plants -> Shippingpoint' || chr(13) || chr(10) || 'Customers -> Ship To');
execute upsert_tx('pdf.collective_deliverynote.footer.text', 'es', 'Collective del. note footer text (es - translate).');
execute upsert_tx('pdf.collective_deliverynote.footer.title', 'es', 'Collective del. note footer title (es - translate)');
execute upsert_tx('pdf.deliverynote.footer.title', 'es', 'El Operador Logístico garantiza que');
execute upsert_tx('pdf.deliverynote.bimodal.truck', 'es', 'Entrega Bimodal desde pedido por tren');
execute upsert_tx('pdf.deliverynote.bimodal.qty', 'es', 'Cantidad total de vagones');
execute upsert_tx('pdf.deliverynote.bimodal_ord_wagon_inf','es','Entrega bimodal del pedido por tren (0), vagón (1), cantidad total en el vagón: (2)');
execute upsert_tx('pdf.deliverynote.customs', 'es', 'Aplicación colectiva periódica de acuerdo con ZG 42 Abs 1  Bes. c, con la solicitud/aplicación para la autorización preferente. El exportador de las mercancías, el cual ha sido mencionado en este documento comercial, expone que las mercancías son productos preferentes CE originales.');
execute upsert_tx('pdf.deliverynote.footer.text', 'es', 'Se ha concedido el permiso necesario para transportar esta mercancía. La carga no excede el peso bruto del vehículo permitido por la Ley. La entrega de las mercancías declaradas se realizará al destinatario.');
execute upsert_tx('pdf.deliverynote.ust-id', 'es', 'DE142385541');
execute upsert_tx('pdf.delivnote.eichbeleg.intro', 'es', 'Medidas de componentes adicionales libremente calibrados. Pueden revisarse las medidas básicas de calibración.');
execute upsert_tx('pdf.delivnote.eichbeleg.tare', 'es', 'Número básico de calibración: medida de tara {0}: {1} ({2}{3})');
execute upsert_tx('pdf.delivnote.eichbeleg.gross', 'es', 'Medida del peso bruto: {0}: {1} ({2}{3})');
execute upsert_tx('pdf.deliverynote.lds', 'es', 'SLE');
execute upsert_tx('pdf.deliverynote.lt', 'es', 'LT');
execute upsert_tx('pdf.deliverynote.custrefno', 'es', 'Núm. Ref. Cliente');
execute upsert_tx('pdf.deliverynote.contractno', 'es', 'Contrato');
execute upsert_tx('pdf.deliverynote.deliverytype', 'es', ' ');
execute upsert_tx('marketinfo.email.subject', 'es', 'Información Mercado de {0} {1} ({2})');
execute upsert_tx('marketinfo.email.message', 'es', 'Querido Destinatario,'  || chr(13) || chr(10)  || chr(13) || chr(10) || 'El siguiente mensaje ha sido creado:' || chr(13) || chr(10) || chr(13) || chr(10) || '---' || chr(13) || chr(10) || '  {0}' || chr(13) || chr(10) || '---' || chr(13) || chr(10) || chr(13) || chr(10) || 'Información remitente:' || chr(13) || chr(10) || '  Remitente:         {1} {2} ({3})' || chr(13) || chr(10) || '  Vehículo:        {4}, {5}' || chr(13) || chr(10) || '  Fecha:           {6,date,dd.MM.yy} {6,time,HH:mm}' || chr(13) || chr(10) || '  Lugar:          {7}maps?q={8},{9}&num=1&t=h&ie=UTF8&iwloc=near' || chr(13) || chr(10) || '  Móvil:         {10}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Pedido LOGON actual:' || chr(13) || chr(10) || '  Número:         {11}' || chr(13) || chr(10) || '  Cliente:       {12} {13}' || chr(13) || chr(10) || '  Localización:       {14} {15}, {16} {17}' || chr(13) || chr(10) || '  Región:         {18}/{19}' || chr(13) || chr(10) || '  Distrito de Ventas: {20}' || chr(13) || chr(10) || '  Oficina de Ventas:   {21}' || chr(13) || chr(10) || '  Grupo de Ventas:    {22}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Este email ha sido creado automáticamente debido a un mensaje PDA. No responder directamente a esta dirección de email.' || chr(13) || chr(10) || 'Para más información, por favor, contacte con el destinatario de este mensaje.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Nuestros mejores deseos,' || chr(13) || chr(10) || '{1} {2}');
execute upsert_tx('order.confirm.email.subject', 'es', 'Confirmación de pedido Crh {0}');
execute upsert_tx('order.confirm.email.order_item', 'es', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.email.message', 'es', 'Querido Cliente,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Muchas gracias por el pedido realizado a Crh.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Confirmación de pedido {0}:'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Tu referencia {15}' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Hora de Entrega' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.aaaa})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.aaaa})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Lugar de Entrega' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Pedido por:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Más detalles y el estado actual del pedido pueden encontrarse en la página web de LOGON.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Saludos cordiales,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'Este email ha sido creado automáticamente. No responder directamente a esta dirección de email. Para cualquier pregunta, por favor, contacte con su línea de Atención al Cliente Crh o con su Representante de Ventas.');
execute upsert_tx('load.confirm.email.subject', 'es', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'es', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'es', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.fax.order_item', 'es', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.fax.message', 'es', 'Confirmación de pedido Crh {0}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Querido Cliente,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Muchas gracias por el pedido realizado a Crh.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Confirmación de pedido {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Hora de Entrega' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.aaaa})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.aaaa})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Lugar de Entrega' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Pedido por:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Más detalles y el estado actual del pedido pueden encontrarse en la página web de LOGON.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Saludos cordiales,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'Para cualquier pregunta, por favor, contacte con su línea de Atención al Cliente Crh o con su Representante de Ventas.');
execute upsert_tx('load.confirm.fax.message', 'es', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.sms.order_item', 'es', '{0} {1} {2} ');
execute upsert_tx('order.confirm.sms.message', 'es', 'Confirmación de pedido Crh {0} / {1}/ {6} / {2,date,dd.MM.aa} {2,time,HH:mm} - {3,time,HH:mm} - Gracias.');
execute upsert_tx('order.confirm.sms.message.1', 'es', 'Confirmación de pedido Crh (1/2) {0} / {1}/ {6}');
execute upsert_tx('order.confirm.sms.message.2', 'es', 'Confirmación de pedido Crh (2/2) {2,date,dd.MM.aa} {2,time,HH:mm} - {3,time,HH:mm} - Gracias.');
execute upsert_tx('load.confirm.sms.message', 'es', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'es', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'es', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('order.delivery.email.subject', 'es', 'Albarán Crh {0}');
execute upsert_tx('order.delivery.email.text', 'es', 'Querido Destinatario,' || chr(13) || chr(10)  || chr(13) || chr(10) || 'Por favor, revise el archivo adjunto donde se encuentra el Albarán de su pedido {0}.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Nuestros mejores deseos,' || chr(13) || chr(10) || 'Crh');
execute upsert_tx('special.rail.order.email.subj','es','Pedido para vagón {0} ({1})');
execute upsert_tx('special.rail.order.email.offer','es','Por favor, envíenos su oferta');
execute upsert_tx('special.rail.order.email.rep','es','Pedido Reparación/Mantenimiento: {0}' || chr(13) || chr(10) || 'Códigos de daño: {1}'|| chr(13) || chr(10));
execute upsert_tx('special.rail.order.email','es','Querido Destinatario,'|| chr(13) || chr(10) || chr(13) || chr(10) || 'El siguiente pedido {0} ha sido creado:'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Vagón:'|| chr(13) || chr(10) || '  {1}/{2}'|| chr(13) || chr(10) || '  {3} {4}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Destinatario:'|| chr(13) || chr(10) || '  {5}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Transporte:'|| chr(13) || chr(10) || '  {6} ({7}) -> {8} ({9})'|| chr(13) || chr(10) || chr(13) || chr(10) || '{10}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Comentarios:'|| chr(13) || chr(10) || '{11}'|| chr(13) || chr(10) || chr(13) || chr(10) || chr(13) || chr(10) || '{12}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Persona de contacto: {13}'|| chr(13) || chr(10) || 'Nuestros mejores deseos,'|| chr(13) || chr(10) || '  {14}'|| chr(13) || chr(10) || chr(13) || chr(10) || '------------------------------'|| chr(13) || chr(10) || '  {15}'|| chr(13) || chr(10) || '  {16}'|| chr(13) || chr(10) || '  {17}'|| chr(13) || chr(10) || 'Este email ha sido generado automáticamente. Para cualquier pregunta, por favor, utilice los detalles de contacto de la parte inferior o escriba a rail-ch@crh.com');
execute upsert_tx('new_password1.email.subject', 'es', 'Borrar la contraseña de acceso a Crh Logon ({0})');
execute upsert_tx('new_password1.email.text', 'es', 'Su nueva contraseña de usuario ''''{0}'''' ({1}) será enviada en un email por separado.');
execute upsert_tx('new_password2.email.subject', 'es', 'Borrar contraseña');
execute upsert_tx('new_password2.email.text', 'es', 'Su nueva contraseña es:' || chr(13) || chr(10) || '{0}');
-- LOGON-5538 Translations for "password reset" mails (fr,es,it)
execute upsert_tx('password_reset_token.email.subject', 'es', 'Instrucciones para el reseteo de la contraseña de usuario ''''{0}''''');
execute upsert_tx('password_reset_token.email.text', 'es', 'Querido usuario,'  || chr(13) || chr(10) || chr(13) || chr(10) || 'Se ha enviado una petición de reseteo de contraseña para el usuario {0}.'|| chr(13) || chr(10) || 'Para resetear su contraseña, pinche en el siguiente enlace o cópielo en su barra de direcciones:' || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Si usted no ha pedido que se le reseteara la contraseña, por favor, ignore este email.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Saludos cordiales,' || chr(13) || chr(10) || 'Crh');

execute upsert_tx('daily_reports.email.subject', 'es', 'Informe diario Logon {0,date,dd.MM.aa}');
execute upsert_tx('daily_reports.email.text', 'es', 'Por favor, revise el informe ''''{1}'''' adjunto.' || chr(13) || chr(10) || chr(13) || chr(10) || '{2}');
execute upsert_tx('staging_area_report.email.subject', 'es', 'Informe del Staging Area ({0}) {1,date,dd.MM.yy}');
execute upsert_tx('staging_area_report.email.text', 'es', 'Por favor, encuentre el informe ''''{0}'''' adjunto.');
execute upsert_tx('unlock_password.email.subject', 'es', 'Acceso externo a Crh LOGON bloqueado ({0})');
execute upsert_tx('unlock_password.email.text', 'es', 'Querido Usuario,' || chr(13) || chr(10) || chr(13) || chr(10) || 'su cuenta {0} en {1} ha sido bloqueada en {2}, a las {3} debido a la introducción incorrecta de la contraseña demasiadas veces. Puede desbloquear su cuenta y borrar su contraseña vía el siguiente enlace:'|| chr(13) || chr(10) || chr(13) || chr(10) || '{4}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Recibirá la nueva contraseña que ha registrado en {1} vía email.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Nuestros mejores deseos,' || chr(13) || chr(10) || 'Crh');
execute upsert_tx('unlock_pwdsucc.email.subject', 'es', 'Acceso externo Crh LOGON desbloqueado ({0})');
execute upsert_tx('unlock_pwdsucc.email.text', 'es', 'Querido Usuario,' || chr(13) || chr(10) || chr(13) || chr(10) || 'su cuenta {0} en {1} ha sido desbloqueada con éxito en {2}, a las {3}.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Nuestros mejores deseos,' || chr(13) || chr(10) || 'Crh');
execute upsert_tx('order.deallocate.sms.message', 'es', 'ORDER {0} has been deleted');
execute upsert_tx('order.released.sms.message', 'es', 'ORDER {0} / Start: {1} / Time: {2}({3}) - {4}({5}) / Ship-to: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');
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
execute upsert_tx('unloads.for.target.conn.back.email.text',  'es', 'Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject',  'es', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text',  'es', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.short.unload.email.subject', 'es', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'es', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');


prompt INSERTING into TLGNR_TEXT_TX - CS
execute upsert_tx('help.useradmin.authorization_dep', 'cs', 'Kód společnosti -> Prodejní oblasti, Prodejní oblasti -> Závody, Závody -> Místo nakládky' || chr(13) || chr(10) || 'Zákazníci -> Příjemce materiálu');
execute upsert_tx('pdf.collective_deliverynote.footer.text', 'cs', 'Text záhlaví - hromadný dodací list.');
execute upsert_tx('pdf.collective_deliverynote.footer.title', 'cs', 'Text hlavičky záhlaví - hromadný dodací list');
execute upsert_tx('pdf.deliverynote.footer.title', 'cs', 'Speditér zajišťuje, že');
execute upsert_tx('pdf.deliverynote.bimodal.truck', 'cs', 'Bimodální dodávka ze železniční objednávky');
execute upsert_tx('pdf.deliverynote.bimodal.qty', 'cs', 'Celkové množství železničních vagónů');
execute upsert_tx('pdf.deliverynote.bimodal_ord_wagon_inf','cs','Bimodální dodávka železniční objednávky (0), vagón (1), celkové množství ve vagónu: (2)');
execute upsert_tx('pdf.deliverynote.customs', 'cs', 'Pravidelné kolektivní aplikace podle ZG 42 Abs 1 Bes. c, s žádostí / žádost o přednostní odbavení. Vývozce zboží, které se odkazuje v tomto obchodním dokladu, uvádí, že tyto výrobky jsou preferenční CE původní produkty.');
execute upsert_tx('pdf.deliverynote.footer.text', 'cs', 'Potřebná povolení k přepravě tohoto nákladu jsou dána.Zatížení nepřekračuje zákonem povolenou celkovou hmotnost vozidla. Dodávka je určena jako deklarované zboží příjemci.');
execute upsert_tx('pdf.deliverynote.ust-id', 'cs', 'DE142385541');
execute upsert_tx('pdf.delivnote.eichbeleg.intro', 'cs', 'Měření volně kalibrovaných přídavných částí. Základní kalibrační měření může být upraveno.');
execute upsert_tx('pdf.delivnote.eichbeleg.tare', 'cs', 'Základní kalibrační číslo: měření tara {0}: {1} ({2}{3})');
execute upsert_tx('pdf.delivnote.eichbeleg.gross', 'cs', 'Měření bruto {0}: {1} ({2}{3})');
execute upsert_tx('pdf.deliverynote.lds', 'cs', 'LDS');
execute upsert_tx('pdf.deliverynote.lt', 'cs', 'LT');
execute upsert_tx('pdf.deliverynote.custrefno', 'cs', 'Referenční číslo zákazníka');
execute upsert_tx('pdf.deliverynote.contractno', 'cs', 'Smlouva');
execute upsert_tx('pdf.deliverynote.deliverytype', 'cs', ' ');
execute upsert_tx('marketinfo.email.subject', 'cs', 'Obchodní informace z {0} {1} ({2})');
execute upsert_tx('marketinfo.email.message', 'cs', 'Drazí příjemci,'  || chr(13) || chr(10)  || chr(13) || chr(10) || 'Následují zpráva o trhu byla založena:' || chr(13) || chr(10) || chr(13) || chr(10) || '---' || chr(13) || chr(10) || '  {0}' || chr(13) || chr(10) || '---' || chr(13) || chr(10) || chr(13) || chr(10) || 'Informace odesilatele:' || chr(13) || chr(10) || '  Odesilatel:         {1} {2} ({3})' || chr(13) || chr(10) || '  Vozidlo:        {4}, {5}' || chr(13) || chr(10) || '  Datum:           {6,date,dd.MM.yy} {6,time,HH:mm}' || chr(13) || chr(10) || '  Místo:          {7}maps?q={8},{9}&num=1&t=h&ie=UTF8&iwloc=near' || chr(13) || chr(10) || '  Mobil:         {10}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Stávající LOGON objednávka:' || chr(13) || chr(10) || '  Číslo:         {11}' || chr(13) || chr(10) || '  Zákazník:       {12} {13}' || chr(13) || chr(10) || '  Umístění:       {14} {15}, {16} {17}' || chr(13) || chr(10) || '  Region:         {18}/{19}' || chr(13) || chr(10) || '  Prodejní oblast: {20}' || chr(13) || chr(10) || '  Prodejní kancelář:   {21}' || chr(13) || chr(10) || '  Prodejní skupina:    {22}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Tento e-mail byl vygenerován automaticky na základě PDA zprávy. Odpověď na tuto e-mailovou adresu není možná.' || chr(13) || chr(10) || 'Pro další informace kontaktujte prosím autora této zprávy.' || chr(13) || chr(10) || chr(13) || chr(10) || 'S pozdravem,' || chr(13) || chr(10) || '{1} {2}');
execute upsert_tx('order.confirm.email.subject', 'cs', 'Crh - Potvrzení objednávky {0}');
execute upsert_tx('order.confirm.email.order_item', 'cs', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.email.message', 'cs', 'Vážený zákazníku,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Děkujeme za Vaši objednávku u Crh (Česko) a.s.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Potvrzení objednávky {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || 'Vaše reference {15}' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Čas dodání' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Místo dodání' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Objednáno:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Více detailů a status objednávky je možné najít na webové stránce LOGON.' || chr(13) || chr(10) || chr(13) || chr(10) || 'S pozdravem,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'Tento e-mail byl vytvořen automaticky. Odpověď na tento e-mail není možná. V případě dotazů se prosím obraťte na svého obchodního zástupce nebo jiný Crh kontakt.');
execute upsert_tx('load.confirm.email.subject', 'cs', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'cs', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'cs', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.fax.order_item', 'cs', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.fax.message', 'cs', 'Crh - Potvrzení objednávky {0}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Vážený zákazníku,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Děkujeme za Vaši objednávku u Crh (Česko) a.s.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Potvrzení objednávky {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Čas dodání' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Místo dodání' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Objednáno:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Více detailů a status objednávky je možné najít na webové stránce LOGON.' || chr(13) || chr(10) || chr(13) || chr(10) || 'S pozdravem,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'V případě dotazů se prosím obraťte na svého obchodního zástupce nebo jiný Crh kontakt.');
execute upsert_tx('load.confirm.fax.message', 'cs', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.sms.order_item', 'cs', '{0} {1} {2} ');
execute upsert_tx('order.confirm.sms.message', 'cs', 'Crh - Potvrzení objednávky {0} / {1}/ {6} / {2,date,dd.MM.yy} {2,time,HH:mm} - {3,time,HH:mm} - Děkujeme.');
execute upsert_tx('order.confirm.sms.message.1', 'cs', 'Crh - Potvrzení objednávky (1/2) {0} / {1}/ {6}');
execute upsert_tx('order.confirm.sms.message.2', 'cs', 'Crh - Potvrzení objednávky (2/2) {2,date,dd.MM.yy} {2,time,HH:mm} - {3,time,HH:mm} - Děkujeme.');
execute upsert_tx('load.confirm.sms.message', 'cs', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'cs', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'cs', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('order.delivery.email.subject', 'cs', 'Crh dodací list {0}');
execute upsert_tx('order.delivery.email.text', 'cs', 'Drazí příjemci,' || chr(13) || chr(10)  || chr(13) || chr(10) || 'V příloze naleznete dodací list pro Vaši objednávku {0}.' || chr(13) || chr(10) || chr(13) || chr(10) || 's přátelským pozdravem,' || chr(13) || chr(10) || 'Crh');
execute upsert_tx('special.rail.order.email.subj','cs','Objednávka pro vagón {0} ({1})');
execute upsert_tx('special.rail.order.email.offer','cs','Porsím zašlete nám svoji nabídku');
execute upsert_tx('special.rail.order.email.rep','cs','Oprava/Údržba - Objednávka: {0}' || chr(13) || chr(10) || 'Kódy poškození: {1}'|| chr(13) || chr(10));
execute upsert_tx('special.rail.order.email','cs','Drazí příjemci,'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Následující objednávka {0} byla založena:'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Vagón:'|| chr(13) || chr(10) || '  {1}/{2}'|| chr(13) || chr(10) || '  {3} {4}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Příjemce:'|| chr(13) || chr(10) || '  {5}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Doprava:'|| chr(13) || chr(10) || '  {6} ({7}) -> {8} ({9})'|| chr(13) || chr(10) || chr(13) || chr(10) || '{10}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Poznámka:'|| chr(13) || chr(10) || '{11}'|| chr(13) || chr(10) || chr(13) || chr(10) || chr(13) || chr(10) || '{12}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Kontaktní osoba: {13}'|| chr(13) || chr(10) || 'S pozdravem,'|| chr(13) || chr(10) || '  {14}'|| chr(13) || chr(10) || chr(13) || chr(10) || '------------------------------'|| chr(13) || chr(10) || '  {15}'|| chr(13) || chr(10) || '  {16}'|| chr(13) || chr(10) || '  {17}'|| chr(13) || chr(10) || 'Tento e-mail byl vytvořen automaticky. V případě dotazů prosím kontaktujete info@transplus.eu');
execute upsert_tx('new_password1.email.subject', 'cs', 'Crh Logon - Reset hesla ({0})');
execute upsert_tx('new_password1.email.text', 'cs', 'Vaše nové heslo pro uživatele ''''{0}'''' ({1}) Vám bude zasláno zvláštním e-mailem.');
execute upsert_tx('new_password2.email.subject', 'cs', 'Reset hesla');
execute upsert_tx('new_password2.email.text', 'cs', 'Vaše nové heslo je:' || chr(13) || chr(10) || '{0}');

execute upsert_tx('password_reset_token.email.subject', 'cs', 'Instrukce resetování hesla pro uživatele  ''''{0}''''');
execute upsert_tx('password_reset_token.email.text', 'cs', 'Vážený uživateli,'  || chr(13) || chr(10) || chr(13) || chr(10) || 'Byl proveden požadavek na reset hesla pro uživatele {0}.'|| chr(13) || chr(10) || 'Pro resetování hesla klikněte na níže uvedený link, nebo jej spusťte v internetovém prohlížeči:' || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Pokud jste nepožadoval reset hesla, prosím ignorujte tento e-mail.' || chr(13) || chr(10) || chr(13) || chr(10) || 'S přátelským pozdravem,' || chr(13) || chr(10) || 'Crh');

execute upsert_tx('daily_reports.email.subject', 'cs', 'Logon denní přehled {0,date,dd.MM.yy}');
execute upsert_tx('daily_reports.email.text', 'cs', 'Prosím prohlédnětě si přehled ''''{1}'''' v příloze.' || chr(13) || chr(10) || chr(13) || chr(10) || '{2}');
execute upsert_tx('staging_area_report.email.subject', 'cs', 'Výchozí prostor přehled ({0}) {1,date,dd.MM.yy}');
execute upsert_tx('staging_area_report.email.text', 'cs', 'Prosím prohlédnětě si přehled ''''{1}'''' v příloze.');
execute upsert_tx('unlock_password.email.subject', 'cs', 'Crh LOGON external přístup zablokován ({0})');
execute upsert_tx('unlock_password.email.text', 'cs', 'Vážený uživateli,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Váš účet {0} in {1} byl uzamčen {2}, at {3} z důvodu mnohonásobného pokusu o přihlášení. Můžete odemknout svůj účet a resetovat heslo přes následující lik:'|| chr(13) || chr(10) || chr(13) || chr(10) || '{4}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Obdržíte nové heslo {1} emailem.' || chr(13) || chr(10) || chr(13) || chr(10) || 's přátelským pozdravem,' || chr(13) || chr(10) || 'Crh');
execute upsert_tx('unlock_pwdsucc.email.subject', 'cs', 'Crh LOGON externní přihlášení obnoveno ({0})');
execute upsert_tx('unlock_pwdsucc.email.text', 'cs', 'Vážený uživateli,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Váš účet {0} v {1} byl úspěšně odblokován  {2}, v {3}.' || chr(13) || chr(10) || chr(13) || chr(10) || 's přátelským pozdravem,' || chr(13) || chr(10) || 'Crh');
execute upsert_tx('order.deallocate.sms.message', 'cs', 'Zakázka {0} byla vymazána');
execute upsert_tx('order.released.sms.message', 'cs', 'Zakázka {0} / Start: {1} / Čas: {2}({3}) - {4}({5}) / Příjemce: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');

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
execute upsert_tx('unloads.for.target.conn.back.email.text',  'cs', 'Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject',  'cs', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text',  'cs', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.short.unload.email.subject', 'cs', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'cs', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');


prompt INSERTING into TLGNR_TEXT_TX - SK
execute upsert_tx('help.useradmin.authorization_dep', 'sk', 'Kód spoločnosti -> Oblasť predaja, Oblasť predaja -> Závody, Závody -> Miesto nakládky' || chr(13) || chr(10) || 'Zákazník -> Príjemca tovaru');
execute upsert_tx('pdf.collective_deliverynote.footer.text', 'sk', 'Skupinový dodací list text v zápätí.');
execute upsert_tx('pdf.collective_deliverynote.footer.title', 'sk', 'Skupinový dodací list názov zápätia');
execute upsert_tx('pdf.deliverynote.footer.title', 'sk', 'Prepravný agent zaručuje, že');
execute upsert_tx('pdf.deliverynote.bimodal.truck', 'sk', 'Bimodálna dodávka z železničnej objednávky');
execute upsert_tx('pdf.deliverynote.bimodal.qty', 'sk', 'Celkové množstvo železničných vagónov');
execute upsert_tx('pdf.deliverynote.bimodal_ord_wagon_inf','sk','Bimodálna dodávka z železničnej objednávky (0), vagón (1), celkové množstvo železničných vagónov: (2)');
execute upsert_tx('pdf.deliverynote.customs', 'sk', 'Periodická hromadná žiadosť podľa ZG 42 Abs 1  Bes. c, so žiadosťou pre prednostné vybavenie. Exportér tovaru, ktorý je uvedený v tomto obchodnom dokumente, prehlasuje, že tovar je preferenčne vyrobený v CE.');
execute upsert_tx('pdf.deliverynote.footer.text', 'sk', 'Potrebné povolenie na dopravu tohto materiálu je vydané. Náklad nepresahuje legálne povolenú brutto váhu vozidla. Dodávka je určená pre uvedeného príjemcu tovaru.');
execute upsert_tx('pdf.deliverynote.ust-id', 'sk', 'DE142385541');
execute upsert_tx('pdf.delivnote.eichbeleg.intro', 'sk', 'Miera voľne kalibrovaných dodatočných komponentov. Základné kalibračné miery môžu byť skontrolované.');
execute upsert_tx('pdf.delivnote.eichbeleg.tare', 'sk', 'Základné kalibračné číslo: tara hmotnosť váženie {0}: {1} ({2}{3})');
execute upsert_tx('pdf.delivnote.eichbeleg.gross', 'sk', 'Brutto váha meranie:  {0}: {1} ({2}{3})');
execute upsert_tx('pdf.deliverynote.lds', 'sk', 'LDS');
execute upsert_tx('pdf.deliverynote.lt', 'sk', 'LT');
execute upsert_tx('pdf.deliverynote.custrefno', 'sk', 'Ref.č. zákazníka');
execute upsert_tx('pdf.deliverynote.contractno', 'sk', 'Zmluva');
execute upsert_tx('pdf.deliverynote.deliverytype', 'sk', ' ');
execute upsert_tx('marketinfo.email.subject', 'sk', 'Trhová informácia od  {0} {1} ({2})');
execute upsert_tx('marketinfo.email.message', 'sk', 'Vážený príjemca'  || chr(13) || chr(10)  || chr(13) || chr(10) || 'Nasledujúca trhová správa bola práve vytvorená:' || chr(13) || chr(10) || chr(13) || chr(10) || '---' || chr(13) || chr(10) || '  {0}' || chr(13) || chr(10) || '---' || chr(13) || chr(10) || chr(13) || chr(10) || 'Informácie odosielateľa:' || chr(13) || chr(10) || '  Odosielateľ:         {1} {2} ({3})' || chr(13) || chr(10) || '  Vozidlo:        {4}, {5}' || chr(13) || chr(10) || '  Dátum:           {6,date,dd.MM.yy} {6,time,HH:mm}' || chr(13) || chr(10) || '  Miesto:          {7}maps?q={8},{9}&num=1&t=h&ie=UTF8&iwloc=near' || chr(13) || chr(10) || '  Mobil:         {10}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Aktuálna LOGON objednávka:' || chr(13) || chr(10) || '  Číslo:         {11}' || chr(13) || chr(10) || '  Zákazník:       {12} {13}' || chr(13) || chr(10) || '  Miesto:       {14} {15}, {16} {17}' || chr(13) || chr(10) || '  Región:         {18}/{19}' || chr(13) || chr(10) || '  Oblasť predaja: {20}' || chr(13) || chr(10) || '  Kancelária odbytu:   {21}' || chr(13) || chr(10) || '  Predajná skupina:    {22}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Tento email bol automaticky vytvorený kvôli PDA odkazu. Priama odpoveď na tento email nie je možná.' || chr(13) || chr(10) || 'Pre viac informácií kontaktujte autora tejto správy.' || chr(13) || chr(10) || chr(13) || chr(10) || 'S pozdravom,' || chr(13) || chr(10) || '{1} {2}');
execute upsert_tx('order.confirm.email.subject', 'sk', 'Crh potvrdenie objednávky {0}');
execute upsert_tx('order.confirm.email.order_item', 'sk', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.email.message', 'sk', 'Vážený zákazník,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Ďakujeme za Vašu objednávku v Crhe.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Potvrdenie objednávky {0}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Tvoja referencia {15}' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Dodacia lehota' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Miesto dodania' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Objednané:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Viac detailov a aktuálny status objednávky je na LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'S pozdravom,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'Tento email bol vytvorený automaticky. Priama odpoveď na tento email nie je možná. V prípade otázok volajte Vašu kontaktnú osobu v Crhe alebo obchodného zástupcu.');
execute upsert_tx('load.confirm.email.subject', 'sk', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'sk', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'sk', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.fax.order_item', 'sk', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.fax.message', 'sk', 'Crh potvrdenie objednávky {0}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Vážený zákazník,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Ďakujeme za Vašu objednávku v Crhe.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Potvrdenie objednávky {0}' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Dodacia lehota' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Miesto dodania' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Objednané:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Viac detailov a aktuálny status objednávky je na LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'S pozdravom,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'V prípade otázok volajte Vašu kontaktnú osobu v Crhe alebo obchodného zástupcu.');
execute upsert_tx('load.confirm.fax.message', 'sk', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.sms.order_item', 'sk', '{0} {1} {2} ');
execute upsert_tx('order.confirm.sms.message', 'sk', 'Crh potvrdenie objednávky {0} / {1}/ {6} / {2,date,dd.MM.yy} {2,time,HH:mm} - {3,time,HH:mm} - Ďakujem.');
execute upsert_tx('order.confirm.sms.message.1', 'sk', 'Crh potvrdenie objednávky (1/2) {0} / {1}/ {6}');
execute upsert_tx('order.confirm.sms.message.2', 'sk', 'Crh potvrdenie objednávky (2/2) {2,date,dd.MM.yy} {2,time,HH:mm} - {3,time,HH:mm} - Ďakujem.');
execute upsert_tx('load.confirm.sms.message', 'sk', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'sk', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'sk', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('order.delivery.email.subject', 'sk', 'Crh dodací list {0}');
execute upsert_tx('order.delivery.email.text', 'sk', 'Vážený príjemca,' || chr(13) || chr(10)  || chr(13) || chr(10) || 'V prílohe nájdete dodací list k Vašej objednávke {0}.' || chr(13) || chr(10) || chr(13) || chr(10) || 's pozdravom,' || chr(13) || chr(10) || 'Crh');
execute upsert_tx('special.rail.order.email.subj','sk','Objednávka pre vagón {0} ({1})');
execute upsert_tx('special.rail.order.email.offer','sk','Prosím pošlite nám Vašu ponuku');
execute upsert_tx('special.rail.order.email.rep','sk','Objednávka oprava/údržba: {0}' || chr(13) || chr(10) || 'Kódy poškodenia: {1}'|| chr(13) || chr(10));
execute upsert_tx('special.rail.order.email','sk','Vážený príjemca,'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Nasledujúca objednávka {0} bola vytvorená:'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Vagón:'|| chr(13) || chr(10) || '  {1}/{2}'|| chr(13) || chr(10) || '  {3} {4}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Príjemca:'|| chr(13) || chr(10) || '  {5}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Doprava:'|| chr(13) || chr(10) || '  {6} ({7}) -> {8} ({9})'|| chr(13) || chr(10) || chr(13) || chr(10) || '{10}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Poznámky:'|| chr(13) || chr(10) || '{11}'|| chr(13) || chr(10) || chr(13) || chr(10) || chr(13) || chr(10) || '{12}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Kontaktná osoba: {13}'|| chr(13) || chr(10) || 's pozdravom,'|| chr(13) || chr(10) || '  {14}'|| chr(13) || chr(10) || chr(13) || chr(10) || '------------------------------'|| chr(13) || chr(10) || '  {15}'|| chr(13) || chr(10) || '  {16}'|| chr(13) || chr(10) || '  {17}'|| chr(13) || chr(10) || 'Tento email bol vytvorený automaticky. V prípade otázok použite horeuvedený kontakt alebo  rail-ch@crh.com');
execute upsert_tx('new_password1.email.subject', 'sk', 'Crh Logon heslo resetované ({0})');
execute upsert_tx('new_password1.email.text', 'sk', 'Vaše nové heslo pre užívateľa  ''''{0}'''' ({1}) bude poslaný v druhom emaily.');
execute upsert_tx('new_password2.email.subject', 'sk', 'Heslo resetované');
execute upsert_tx('new_password2.email.text', 'sk', 'Vaše nové heslo je:' || chr(13) || chr(10) || '{0}');

execute upsert_tx('password_reset_token.email.subject', 'sk', 'Inštrukcie resetovania hesla pre užívateľa  ''''{0}''''');
execute upsert_tx('password_reset_token.email.text', 'sk', 'Vážený užívateľ,'  || chr(13) || chr(10) || chr(13) || chr(10) || 'Bola zadaná žiadosť na zmenu hesla pre užívateľa {0}.'|| chr(13) || chr(10) || 'Pre zmenu hesla kliknite na nasledujúci link, alebo ho nakopírujte do Vášho prehliadača:' || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Ak ste nežiadali o zmenu hesla, prosím ignorujte tento email.' || chr(13) || chr(10) || chr(13) || chr(10) || 'S pozdravom,' || chr(13) || chr(10) || 'Crh');

execute upsert_tx('daily_reports.email.subject', 'sk', 'Logon denný report {0,date,dd.MM.yy}');
execute upsert_tx('daily_reports.email.text', 'sk', 'V prílohe nájdete report ''''{1}''''.' || chr(13) || chr(10) || chr(13) || chr(10) || '{2}');
execute upsert_tx('staging_area_report.email.subject', 'sk', 'Staging area report ({0}) {1,date,dd.MM.yy}');
execute upsert_tx('staging_area_report.email.text', 'sk', 'V prílohe nájdete report ''''{1}''''.');
execute upsert_tx('unlock_password.email.subject', 'sk', 'Crh LOGON externé prihlásenie blokované ({0})');
execute upsert_tx('unlock_password.email.text', 'sk', 'Vážený užívateľ,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Váš účet {0} v {1} bol blokovaný {2}, v {3} kvôli viacerým neúspešným prihláseniam. Váš účet môžete odblokovať a resetovať heslo cez nasledujúci link:'|| chr(13) || chr(10) || chr(13) || chr(10) || '{4}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Vaše nové heslo ktoré ste registrovali {1} obdržíte emailom.' || chr(13) || chr(10) || chr(13) || chr(10) || 's pozdravom,' || chr(13) || chr(10) || 'Crh');
execute upsert_tx('unlock_pwdsucc.email.subject', 'sk', 'Crh LOGON externé prihlásenie odblokované ({0})');
execute upsert_tx('unlock_pwdsucc.email.text', 'sk', 'Vážený užívateľ,' || chr(13) || chr(10) || chr(13) || chr(10) || 'váš účet {0} v {1} bol úspešne odblokovaný {2}, o {3}' || chr(13) || chr(10) || chr(13) || chr(10) || 's pozdravom,' || chr(13) || chr(10) || 'Crh');
execute upsert_tx('order.deallocate.sms.message', 'sk', 'Objednávka {0} bola zmazaná');
execute upsert_tx('order.released.sms.message', 'sk', 'Objednávka {0} / Štart: {1} / Čas: {2}({3}) - {4}({5}) / Príjemca tovaru: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');
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
execute upsert_tx('unloads.for.target.conn.back.email.text',  'sk', 'Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject',  'sk', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text',  'sk', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.short.unload.email.subject', 'sk', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'sk', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');


prompt INSERTING into TLGNR_TEXT_TX - HU
execute upsert_tx('help.useradmin.authorization_dep', 'hu', 'Vállalati kód -> Értékesítési körzet -> Gyár -> Felrakó hely' || chr(13) || chr(10) || 'Vevők -> Ship to');
execute upsert_tx('pdf.collective_deliverynote.footer.text', 'hu', 'Közös szállítólevél előláb szöveg');
execute upsert_tx('pdf.collective_deliverynote.footer.title', 'hu', 'Közös szállítólevél előláb cím');
execute upsert_tx('pdf.deliverynote.footer.title', 'hu', 'A szállítmányozó biztosítja, hogy');
execute upsert_tx('pdf.deliverynote.bimodal.truck', 'hu', 'Bimodális szállítás vasúti megrendelés esetén');
execute upsert_tx('pdf.deliverynote.bimodal.qty', 'hu', 'Vasúti vagonok teljes összege');
execute upsert_tx('pdf.deliverynote.bimodal_ord_wagon_inf','hu','Bimodális vasúti szállításra vonatkozó megrendelés (0), vagon (1), vasúti kocsik száma: (2)');
execute upsert_tx('pdf.deliverynote.customs', 'hu', 'Időszakos közös kérelem a ZG 42 ABS 1 Bes szerint a preferált szállítási engedély kérésével / alkalmazásával. Az áruk exportőre, akik a jelenleg a jelen kereskedelmi dokumentum kijelentik, hogy ezek az áruk  CE preferálásából származó termékek.');
execute upsert_tx('pdf.deliverynote.footer.text', 'hu', 'A közlekedési a rakományra  szükséges engedélyt ki lett állítva. A rakomány nem haladja meg a jogilag megengedett össztömegű jármű súlyát. A bejelentett áru szállítása a címzetthez.');
execute upsert_tx('pdf.deliverynote.ust-id', 'hu', 'DE142385541');
execute upsert_tx('pdf.delivnote.eichbeleg.intro', 'hu', 'Szabad mérés kalibráció a további összetevőkre. Az alap kalibrációs méréseket el lehet végezni.');
execute upsert_tx('pdf.delivnote.eichbeleg.tare', 'hu', 'Alap kalibrációs szám: tára súly mérése {0}: {1} ({2}{3})');
execute upsert_tx('pdf.delivnote.eichbeleg.gross', 'hu', 'Bruttó súly mérése: {0}: {1} ({2}{3})');
execute upsert_tx('pdf.deliverynote.lds', 'hu', 'LDS');
execute upsert_tx('pdf.deliverynote.lt', 'hu', 'LT');
execute upsert_tx('pdf.deliverynote.custrefno', 'hu', 'Ügyfél referencia száma');
execute upsert_tx('pdf.deliverynote.contractno', 'hu', 'Szerződés');
execute upsert_tx('pdf.deliverynote.deliverytype', 'hu', ' ');
execute upsert_tx('marketinfo.email.subject', 'hu', 'Piaci információ {0} {1} ({2})');
execute upsert_tx('marketinfo.email.message', 'hu', 'Kedves ügyfél,'  || chr(13) || chr(10)  || chr(13) || chr(10) || 'A következő piaci üzenet hozták létre:' || chr(13) || chr(10) || chr(13) || chr(10) || '---' || chr(13) || chr(10) || '  {0}' || chr(13) || chr(10) || '---' || chr(13) || chr(10) || chr(13) || chr(10) || 'Feladó információja:' || chr(13) || chr(10) || '  Feladó:         {1} {2} ({3})' || chr(13) || chr(10) || '  Jármű:        {4}, {5}' || chr(13) || chr(10) || '  Dátum:           {6,date,dd.MM.yy} {6,time,HH:mm}' || chr(13) || chr(10) || '  Hely:          {7}maps?q={8},{9}&num=1&t=h&ie=UTF8&iwloc=near' || chr(13) || chr(10) || '  Mobil:         {10}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Aktuális LOGON megrendelés:' || chr(13) || chr(10) || '  Szám:         {11}' || chr(13) || chr(10) || '  Ügyfél:       {12} {13}' || chr(13) || chr(10) || '  Tartózkodási hely:       {14} {15}, {16} {17}' || chr(13) || chr(10) || '  Régió:         {18}/{19}' || chr(13) || chr(10) || '  Értékesítési terület: {20}' || chr(13) || chr(10) || '  Értékesítési iroda:   {21}' || chr(13) || chr(10) || '  Értékesítési csoport:    {22}' || chr(13) || chr(10) || chr(13) || chr(10) || 'A PDA üzenet miatt ez az e-mailt automatikusan jött létre. Közvetlen üzenet erre az e-mail címre nem lehetséges.' || chr(13) || chr(10) || 'További információért kérem lépjen kapcsolatba az üzenet küldőjével.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Üdvözlettel,' || chr(13) || chr(10) || '{1} {2}');

execute upsert_tx('order.confirm.email.subject', 'hu', 'Crh megrendelés megerősítése {0}');
execute upsert_tx('order.confirm.email.order_item', 'hu', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.email.message', 'hu', 'Tisztelt Ügyfelünk,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Köszönjük szépen Crh-es megrendelését.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Megrendelés visszaigazolása {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || 'Az Ön referenciája {15}' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Szállítási idő' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kézbesítés /szállítási cím' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Megrendelte:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'További részletek és aktuális megrendelése állapotáról tájékozódni tud LOGON weboldalán.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Üdvözlettel,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'Ez az e-mailt automatikusan generálódott. Közvetlen üzenet erre az e-mail címre nem lehetséges. Kérdése esetén lépjen kapcsolatba az Ön Crhes kapcsolattartójával vagy értéseksítő kollegáinkkal.');
execute upsert_tx('load.confirm.email.subject', 'hu', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'hu', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'hu', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.fax.order_item', 'hu', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.fax.message', 'hu', 'Crh megrendelés emgerősítése {0}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Tisztelt Ügyfelünk,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Many thanks for your order at Crh.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Megredndelés visszaigazolása {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Szállítási idő' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kézbesítés /szállítási cím' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Megrendelte:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'További részletek és aktuális megrendelése állapotáról tájékozódni tud LOGON weboldalán.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Üdvözlettel,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'Kérdése esetén lépjen kapcsolatba az Ön Crhes kapcsolattartójával vagy értéseksítő kollegáinkkal.');
execute upsert_tx('load.confirm.fax.message', 'hu', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.sms.order_item', 'hu', '{0} {1} {2} ');
execute upsert_tx('order.confirm.sms.message', 'hu', 'Crh megrendelés emgerősítése {0} / {1}/ {6} / {2,date,dd.MM.yy} {2,time,HH:mm} - {3,time,HH:mm} - Köszönjük.');
execute upsert_tx('order.confirm.sms.message.1', 'hu', 'Crh megrendelés emgerősítése (1/2) {0} / {1}/ {6}');
execute upsert_tx('order.confirm.sms.message.2', 'hu', 'Crh megrendelés emgerősítése (2/2) {2,date,dd.MM.yy} {2,time,HH:mm} - {3,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message', 'hu', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'hu', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'hu', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('order.delivery.email.subject', 'hu', 'Crh szállítólevél {0}');
execute upsert_tx('order.delivery.email.text', 'hu', 'Kedves ügyfél,' || chr(13) || chr(10)  || chr(13) || chr(10) || 'Csatolmányban megtalálja a megrendelése szállító levelét {0}.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Tisztelettel,' || chr(13) || chr(10) || 'Crh');
execute upsert_tx('special.rail.order.email.subj','hu','Vagon megrendelés {0} ({1})');
execute upsert_tx('special.rail.order.email.offer','hu','Kérem küldje az Ön ajánlatát');
execute upsert_tx('special.rail.order.email.rep','hu','Javítás / Karbantartás megrendelése: {0}' || chr(13) || chr(10) || 'Damage codes: {1}'|| chr(13) || chr(10));
execute upsert_tx('special.rail.order.email','hu','Kedves ügyfél,'|| chr(13) || chr(10) || chr(13) || chr(10) || 'A következő megrendelés {0} lwtt létrehozva:'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Vagon:'|| chr(13) || chr(10) || '  {1}/{2}'|| chr(13) || chr(10) || '  {3} {4}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Címzett:'|| chr(13) || chr(10) || '  {5}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Közlekedés:'|| chr(13) || chr(10) || '  {6} ({7}) -> {8} ({9})'|| chr(13) || chr(10) || chr(13) || chr(10) || '{10}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Megjegyzés:'|| chr(13) || chr(10) || '{11}'|| chr(13) || chr(10) || chr(13) || chr(10) || chr(13) || chr(10) || '{12}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Kapcsolattartó személy: {13}'|| chr(13) || chr(10) || 'Tisztelettel,'|| chr(13) || chr(10) || '  {14}'|| chr(13) || chr(10) || chr(13) || chr(10) || '------------------------------'|| chr(13) || chr(10) || '  {15}'|| chr(13) || chr(10) || '  {16}'|| chr(13) || chr(10) || '  {17}'|| chr(13) || chr(10) || 'Ez az e-mailt automatikusan generálódott. Abban az esetben ha kérdése van kérem használja a fenti elérhetőséget vagy írjon a rail-ch@crh.com email címre');
execute upsert_tx('new_password1.email.subject', 'hu', 'Crh LOGON jelszó törlés ({0})');
execute upsert_tx('new_password1.email.text', 'hu', 'Az Ön új felhasználnálói ''''{0}'''' ({1}) jelszavat elküldjük egy külön e-mail címre.');
execute upsert_tx('new_password2.email.subject', 'hu', 'Jelszó törlése');
execute upsert_tx('new_password2.email.text', 'hu', 'Az Ön új jelszava:' || chr(13) || chr(10) || '{0}');

execute upsert_tx('password_reset_token.email.subject', 'hu', 'Jelszó törlési instrukció a felhasználónak ''''{0}''''');
execute upsert_tx('password_reset_token.email.text', 'hu', 'Kedves felhasználó,'  || chr(13) || chr(10) || chr(13) || chr(10) || 'A felhasználó jelszó kérelmet nyújtottak be {0}.'|| chr(13) || chr(10) || 'A jelszó törléséhez kattintson a következő linkre vagy illesze be a keresőjébe:' || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Ha igényelt ez a jelszót, kérem hagyja figyelmen kívül ezt az e-mailt.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Üdvözlettel,' || chr(13) || chr(10) || 'Crh');

execute upsert_tx('daily_reports.email.subject', 'hu', 'LOGON napi jelentés {0,date,dd.MM.yy}');
execute upsert_tx('daily_reports.email.text', 'hu', 'Csatolmányként megtalálja ''''{1}'''' a riportot.' || chr(13) || chr(10) || chr(13) || chr(10) || '{2}');
execute upsert_tx('staging_area_report.email.subject', 'hu', 'Szakasz terület jelentés ({0}) {1,date,dd.MM.yy}');
execute upsert_tx('staging_area_report.email.text', 'hu', 'Csatolmányként megtalálja ''''{1}'''' a riportot.');
execute upsert_tx('unlock_password.email.subject', 'hu', 'Crh Logon külső felhasználó zárolva ({0})');
execute upsert_tx('unlock_password.email.text', 'hu', 'Kedves felhasználó,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Túl sok sikertelen bejelentkezési kísérlet miatt az Ön fiókja zárolva lett. Fel tudja oldani a zárat és törölni a jelszavát az alábbi link segítségével:'|| chr(13) || chr(10) || chr(13) || chr(10) || '{4}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Megkapja az új jelszavát miután {1} e-mailen keresztül regisztrált.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Tisztelettel,' || chr(13) || chr(10) || 'Crh');
execute upsert_tx('unlock_pwdsucc.email.subject', 'hu', 'Crh Logon külső felhasználó zárolva ({0})');
execute upsert_tx('unlock_pwdsucc.email.text', 'hu', 'Kedves felhasználó,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Az ön fiókja sikeresen feloldódott.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Tisztelettel,' || chr(13) || chr(10) || 'Crh');
execute upsert_tx('order.deallocate.sms.message', 'hu', 'A megrendelést {0} törölték');
execute upsert_tx('order.released.sms.message', 'hu', 'Megrendelés: {0}/ Kezdés: {1}/ Idő: {2}({3}) - {4}({5}) /Ship-to: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');
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
execute upsert_tx('unloads.for.target.conn.back.email.text',  'hu', 'Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject',  'hu', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text',  'hu', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.short.unload.email.subject', 'hu', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'hu', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');


prompt INSERTING into TLGNR_TEXT_TX - RO
execute upsert_tx('help.useradmin.authorization_dep', 'ro', 'Cod Companie -> Arii vanzari, Arii vanzari -> Fabrici, Fabrici -> Punct de Expeditie' || chr(13) || chr(10) || 'Clienti-> Destinatar');
execute upsert_tx('pdf.collective_deliverynote.footer.text', 'ro', 'Text subsol nota de livrare colectiva.');
execute upsert_tx('pdf.collective_deliverynote.footer.title', 'ro', 'Titlu subsol nota de livrare colectiva');
execute upsert_tx('pdf.deliverynote.footer.title', 'ro', 'Agent de forwarding asigura ca');
execute upsert_tx('pdf.deliverynote.bimodal.truck', 'ro', 'Livrare bimodal comanda feroviara');
execute upsert_tx('pdf.deliverynote.bimodal.qty', 'ro', 'Cantitatea totala a vagoanelor feroviare');
execute upsert_tx('pdf.deliverynote.bimodal_ord_wagon_inf','ro','Livrare bimodal a comenzii feroviare (0), vagon (1), cantitatea totala pe vagon feroviar: (2)');
execute upsert_tx('pdf.deliverynote.customs', 'ro', 'Aplicatie colectiva periodica conform ZG 42 Abs 1  Bes. c, cu cerere/aplicatie pentru clarificare preferentiala. Exportatorul produselor la care se face referire in acest document comercial declara ca acestea sunt produse  de origine CE preferentiala.');
execute upsert_tx('pdf.deliverynote.footer.text', 'ro', 'Permisiunea ceruta pentru a transporta aceasta incarcatura este acordata. Incarcatura nu depaseste greutatea bruta legala a vehiculului. Livrarea este destinata beneficiarului declarat al bunurilor.');
execute upsert_tx('pdf.deliverynote.ust-id', 'ro', 'DE142385541');
execute upsert_tx('pdf.delivnote.eichbeleg.intro', 'ro', 'Masuratori ale componentelor aditionale liber calibrate. Masuratorile de calibrare de baza pot fi revizuite.');
execute upsert_tx('pdf.delivnote.eichbeleg.tare', 'ro', 'Numar de calibrare de baza: cantitate greutate tara {0}: {1} ({2}{3})');
execute upsert_tx('pdf.delivnote.eichbeleg.gross', 'ro', 'Cantitate greutate bruta: {0}: {1} ({2}{3})');
execute upsert_tx('pdf.deliverynote.lds', 'ro', 'Sistem Local de Expeditie');
execute upsert_tx('pdf.deliverynote.lt', 'ro', 'LT');
execute upsert_tx('pdf.deliverynote.custrefno', 'ro', 'Nr. Ref. Client');
execute upsert_tx('pdf.deliverynote.contractno', 'ro', 'Contract');
execute upsert_tx('pdf.deliverynote.deliverytype', 'ro', ' ');
execute upsert_tx('marketinfo.email.subject', 'ro', 'Informatii Piata de la {0} {1} ({2})');
execute upsert_tx('marketinfo.email.message', 'ro', 'Draga Beneficiar,'  || chr(13) || chr(10)  || chr(13) || chr(10) || 'Urmatorul mesaj de piata a fost creat:' || chr(13) || chr(10) || chr(13) || chr(10) || '---' || chr(13) || chr(10) || '  {0}' || chr(13) || chr(10) || '---' || chr(13) || chr(10) || chr(13) || chr(10) || 'Informatii Expeditor:' || chr(13) || chr(10) || '  Expeditor:         {1} {2} ({3})' || chr(13) || chr(10) || '  Vehicul:        {4}, {5}' || chr(13) || chr(10) || '  Data:           {6,date,dd.MM.yy} {6,time,HH:mm}' || chr(13) || chr(10) || '  Loc:          {7}maps?q={8},{9}&num=1&t=h&ie=UTF8&iwloc=near' || chr(13) || chr(10) || '  Mobil:         {10}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Comanda LOGON actuala:' || chr(13) || chr(10) || '  Numar:         {11}' || chr(13) || chr(10) || '  Client:       {12} {13}' || chr(13) || chr(10) || '  Locatie:       {14} {15}, {16} {17}' || chr(13) || chr(10) || '  Regiune:         {18}/{19}' || chr(13) || chr(10) || '  District vanzari: {20}' || chr(13) || chr(10) || '  Birou vanzari:   {21}' || chr(13) || chr(10) || '  Grup vanzari:    {22}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Acest email a fost creat automat din cauza unui mesaj PDA. Nu este posibil un raspuns direct catre aceasta adresa de email.' || chr(13) || chr(10) || 'Pentru mai multe informatii contactati autorul acestui mesaj.' || chr(13) || chr(10) || chr(13) || chr(10) || 'cu respect,' || chr(13) || chr(10) || '{1} {2}');
execute upsert_tx('order.confirm.email.subject', 'ro', 'Confirmare comanda Crh {0}');
execute upsert_tx('order.confirm.email.order_item', 'ro', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.email.message', 'ro', 'Draga Client,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Va multumim pentru comanda dumneavoastra la Crh.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Confirmare comanda {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || 'Referința ta {15}' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Timp de livrare' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Locatie Livrare' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Comandat de catre:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Mai multe detalii si status-ul actual al comenzii pot fi gasite pe website-ul LOGON.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Cu respect,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'Acest email a fost creat automat. Nu este posibil un raspuns direct la aceasta adresa de email. Daca aveti intrebari, va rugam sa sunati  persoana dumneavostra de contact de la Crh sau reprezentantul vanzari.');
execute upsert_tx('load.confirm.email.subject', 'ro', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'ro', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'ro', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.fax.order_item', 'ro', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.fax.message', 'ro', 'Confirmare comanda Crh {0}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Draga Client,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Va multumim pentru comanda dumneavoastra la Crh.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Confirmare comanda {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Timp de livrare' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Locatie Livrare' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Comandat de catre:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Mai multe detalii si status-ul actual al comenzii pot fi gasite pe website-ul LOGON.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Cu respect,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'Daca aveti intrebari, va rugam sa sunati  persoana dumneavostra de contact de la Crh sau reprezentantul vanzari.');
execute upsert_tx('load.confirm.fax.message', 'ro', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.sms.order_item', 'ro', '{0} {1} {2} ');
execute upsert_tx('order.confirm.sms.message', 'ro', 'Confirmare comanda Crh {0} / {1}/ {6} / {2,date,dd.MM.yy} {2,time,HH:mm} - {3,time,HH:mm} - Va multumim.');
execute upsert_tx('order.confirm.sms.message.1', 'ro', 'Confirmare comanda Crh (1/2) {0} / {1}/ {6}');
execute upsert_tx('order.confirm.sms.message.2', 'ro', 'Confirmare comanda Crh (2/2) {2,date,dd.MM.yy} {2,time,HH:mm} - {3,time,HH:mm} - Va multumim.');
execute upsert_tx('load.confirm.sms.message', 'ro', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'ro', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'ro', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('order.delivery.email.subject', 'ro', 'Nota de livrare Crh {0}');
execute upsert_tx('order.delivery.email.text', 'ro', 'Dear recipient,' || chr(13) || chr(10)  || chr(13) || chr(10) || 'Attached please find the delivery note to your order {0}.' || chr(13) || chr(10) || chr(13) || chr(10) || 'with best regards,' || chr(13) || chr(10) || 'Crh');
execute upsert_tx('special.rail.order.email.subj','ro','Order for wagon {0} ({1})');
execute upsert_tx('special.rail.order.email.offer','ro','Please send us your offer');
execute upsert_tx('special.rail.order.email.rep','ro','Repair/Maintenance Order: {0}' || chr(13) || chr(10) || 'Coduri avarii: {1}'|| chr(13) || chr(10));
execute upsert_tx('special.rail.order.email','ro','Draga Beneficiar,'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Urmatoarea comanda {0} tocmai a fost creata:'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Vagon:'|| chr(13) || chr(10) || '  {1}/{2}'|| chr(13) || chr(10) || '  {3} {4}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Beneficiar:'|| chr(13) || chr(10) || '  {5}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Transport:'|| chr(13) || chr(10) || '  {6} ({7}) -> {8} ({9})'|| chr(13) || chr(10) || chr(13) || chr(10) || '{10}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Observatii:'|| chr(13) || chr(10) || '{11}'|| chr(13) || chr(10) || chr(13) || chr(10) || chr(13) || chr(10) || '{12}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Persoana de contact: {13}'|| chr(13) || chr(10) || 'Cu respect,'|| chr(13) || chr(10) || '  {14}'|| chr(13) || chr(10) || chr(13) || chr(10) || '------------------------------'|| chr(13) || chr(10) || '  {15}'|| chr(13) || chr(10) || '  {16}'|| chr(13) || chr(10) || '  {17}'|| chr(13) || chr(10) || 'Acest email a fost generat automat. Daca aveti intrebari, va rugam sa folositi detaliile de contact de mai sus sau trimiteti raspuns la rail-ch@crh.com');
execute upsert_tx('new_password1.email.subject', 'ro', 'Resetare parola Logon Crh ({0})');
execute upsert_tx('new_password1.email.text', 'ro', 'Noua dumneavoastra parola pentru utilizator  ''''{0}'''' ({1}) va fi trimisa intr-un email separat.');
execute upsert_tx('new_password2.email.subject', 'ro', 'Resetare parola');
execute upsert_tx('new_password2.email.text', 'ro', 'Noua dumneavoastra parola este:' || chr(13) || chr(10) || '{0}');

execute upsert_tx('password_reset_token.email.subject', 'ro', 'Instructiuni de resetare parola pentru utilizator ''''{0}''''');
execute upsert_tx('password_reset_token.email.text', 'ro', 'Draga utilizator,'  || chr(13) || chr(10) || chr(13) || chr(10) || 'O solicitate de resetare parola a fost facuta pentru utilizatorul {0}.'|| chr(13) || chr(10) || 'Pentru a va reseta parola accesati urmatorul link sau copiati-l in browser-ul dumneavoastra:' || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Daca nu ati solicitat o resetare a parolei, va rugam sa ignorati acest email.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Cu respect,' || chr(13) || chr(10) || 'Crh');

execute upsert_tx('daily_reports.email.subject', 'ro', 'Raport zilnic Logon {0,date,dd.MM.yy}');
execute upsert_tx('daily_reports.email.text', 'ro', 'Va rugam, gasiti raportul ''''{1}'''' atasat.' || chr(13) || chr(10) || chr(13) || chr(10) || '{2}');
execute upsert_tx('staging_area_report.email.subject', 'ro', 'Raport impartire zona({0}) {1,date,dd.MM.yy}');
execute upsert_tx('staging_area_report.email.text', 'ro', 'Va rugam, gasiti raportul ''''{1}'''' atasat.');
execute upsert_tx('unlock_password.email.subject', 'ro', 'Conectare externa LOGON Crh blocata ({0})');
execute upsert_tx('unlock_password.email.text', 'ro', 'Draga utilizator,' || chr(13) || chr(10) || chr(13) || chr(10) || 'contul dumneavoastra {0} in {1} a fost blocat in {2}, la {3} din cauza prea multor incercari esuate de conectare. Va puteti debloca contul si reseta parola accesand urmatorul link:'|| chr(13) || chr(10) || chr(13) || chr(10) || '{4}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Veti primi noua parola pe care ati inregistrat-o in {1} prin email.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Cu respect,' || chr(13) || chr(10) || 'Crh');
execute upsert_tx('unlock_pwdsucc.email.subject', 'ro', 'Conectare externa LOGON Crh deblocata ({0})');
execute upsert_tx('unlock_pwdsucc.email.text', 'ro', 'Draga utilizator,' || chr(13) || chr(10) || chr(13) || chr(10) || 'contul dumneavoastra {0} in {1} a fost deblocat cu succes in {2}, la {3}.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Cu respect,' || chr(13) || chr(10) || 'Crh');
execute upsert_tx('order.deallocate.sms.message', 'ro', 'COMANDA {0} a fost stearsa');
execute upsert_tx('order.released.sms.message', 'ro', 'COMANDA {0} / Start: {1} / Timp: {2}({3}) - {4}({5}) / Destinatar: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');
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
execute upsert_tx('unloads.for.target.conn.back.email.text',  'ro', 'Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject',  'ro', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text',  'ro', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.short.unload.email.subject', 'ro', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'ro', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');


prompt INSERTING into TLGNR_TEXT_TX - BG
execute upsert_tx('help.useradmin.authorization_dep', 'bg', 'Код на фирма -> Продажбени области-> Заводи, Заводи-> Място на доставяне' || chr(13) || chr(10) || 'Клиенти -> Обект');
execute upsert_tx('pdf.collective_deliverynote.footer.text', 'bg', 'Колективна експедиционна бележка footer текст.');
execute upsert_tx('pdf.collective_deliverynote.footer.title', 'bg', 'Колективна експедиционна бележка footer заглавие');
execute upsert_tx('pdf.deliverynote.footer.title', 'bg', 'Търговският агент уверява че');
execute upsert_tx('pdf.deliverynote.bimodal.truck', 'bg', 'Бимодална доставка от жп поръчка');
execute upsert_tx('pdf.deliverynote.bimodal.qty', 'bg', 'Средна сума на жп вагони');
execute upsert_tx('pdf.deliverynote.bimodal_ord_wagon_inf','bg','Бимодална доставка на жп поръчка (0), вагон (1), средна сума на жп вагон: (2)');
execute upsert_tx('pdf.deliverynote.customs', 'bg', 'Периодично съвместно осъществяане според ZG 42 Abs 1 Bes.c, с искане/приложение за преференциално разчистване. Износителят на стоките, за които се отнася в този търговски документ, се посочва че тези стоки са преференциални CE продукти с  произход.');
execute upsert_tx('pdf.deliverynote.footer.text', 'bg', 'Дадено е необходимото разрешение за транспортиране на този товар. Товарът не превишава нормативно разрешената обща маса на превозното средство. Доставката е предназначена за получателя на декларираните стоки.');
execute upsert_tx('pdf.deliverynote.ust-id', 'bg', 'DE142385541');
execute upsert_tx('pdf.delivnote.eichbeleg.intro', 'bg', 'Измервания на свободните калибрирани допълнителни компоненти. Основното измерване може да бъде преразгледано.');
execute upsert_tx('pdf.delivnote.eichbeleg.tare', 'bg', 'Основен номер на калибриране {0}: {1} ({2}{3})');
execute upsert_tx('pdf.delivnote.eichbeleg.gross', 'bg', 'Измерване на бруто  тегло: {0}: {1} ({2}{3})');
execute upsert_tx('pdf.deliverynote.lds', 'bg', 'LDS');
execute upsert_tx('pdf.deliverynote.lt', 'bg', 'LT');
execute upsert_tx('pdf.deliverynote.custrefno', 'bg', 'Клиентски референтен номер');
execute upsert_tx('pdf.deliverynote.contractno', 'bg', 'Договор');
execute upsert_tx('pdf.deliverynote.deliverytype', 'bg', ' ');
execute upsert_tx('marketinfo.email.subject', 'bg', 'Пазарна информация от {0} {1} ({2})');
execute upsert_tx('marketinfo.email.message', 'bg', 'Уважаеми получател,'  || chr(13) || chr(10)  || chr(13) || chr(10) || 'Съобщението за следния пазар беше току що създадено:' || chr(13) || chr(10) || chr(13) || chr(10) || '---' || chr(13) || chr(10) || '  {0}' || chr(13) || chr(10) || '---' || chr(13) || chr(10) || chr(13) || chr(10) || 'Информация за подателя:' || chr(13) || chr(10) || '  Подател:         {1} {2} ({3})' || chr(13) || chr(10) || '  Превозно средство:        {4}, {5}' || chr(13) || chr(10) || '  Дата:           {6,date,dd.MM.yy} {6,time,HH:mm}' || chr(13) || chr(10) || '  Място:          {7}maps?q={8},{9}&num=1&t=h&ie=UTF8&iwloc=near' || chr(13) || chr(10) || '  Мобилен телефон:         {10}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Текуща ЛОГОН поръчка:' || chr(13) || chr(10) || '  Номер:         {11}' || chr(13) || chr(10) || '  Клиент:       {12} {13}' || chr(13) || chr(10) || '  Месположение:       {14} {15}, {16} {17}' || chr(13) || chr(10) || '  Регион:         {18}/{19}' || chr(13) || chr(10) || '  Област на продажба: {20}' || chr(13) || chr(10) || '  Търговски офис:   {21}' || chr(13) || chr(10) || '  Търговска група:    {22}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Този имейл е създаден автоматично, заради PDA съобщение. Директен отговор на този имейл адрес не е възможен.' || chr(13) || chr(10) || 'За повече информация, моля свържете се с автора на това съобщение.' || chr(13) || chr(10) || chr(13) || chr(10) || 'С най-добри пожелания,' || chr(13) || chr(10) || '{1} {2}');
execute upsert_tx('order.confirm.email.subject', 'bg', 'Потвържение на Холсим поръчка {0}');
execute upsert_tx('order.confirm.email.order_item', 'bg', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.email.message', 'bg', 'Уважаеми клиент,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Много благодаря за вашата поръчка на Холсим.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Потвърждение на поръчка {0}:'  || chr(13) || chr(10) || chr(13) || chr(10) || 'Вашата препоръка {15}' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Време на доставяне' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Място на доставяне' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Поръчва се от:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Повече детайли и текущ статус на поръчка може да бъде открит на ЛОГОН сайта.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Поздрави,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.email.subject', 'bg', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'bg', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'bg', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.fax.order_item', 'bg', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.fax.message', 'bg', 'Потвържение на Холсим поръчка {0}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Уважаеми клиент,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Много благодаря за вашата поръчка на Холсим.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Потвърждение на поръчка {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Време на доставяне' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Място на доставяне' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Поръчва се от:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Повече детайли и текущ статус на поръчка може да бъде открит на ЛОГОН сайта.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Поздрави,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.fax.message', 'bg', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.sms.order_item', 'bg', '{0} {1} {2} ');
execute upsert_tx('order.confirm.sms.message', 'bg', 'Потвържение на Холсим поръчка {0} / {1}/ {6} / {2,date,dd.MM.yy} {2,time,HH:mm} - {3,time,HH:mm} - Благодаря.');
execute upsert_tx('order.confirm.sms.message.1', 'bg', 'Потвържение на Холсим поръчка (1/2) {0} / {1}/ {6}');
execute upsert_tx('order.confirm.sms.message.2', 'bg', 'Потвържение на Холсим поръчка (2/2) {2,date,dd.MM.yy} {2,time,HH:mm} - {3,time,HH:mm} - Благодаря.');
execute upsert_tx('load.confirm.sms.message', 'bg', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'bg', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'bg', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('order.delivery.email.subject', 'bg', 'Холсим експедиционна бележка {0}');
execute upsert_tx('order.delivery.email.text', 'bg', 'Уважаеми получател,' || chr(13) || chr(10)  || chr(13) || chr(10) || 'Приложение са на известие за доставка на вашата поръчка {0}.' || chr(13) || chr(10) || chr(13) || chr(10) || 'С най-добри пожелания,' || chr(13) || chr(10) || 'Crh');
execute upsert_tx('special.rail.order.email.subj','bg','Поръчка за вагон {0} ({1})');
execute upsert_tx('special.rail.order.email.offer','bg','Моля изпратете ни вашето предложение');
execute upsert_tx('special.rail.order.email.rep','bg','Ремонт/Поддръжка за поръчка: {0}' || chr(13) || chr(10) || 'Увредени кодове: {1}'|| chr(13) || chr(10));
execute upsert_tx('special.rail.order.email','bg','Уважаеми получател,'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Следната поръчка {0} беше току-що създадена:'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Вагон:'|| chr(13) || chr(10) || '  {1}/{2}'|| chr(13) || chr(10) || '  {3} {4}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Получател:'|| chr(13) || chr(10) || '  {5}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Транспорт:'|| chr(13) || chr(10) || '  {6} ({7}) -> {8} ({9})'|| chr(13) || chr(10) || chr(13) || chr(10) || '{10}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Забележки:'|| chr(13) || chr(10) || '{11}'|| chr(13) || chr(10) || chr(13) || chr(10) || chr(13) || chr(10) || '{12}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Лице за контакт: {13}'|| chr(13) || chr(10) || 'С най-добри пожелания,'|| chr(13) || chr(10) || '  {14}'|| chr(13) || chr(10) || chr(13) || chr(10) || '------------------------------'|| chr(13) || chr(10) || '  {15}'|| chr(13) || chr(10) || '  {16}'|| chr(13) || chr(10) || '  {17}'|| chr(13) || chr(10) || 'Този имейл е генериран автоматично. В случай че имате въпроси моля използвайте данните за контакт по-горе или пишете на rail-ch@crh.com');
execute upsert_tx('new_password1.email.subject', 'bg', 'Нулиране на Холсим парола за достъп({0})');
execute upsert_tx('new_password1.email.text', 'bg', 'Вашата нова парола за потребител ""{0}""({1}) ще бъде изпратена на отделен имейл.');
execute upsert_tx('new_password2.email.subject', 'bg', 'Нулиране на парола');
execute upsert_tx('new_password2.email.text', 'bg', 'Вашата нова парола е:' || chr(13) || chr(10) || '{0}');

execute upsert_tx('password_reset_token.email.subject', 'bg', 'Инструкции за нулиране на парола на потребител ''''{0}''''');
execute upsert_tx('password_reset_token.email.text', 'bg', 'Уважаеми потребител,'  || chr(13) || chr(10) || chr(13) || chr(10) || 'Искане за възстановяване на парола е направено за потребителя {0}.'|| chr(13) || chr(10) || 'За нулиране на вашата парола, кликнете на следния линк или го копирайте във вашия браузер:' || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Ако не е поискано възстановяване на паролата, моля игнорирайте този имейл.' || chr(13) || chr(10) || chr(13) || chr(10) || 'С най-добри пожелания,' || chr(13) || chr(10) || 'Crh');

execute upsert_tx('daily_reports.email.subject', 'bg', 'Логон дневен отчет {0,date,dd.MM.yy}');
execute upsert_tx('daily_reports.email.text', 'bg', 'Моля вижте приложения доклад ''''{1}''''.' || chr(13) || chr(10) || chr(13) || chr(10) || '{2}');
execute upsert_tx('staging_area_report.email.subject', 'bg', 'Постановка район доклад ({0}) {1,date,dd.MM.yy}');
execute upsert_tx('staging_area_report.email.text', 'bg', 'Моля вижте приложения доклад ''''{1}''''.');
execute upsert_tx('unlock_password.email.subject', 'bg', 'Crh LOGON външен вход заключен ({0})');
execute upsert_tx('unlock_password.email.text', 'bg', 'Уважаеми потребител,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Вашият акаунт {0} в {1} е заключен на {2}, в {3} поради твърде много неуспешни опити за влизане. Можете да отключите профила си и да възстановите паролата си чрез следния линк:'|| chr(13) || chr(10) || chr(13) || chr(10) || '{4}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ще получите вашата нова парола в {1} чрез имейл.' || chr(13) || chr(10) || chr(13) || chr(10) || 'С най-добри пожелания,' || chr(13) || chr(10) || 'Crh');
execute upsert_tx('unlock_pwdsucc.email.subject', 'bg', 'Холсим ЛОГОН външен вход отключен ({0})');
execute upsert_tx('unlock_pwdsucc.email.text', 'bg', 'Уважаеми потребител,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Вашият акаунт {0} в {1} е успешно отключен на {2}, в {3}.' || chr(13) || chr(10) || chr(13) || chr(10) || 'С най-добри пожелания,' || chr(13) || chr(10) || 'Crh');
execute upsert_tx('order.deallocate.sms.message', 'bg', 'Поръчка {0} е изтрита');
execute upsert_tx('order.released.sms.message', 'bg', 'Поръчка {0} / Начало: {1} / Време: {2}({3}) - {4}({5}) / Обект: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');
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
execute upsert_tx('unloads.for.target.conn.back.email.text',  'bg', 'Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject',  'bg', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text',  'bg', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.short.unload.email.subject', 'bg', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'bg', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');


prompt INSERTING into TLGNR_TEXT_TX - RU
execute upsert_tx('help.useradmin.authorization_dep', 'ru', 'Company Code -> Sales Areas, Sales Areas -> Plants, Plants -> Shippingpoint' || chr(13) || chr(10) || 'Клиент -> грузополучатель');
execute upsert_tx('pdf.collective_deliverynote.footer.text', 'ru', 'Нижний колонтитул общей накладной.');
execute upsert_tx('pdf.collective_deliverynote.footer.title', 'ru', 'Верхний колонтитул общей накладной.');
execute upsert_tx('pdf.deliverynote.footer.title', 'ru', 'перевозчик уверяет, что');
execute upsert_tx('pdf.deliverynote.bimodal.truck', 'ru', 'бимодальная доставка в жд вагонах');
execute upsert_tx('pdf.deliverynote.bimodal.qty', 'ru', 'общий объем жд вагонов');
execute upsert_tx('pdf.deliverynote.bimodal_ord_wagon_inf','ru','Бимодальная доставка в жд вагонах (0), вагон (1), общий объем жд вагонов: (2)');
execute upsert_tx('pdf.deliverynote.customs', 'ru', 'Periodical collective application according to ZG 42 Abs 1  Bes. c, with request/application for preferential clearance. The exporter of the goods, to which are being referred to in this commercial document, states, that these goods are preferential CE originating products.');
execute upsert_tx('pdf.deliverynote.footer.text', 'ru', 'Требуемое разрешение на перевозку этого груза выдано. Нагрузка не превышает допустимое значение веса брутто транспортного средства. Доставка предназначена для получателя декларируемых товаров.');
execute upsert_tx('pdf.deliverynote.ust-id', 'ru', 'DE142385541');
execute upsert_tx('pdf.delivnote.eichbeleg.intro', 'ru', 'Measurements of free calibrated additional components. Basic calibration measurements can be reviewed.');
execute upsert_tx('pdf.delivnote.eichbeleg.tare', 'ru', 'Базовая колибровка значения: измерение веса тары {0}: {1} ({2}{3})');
execute upsert_tx('pdf.delivnote.eichbeleg.gross', 'ru', 'Измерение веса брутто: {0}: {1} ({2}{3})');
execute upsert_tx('pdf.deliverynote.lds', 'ru', 'LDS');
execute upsert_tx('pdf.deliverynote.lt', 'ru', 'LT');
execute upsert_tx('pdf.deliverynote.custrefno', 'ru', 'Дополнительный номер заказчика');
execute upsert_tx('pdf.deliverynote.contractno', 'ru', 'договор');
execute upsert_tx('pdf.deliverynote.deliverytype', 'ru', ' ');
execute upsert_tx('marketinfo.email.subject', 'ru', 'информация о рынке от {0} {1} ({2})');
execute upsert_tx('marketinfo.email.message', 'ru', 'Уважаемые получатель,'  || chr(13) || chr(10)  || chr(13) || chr(10) ||         'The following market message was just created:' || chr(13) || chr(10) || chr(13) || chr(10) ||         '---' || chr(13) || chr(10) ||         '  {0}' || chr(13) || chr(10) ||         '---' || chr(13) || chr(10) || chr(13) || chr(10) ||         'Информация отправителя:' || chr(13) || chr(10) ||         '  Отправитель:         {1} {2} ({3})' || chr(13) || chr(10) ||         '  Автомобиль:        {4}, {5}' || chr(13) || chr(10) ||         '  Дата:           {6,date,dd.MM.yy} {6,time,HH:mm}' || chr(13) || chr(10) ||         '  Место:          {7}maps?q={8},{9}&num=1&t=h&ie=UTF8&iwloc=near' || chr(13) || chr(10) ||         '  Мобильный телефон:         {10}' || chr(13) || chr(10) || chr(13) || chr(10) ||         'Текущий LOGON заказ:' || chr(13) || chr(10) ||         '  Номер:         {11}' || chr(13) || chr(10) ||         '  Клиент:       {12} {13}' || chr(13) || chr(10) ||         '  Расположение:       {14} {15}, {16} {17}' || chr(13) || chr(10) ||         '  Регион:         {18}/{19}' || chr(13) || chr(10) ||         '  Sales District: {20}' || chr(13) || chr(10) ||         '  Sales Office:   {21}' || chr(13) || chr(10) ||         '  Sales Group:    {22}' || chr(13) || chr(10) || chr(13) || chr(10) ||         'Это письмо было отправлено автоматически вследствие оповещения системой PDA. Прямой ответ на это сообщение не представляется возможным.' || chr(13) || chr(10) ||         'За дополниетльной информацией, пожалуйста, обращайтесь к автору данного письма.' || chr(13) || chr(10) || chr(13) || chr(10) ||         'с уважением,' || chr(13) || chr(10) ||         '{1} {2}');
execute upsert_tx('order.confirm.email.subject', 'ru', 'Crh подтверждение заказа {0}');
execute upsert_tx('order.confirm.email.order_item', 'ru', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.email.message', 'ru', 'Уважаемый партнер,' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Благодарим за размещенный заказ в Crh.' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Заказ подтвержден {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || 'Ваша ссылка {15}' || chr(13) || chr(10) || chr(13) || chr(10) ||            '{1}' || chr(13) || chr(10) ||            'Время доставки' || chr(13) || chr(10) ||            '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) ||            '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Место доставки' || chr(13) || chr(10) ||            '  {4} {5}' || chr(13) || chr(10) ||            '  {6} {7}' || chr(13) || chr(10) ||            '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) ||            '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Заказ сделан (кем):' || chr(13) || chr(10) ||            '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Дополнительная информация и текущий статус заказа доступка на сайте LOGON.' || chr(13) || chr(10) || chr(13) || chr(10) ||            'с уважением,' || chr(13) || chr(10) ||            'Crh' || chr(13) || chr(10) || chr(13) || chr(10) ||            '-------------------' || chr(13) || chr(10) ||            'Это письмо было создано автоматически. Прямой ответ на это сообщение невозможно. В случае возникновения вопросов, пожалуйста, свяжитесь по телефону с Вашим постоянным контактным лицом Crh или торговым представителем.');
execute upsert_tx('load.confirm.email.subject', 'ru', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'ru', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'ru', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.fax.order_item', 'ru', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.fax.message', 'ru', 'Crh подтверждение заказа {0}' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Уважаемый партнер,' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Благодарим за размещенный заказ в Crh.' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Заказ подтвержден {0}:' || chr(13) || chr(10) || chr(13) || chr(10) ||          '{1}' || chr(13) || chr(10) ||          'Время доставки' || chr(13) || chr(10) ||          '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) ||          '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Место доставки' || chr(13) || chr(10) ||          '  {4} {5}' || chr(13) || chr(10) ||          '  {6} {7}' || chr(13) || chr(10) ||          '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) ||          '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Заказ сделан (кем):' || chr(13) || chr(10) ||          '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Дополнительная информация и текущий статус заказа доступка на сайте LOGON.' || chr(13) || chr(10) || chr(13) || chr(10) ||          'с уважением,' || chr(13) || chr(10) ||          'Crh' || chr(13) || chr(10) || chr(13) || chr(10) ||          '-------------------' || chr(13) || chr(10) ||          'В случае возникновения вопросов, пожалуйста, свяжитесь по телефону с Вашим постоянным контактным лицом Crh или торговым представителем.');
execute upsert_tx('load.confirm.fax.message', 'ru', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.sms.order_item', 'ru', '{0} {1} {2} ');
execute upsert_tx('order.confirm.sms.message', 'ru', 'Crh подтверждение заказа {0} / {1}/ {6} / {2,date,dd.MM.yy} {2,time,HH:mm} - {3,time,HH:mm} - Thank you.');
execute upsert_tx('order.confirm.sms.message.1', 'ru', 'Crh подтверждение заказа (1/2) {0} / {1}/ {6}');
execute upsert_tx('order.confirm.sms.message.2', 'ru', 'Crh подтверждение заказа (2/2) {2,date,dd.MM.yy} {2,time,HH:mm} - {3,time,HH:mm} - Спасибо.');
execute upsert_tx('load.confirm.sms.message', 'ru', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'ru', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'ru', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('order.delivery.email.subject', 'ru', 'Накладная Crh {0}');
execute upsert_tx('order.delivery.email.text', 'ru', 'Уважаемые получатель,' || chr(13) || chr(10)  || chr(13) || chr(10) ||          'В приложении приведена накладная к Вашему заказу {0}.' || chr(13) || chr(10) || chr(13) || chr(10) ||          'с уважением,' || chr(13) || chr(10) ||          'Crh');
execute upsert_tx('special.rail.order.email.subj','ru','заказ на вагоны {0} ({1})');
execute upsert_tx('special.rail.order.email.offer','ru','Пожалуйста, пришлите нам Ваше предложение');
execute upsert_tx('special.rail.order.email.rep','ru','Заказ на ремон\техническое обслуживание: {0}' || chr(13) || chr(10) || 'Коды повреждения: {1}'|| chr(13) || chr(10));
execute upsert_tx('special.rail.order.email','ru','Уважаемые получатель,'|| chr(13) || chr(10) || chr(13) || chr(10) ||       'Заказ {0} создан:'|| chr(13) || chr(10) || chr(13) || chr(10) ||       'Вагон:'|| chr(13) || chr(10) ||       '  {1}/{2}'|| chr(13) || chr(10) ||       '  {3} {4}'|| chr(13) || chr(10) || chr(13) || chr(10) ||       'Получатель:'|| chr(13) || chr(10) ||       '  {5}'|| chr(13) || chr(10) || chr(13) || chr(10) ||       'Транспорт:'|| chr(13) || chr(10) ||       '  {6} ({7}) -> {8} ({9})'|| chr(13) || chr(10) || chr(13) || chr(10) ||       '{10}'|| chr(13) || chr(10) || chr(13) || chr(10) ||       'Пометка:'|| chr(13) || chr(10) ||       '{11}'|| chr(13) || chr(10) || chr(13) || chr(10) || chr(13) || chr(10) ||       '{12}'|| chr(13) || chr(10) || chr(13) || chr(10) ||       'Контактное лицо: {13}'|| chr(13) || chr(10) ||       'с уважением,'|| chr(13) || chr(10) ||       '  {14}'|| chr(13) || chr(10) || chr(13) || chr(10) ||       '------------------------------'|| chr(13) || chr(10) ||       '  {15}'|| chr(13) || chr(10) ||       '  {16}'|| chr(13) || chr(10) ||       '  {17}'|| chr(13) || chr(10) ||       'Это письмо было создано автоматически. В случае возникновения вопросов, пожалуйста, используйте контактные данные, указанные выше, или напишите на электронный адрес rail-ch@crh.com.');
execute upsert_tx('new_password1.email.subject', 'ru', 'Восстановление пароля Crh Logon ({0})');
execute upsert_tx('new_password1.email.text', 'ru', 'Ваш новый пароль для пользователя ''''{0}'''' ({1}) будет отправлен в отдельном письме.');
execute upsert_tx('new_password2.email.subject', 'ru', 'Пароль восстановлен');
execute upsert_tx('new_password2.email.text', 'ru', 'Ваш новый пароль:' || chr(13) || chr(10) || '{0}');

execute upsert_tx('password_reset_token.email.subject', 'ru', 'Инструкции для восстановления пароля пользователя ''''{0}''''');
execute upsert_tx('password_reset_token.email.text', 'ru',       'Уважаемый пользователь,'  || chr(13) || chr(10) || chr(13) || chr(10) ||       'Запрос на восстановление пароля пользователя {0} создан.'|| chr(13) || chr(10) ||       'Для восстановления пароля, нажмите на ссылку или скопируйте ее в строку Вашего браузера:' || chr(13) || chr(10) ||       '{1}' || chr(13) || chr(10) || chr(13) || chr(10) ||       'Если Вы не делали запрос на восстановление пароля, пожалуйста, игнорируйте это письмо.' || chr(13) || chr(10) || chr(13) || chr(10) ||       'с уважением,' || chr(13) || chr(10) ||       'Crh');

execute upsert_tx('daily_reports.email.subject', 'ru', 'Ежедневный отчет Logon {0,date,dd.MM.yy}');
execute upsert_tx('daily_reports.email.text', 'ru', 'Отчет ''''{1}'''' в приложении.' || chr(13) || chr(10) || chr(13) || chr(10) || '{2}');
execute upsert_tx('staging_area_report.email.subject', 'ru', 'Отчет о не переданных Основных Данных ({0}) {1,date,dd.MM.yy}');
execute upsert_tx('staging_area_report.email.text', 'ru', 'Отчет  ''''{0}'''' в приложении.');
execute upsert_tx('unlock_password.email.subject', 'ru', 'Внешний вход в Crh LOGON заблокирован  ({0})');
execute upsert_tx('unlock_password.email.text', 'ru', 'Уважаемый пользователь,' || chr(13) || chr(10) || chr(13) || chr(10) ||           'Ваша учетная запись  {0} в  {1} заблокирована, в {3} из-за большого количества неверных попыток  входа. Вы можете разблокировать свою учетную запись и восстановить пароль, пройдя по следующей ссылке:'|| chr(13) || chr(10) || chr(13) || chr(10) ||           '{4}'|| chr(13) || chr(10) || chr(13) || chr(10) ||           'Вы получите Ваш новый пароль в {1} по электронной почте.' || chr(13) || chr(10) || chr(13) || chr(10) ||           'с уважением,' || chr(13) || chr(10) ||           'Crh');
execute upsert_tx('unlock_pwdsucc.email.subject', 'ru', 'Учетная запись Crh LOGON разблокирован ({0})');
execute upsert_tx('unlock_pwdsucc.email.text', 'ru', 'Уважаемый пользователь,' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Ваша учетная запись  {0} в  {1} была успешно разблокирована на {2}, в {3}.' || chr(13) || chr(10) || chr(13) || chr(10) ||          'с уважением,' || chr(13) || chr(10) ||          'Crh');
execute upsert_tx('order.deallocate.sms.message', 'ru', 'ЗАКАЗ {0} был удален');
execute upsert_tx('order.released.sms.message', 'ru', 'ЗАКАЗ {0} / Начало: {1} / Время: {2}({3}) - {4}({5}) / Грузополучатель: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');
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
execute upsert_tx('unloads.for.target.conn.back.email.text',  'ru', 'Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject',  'ru', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text',  'ru', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.short.unload.email.subject', 'ru', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'ru', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');


prompt INSERTING into TLGNR_TEXT_TX - PL
execute upsert_tx('help.useradmin.authorization_dep', 'pl', 'Kod firmy -> Działy sprzedaży -> Zakłady, Zakłady -> Punkt dostarczenia' || chr(13) || chr(10) || 'Klienci -> Odbiorca towaru');
execute upsert_tx('pdf.collective_deliverynote.footer.text', 'pl', 'Wspólny list przewozowy w stopce.');
execute upsert_tx('pdf.collective_deliverynote.footer.title', 'pl', 'Wspólny list przewozowy w tytule');
execute upsert_tx('pdf.deliverynote.footer.title', 'pl', 'Spedytor zapewnia, że');
execute upsert_tx('pdf.deliverynote.bimodal.truck', 'pl', 'Bimodalna dostawa z zamówienia kolejowego');
execute upsert_tx('pdf.deliverynote.bimodal.qty', 'pl', 'Łączna ilość wagonów kolejowych');
execute upsert_tx('pdf.deliverynote.bimodal_ord_wagon_inf','pl','Bimodalna dostawa zamówienia kolejowego (0) , wagon (1) , łączna ilość wagonów kolejowych: (2)');
execute upsert_tx('pdf.deliverynote.customs', 'pl', 'Okresowy zbiorowy wniosek zgodnie z ZG 42 ABS 1 Bes . c, z wniosku / aplikacji do preferencyjnego rozliczenia. Eksporter towarów, do których zostały określone w niniejszym dokumencie handlowym, stanowi, że towary te są preferencyjne CE produkty pochodzące.');
execute upsert_tx('pdf.deliverynote.footer.text', 'pl', 'Wymagane pozwolenie na transport frachtowy zaakceptowane. Obciążenie nie przekracza prawnie dozwolonego ciężaru brutto pojazdu. Dostawa jest przeznaczona dla deklarowanego odbiorcy towarów.');
execute upsert_tx('pdf.deliverynote.ust-id', 'pl', 'DE142385541');
execute upsert_tx('pdf.delivnote.eichbeleg.intro', 'pl', 'Pomiary bezpłatnych kalibrowanych elementów dodatkowych. Podstawowe pomiary kalibracyjne mogą być recenzja.');
execute upsert_tx('pdf.delivnote.eichbeleg.tare', 'pl', 'Podstawowy numer kalibracji: masa własna wycena {0}: {1} ({2}{3})');
execute upsert_tx('pdf.delivnote.eichbeleg.gross', 'pl', 'Pomiar wagi brutto: {0}: {1} ({2}{3})');
execute upsert_tx('pdf.deliverynote.lds', 'pl', 'LDS');
execute upsert_tx('pdf.deliverynote.lt', 'pl', 'LT');
execute upsert_tx('pdf.deliverynote.custrefno', 'pl', 'Numer referencyjny klienta');
execute upsert_tx('pdf.deliverynote.contractno', 'pl', 'Umowa');
execute upsert_tx('pdf.deliverynote.deliverytype', 'pl', ' ');
execute upsert_tx('marketinfo.email.subject', 'pl', 'Informacje rynkowe z {0} {1} ({2})');
execute upsert_tx('marketinfo.email.message', 'pl', 'Drodzy odbiorcy,'  || chr(13) || chr(10)  || chr(13) || chr(10) ||         'Następująca wiadomość rynkowa została właśnie utworzona:' || chr(13) || chr(10) || chr(13) || chr(10) ||         '---' || chr(13) || chr(10) ||         '  {0}' || chr(13) || chr(10) ||         '---' || chr(13) || chr(10) || chr(13) || chr(10) ||         'Informacje o nadawcy:' || chr(13) || chr(10) ||         '  Nadawca:         {1} {2} ({3})' || chr(13) || chr(10) ||         '  Pojazd:        {4}, {5}' || chr(13) || chr(10) ||         '  Data:           {6,date,dd.MM.yy} {6,time,HH:mm}' || chr(13) || chr(10) ||         '  Miejsce:          {7}maps?q={8},{9}&num=1&t=h&ie=UTF8&iwloc=near' || chr(13) || chr(10) ||         '  Telefon komórkowy:         {10}' || chr(13) || chr(10) || chr(13) || chr(10) ||         'Obecny LOGON zamówienia:' || chr(13) || chr(10) ||         '  Numer:         {11}' || chr(13) || chr(10) ||         '  Klient:       {12} {13}' || chr(13) || chr(10) ||         '  Lokalizacja:       {14} {15}, {16} {17}' || chr(13) || chr(10) ||         '  Region:         {18}/{19}' || chr(13) || chr(10) ||         '  Dystrykt sprzedaży: {20}' || chr(13) || chr(10) ||         '  Biuro sprzedaży:   {21}' || chr(13) || chr(10) ||         '  Grupa sprzedaży:    {22}' || chr(13) || chr(10) || chr(13) || chr(10) ||         'Ten e-mail został utworzony automatycznie przez wiadomości PDA. Bezpośrednia odpowiedź na ten adres email nie jest możliwa.' || chr(13) || chr(10) ||         'W celu uzyskania dalszych informacji prosimy o kontakt z autorem tej wiadomości.' || chr(13) || chr(10) || chr(13) || chr(10) ||         'Z poważaniem,' || chr(13) || chr(10) ||         '{1} {2}');
execute upsert_tx('order.confirm.email.subject', 'pl', 'Potwierdzenie zamówienia Crh {0}');
execute upsert_tx('order.confirm.email.order_item', 'pl', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.email.message', 'pl', 'Szanowni Państwo,' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Dziękujemy za zamówienie w Crh.' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Potwierdzenie zamówienia {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || 'Twoje referencje {15}' || chr(13) || chr(10) || chr(13) || chr(10) ||            '{1}' || chr(13) || chr(10) ||            'Czas dostawy' || chr(13) || chr(10) ||            '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) ||            '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Miejsce dostawy' || chr(13) || chr(10) ||            '  {4} {5}' || chr(13) || chr(10) ||            '  {6} {7}' || chr(13) || chr(10) ||            '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) ||            '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Zamówione przez:' || chr(13) || chr(10) ||            '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Więcej szczegółów i aktualny status zamówienia można znaleźć na stronie internetowej LOGON.' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Z poważaniem,' || chr(13) || chr(10) ||            'Crh' || chr(13) || chr(10) || chr(13) || chr(10) ||            '-------------------' || chr(13) || chr(10) ||            'Ten e-mail został utworzony automatycznie. Bezpośrednia odpowiedź na ten adres nie jest możliwa. W przypadku pytań prosimy o standardowy kontakt telefoniczny z Crh lub Państwa opiekunem handlowym.');
execute upsert_tx('load.confirm.email.subject', 'pl', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'pl', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'pl', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.fax.order_item', 'pl', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.fax.message', 'pl', 'Potwierdzenie zamówienia Crh {0}' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Szanowni Państwo,' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Dziękujemy za zamówienie w Crh.' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Potwierdzenie zamówienia {0}:' || chr(13) || chr(10) || chr(13) || chr(10) ||          '{1}' || chr(13) || chr(10) ||          'Czas dostawy' || chr(13) || chr(10) ||          '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) ||          '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Miejsce dostawy' || chr(13) || chr(10) ||          '  {4} {5}' || chr(13) || chr(10) ||          '  {6} {7}' || chr(13) || chr(10) ||          '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) ||          '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Zamówione przez:' || chr(13) || chr(10) ||          '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Więcej szczegółów i aktualny status zamówienia można znaleźć na stronie internetowej LOGON.' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Z poważaniem,' || chr(13) || chr(10) ||          'Crh' || chr(13) || chr(10) || chr(13) || chr(10) ||          '-------------------' || chr(13) || chr(10) ||          'W przypadku pytań prosimy o standardowy kontakt telefoniczny z Crh lub Państwa opiekunem handlowym.');
execute upsert_tx('load.confirm.fax.message', 'pl', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.sms.order_item', 'pl', '{0} {1} {2} ');
execute upsert_tx('order.confirm.sms.message', 'pl', 'Potwierdzenie zamówienia Crh {0} / {1}/ {6} / {2,date,dd.MM.yy} {2,time,HH:mm} - {3,time,HH:mm} - Dziękuję.');
execute upsert_tx('order.confirm.sms.message.1', 'pl', 'Potwierdzenie zamówienia Crh (1/2) {0} / {1}/ {6}');
execute upsert_tx('order.confirm.sms.message.2', 'pl', 'Potwierdzenie zamówienia Crh (2/2) {2,date,dd.MM.yy} {2,time,HH:mm} - {3,time,HH:mm} - Dziękuję.');
execute upsert_tx('load.confirm.sms.message', 'pl', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'pl', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'pl', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('order.delivery.email.subject', 'pl', 'Listr przewozowy Crh {0}');
execute upsert_tx('order.delivery.email.text', 'pl', 'Drodzy odbiorcy,' || chr(13) || chr(10)  || chr(13) || chr(10) ||          'W załączniku znajduje się dowód dostawy z zamówieniem {0}.' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Z poważaniem,' || chr(13) || chr(10) ||          'Crh');
execute upsert_tx('special.rail.order.email.subj','pl','Zamówienie wagonu {0} ({1})');
execute upsert_tx('special.rail.order.email.offer','pl','Proszę przesłać nam swoją ofertę');
execute upsert_tx('special.rail.order.email.rep','pl','Naprawa / Konserwacja zamówienia: {0}' || chr(13) || chr(10) || 'Przeciążone kody: {1}'|| chr(13) || chr(10));
execute upsert_tx('special.rail.order.email','pl','Drodzy odbiorcy,'|| chr(13) || chr(10) || chr(13) || chr(10) ||       'Następujący zamówienie {0} zostało właśnie utworzone:'|| chr(13) || chr(10) || chr(13) || chr(10) ||       'Wagon:'|| chr(13) || chr(10) ||       '  {1}/{2}'|| chr(13) || chr(10) ||       '  {3} {4}'|| chr(13) || chr(10) || chr(13) || chr(10) ||       'Odbiorca:'|| chr(13) || chr(10) ||       '  {5}'|| chr(13) || chr(10) || chr(13) || chr(10) ||       'Transport:'|| chr(13) || chr(10) ||       '  {6} ({7}) -> {8} ({9})'|| chr(13) || chr(10) || chr(13) || chr(10) ||       '{10}'|| chr(13) || chr(10) || chr(13) || chr(10) ||       'Uwagi:'|| chr(13) || chr(10) ||       '{11}'|| chr(13) || chr(10) || chr(13) || chr(10) || chr(13) || chr(10) ||       '{12}'|| chr(13) || chr(10) || chr(13) || chr(10) ||       'Osoba kontaktowa: {13}'|| chr(13) || chr(10) ||       'Z poważaniem,'|| chr(13) || chr(10) ||       '  {14}'|| chr(13) || chr(10) || chr(13) || chr(10) ||       '------------------------------'|| chr(13) || chr(10) ||       '  {15}'|| chr(13) || chr(10) ||       '  {16}'|| chr(13) || chr(10) ||       '  {17}'|| chr(13) || chr(10) ||       'Ta wiadomość została wygenerowana automatycznie. W przypadku pytań proszę skorzystać z danych kontaktowych powyżej lub odpowiedzieć rail-ch@crh.com');
execute upsert_tx('new_password1.email.subject', 'pl', 'Crh Resetowanie hasła logowania ({0})');
execute upsert_tx('new_password1.email.text', 'pl', 'Twoje nowe hasło dla użytkownika ''''{0}'''' ({1}) zostanie wysłany w osobnym mailu.');
execute upsert_tx('new_password2.email.subject', 'pl', 'Resetowanie hasła');
execute upsert_tx('new_password2.email.text', 'pl', 'Twoje nowe hasło to:' || chr(13) || chr(10) || '{0}');

execute upsert_tx('password_reset_token.email.subject', 'pl', 'Instrukcje resetowania hasła dla użytkownika ''''{0}''''');
execute upsert_tx('password_reset_token.email.text', 'pl',       'Drogi użytkowniku,'  || chr(13) || chr(10) || chr(13) || chr(10) ||       'Żądanie resetowania hasła został wykonany dla użytkownika {0}.'|| chr(13) || chr(10) ||       'Aby zresetować hasło , kliknij na poniższy link i wklej go w swojej przeglądarce:' || chr(13) || chr(10) ||       '{1}' || chr(13) || chr(10) || chr(13) || chr(10) ||       'Jeśli nie poprosić o zresetowanie hasła , zignoruj ​​tę wiadomość.' || chr(13) || chr(10) || chr(13) || chr(10) ||       'Z poważaniem,' || chr(13) || chr(10) ||       'Crh');

execute upsert_tx('daily_reports.email.subject', 'pl', 'Logon raport dzienny {0,date,dd.MM.yy}');
execute upsert_tx('daily_reports.email.text', 'pl', 'Proszę znaleźć raport ''''{1}'''' w załączeniu.' || chr(13) || chr(10) || chr(13) || chr(10) || '{2}');
execute upsert_tx('staging_area_report.email.subject', 'pl', 'Raport z obszaru postoju ({0}) {1,date,dd.MM.yy}');
execute upsert_tx('staging_area_report.email.text', 'pl', 'Proszę znaleźć raport ''''{0}'''' w załączeniu.');
execute upsert_tx('unlock_password.email.subject', 'pl', 'Zewnętrzny LOGON Crh został zablokowany ({0})');
execute upsert_tx('unlock_password.email.text', 'pl', 'Drogi użytkowniku,' || chr(13) || chr(10) || chr(13) || chr(10) ||           'twoje konto {0} w {1} zostało zablokowane na {2}, w {3} ze względu na zbyt wiele nieudanych prób logowania. Można odblokować konto i zresetować hasło za pomocą poniższego linku:'|| chr(13) || chr(10) || chr(13) || chr(10) ||           '{4}'|| chr(13) || chr(10) || chr(13) || chr(10) ||           'Otrzymasz nowe hasło, które zostało zarejestrowane w {1} za pośrednictwem poczty elektronicznej.' || chr(13) || chr(10) || chr(13) || chr(10) ||           'Z poważaniem,' || chr(13) || chr(10) ||           'Crh');
execute upsert_tx('unlock_pwdsucc.email.subject', 'pl', 'Zewnętrzny LOGON Crh został odblokowany ({0})');
execute upsert_tx('unlock_pwdsucc.email.text', 'pl', 'Drogi użytkowniku,' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Twoje konto {0} w {1} zostało pomyślnie odblokowane na {2} , w {3}.' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Z poważaniem,' || chr(13) || chr(10) ||          'Crh');
execute upsert_tx('order.deallocate.sms.message', 'pl', 'ZAMÓWIENIE {0} zostało usunięte');
execute upsert_tx('order.released.sms.message', 'pl', 'ZAMÓWIENIE {0} / Start: {1} / Czas: {2}({3}) - {4}({5}) / Odbiorca towaru: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');
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
execute upsert_tx('unloads.for.target.conn.back.email.text',  'pl', 'Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject',  'pl', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text',  'pl', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.short.unload.email.subject', 'pl', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'pl', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');


prompt INSERTING into TLGNR_TEXT_TX - AZ
execute upsert_tx('help.useradmin.authorization_dep', 'az', 'Şirkət kodu -> Satış sahələri, Satış sahələri -> Zavodlar, Zavodlar -> Nəqletmə nöqtəsi' || chr(13) || chr(10) || 'Müştərilər -> Hara göndərilir');
execute upsert_tx('pdf.collective_deliverynote.footer.text', 'az', 'Toplu fakturanın aşağı kolontitul mətni');
execute upsert_tx('pdf.collective_deliverynote.footer.title', 'az', 'Toplu fakturanın aşağı kolontitul sərlövhəsi');
execute upsert_tx('pdf.deliverynote.footer.title', 'az', 'Ekspeditor təminat verir ki');
execute upsert_tx('pdf.deliverynote.bimodal.truck', 'az', 'Dəmiryol sifarişi əsasında bimodal çatdırılma');
execute upsert_tx('pdf.deliverynote.bimodal.qty', 'az', 'Dəmiryol vaqonlarının ümumi sayı');
execute upsert_tx('pdf.deliverynote.bimodal_ord_wagon_inf','az','Dəmiryol sifarişi (0), vaqon (1), dəmiryol vaqonlarının ümumi sayının bimodal çatdırılması: (2)');
execute upsert_tx('pdf.deliverynote.customs', 'az', 'ZG 42 Abs 1 Bes. C standartına uyğun olaraq dövri toplu tətbiq, imtiyazlı rəsmiləşdirmə üçün sorğu /tətbiq ilə birlikdə. Hazırkı kommersiya sənədində adı çəkilən mal ixracçısı bəyan edir ki, bu mallar imtiyazlı CE mənşəli məhsullardır.');
execute upsert_tx('pdf.deliverynote.footer.text', 'az', 'Yük nəqliyyat vasitəsinin qanunla icazə verilən brutto çəkisindən artıq olmadıqda, bu yükü daşımaq üçün tələb olunan icazə verilir. Çatdırılma bəyan edilmiş malların təhvil alınması üçün nəzərdə tutulur.');
execute upsert_tx('pdf.deliverynote.ust-id', 'az', 'DE142385541');
execute upsert_tx('pdf.delivnote.eichbeleg.intro', 'az', 'Sərbəst kalibrlənən əlavə komponentlərin ölçüləri. Əsas kalibrləmə ölçülərinə yenidən baxıla bilər.');
execute upsert_tx('pdf.delivnote.eichbeleg.tare', 'az', 'Əsas kalibrləmə sayı: tara çəkisinin ölçüsü {0}: {1} ({2}{3})');
execute upsert_tx('pdf.delivnote.eichbeleg.gross', 'az', 'Brutto çəki ölçüsü: {0}: {1} ({2}{3})');
execute upsert_tx('pdf.deliverynote.lds', 'az', 'LDS');
execute upsert_tx('pdf.deliverynote.lt', 'az', 'LT');
execute upsert_tx('pdf.deliverynote.custrefno', 'az', 'Müştəri İst. №-si');
execute upsert_tx('pdf.deliverynote.contractno', 'az', 'Müqavilə');
execute upsert_tx('pdf.deliverynote.deliverytype', 'az', ' ');
execute upsert_tx('marketinfo.email.subject', 'az', 'Bazar məlumatları {0} {1} ({2})');
execute upsert_tx('marketinfo.email.message', 'az', 'Hörmətli alıcı,'  || chr(13) || chr(10)  || chr(13) || chr(10) ||         'Aşağıdakı bazar məlumatı indicə yaradılıb:' || chr(13) || chr(10) || chr(13) || chr(10) ||         '---' || chr(13) || chr(10) ||         '  {0}' || chr(13) || chr(10) ||         '---' || chr(13) || chr(10) || chr(13) || chr(10) ||         'Göndərənin məlumatı:' || chr(13) || chr(10) ||         '  Göndərən:         {1} {2} ({3})' || chr(13) || chr(10) ||         '  Nəqliyyat vasitəsi:        {4}, {5}' || chr(13) || chr(10) ||         '  Tarix:           {6,date,dd.MM.yy} {6,time,HH:mm}' || chr(13) || chr(10) ||         '  Yer:          {7}maps?q={8},{9}&num=1&t=h&ie=UTF8&iwloc=near' || chr(13) || chr(10) ||         '  Mobil:         {10}' || chr(13) || chr(10) || chr(13) || chr(10) ||         'Cari LOGON sifarişi:' || chr(13) || chr(10) ||         '  Sayı:         {11}' || chr(13) || chr(10) ||         '  Müştəri:       {12} {13}' || chr(13) || chr(10) ||         '  Olduğu yer:       {14} {15}, {16} {17}' || chr(13) || chr(10) ||         '  Region:         {18}/{19}' || chr(13) || chr(10) ||         '  Satış rayonu: {20}' || chr(13) || chr(10) ||         '  Satış ofisi:   {21}' || chr(13) || chr(10) ||         '  Satış qrupu:    {22}' || chr(13) || chr(10) || chr(13) || chr(10) ||         'Bu e-mail PDA mesajına görə avtomatik yaradılıb. Bu e-mail ünvanına birbaşa cavab mümkün deyil.' || chr(13) || chr(10) ||         'Əlavə məlumat üçün, zəhmət olmasa, bu mesajın müəllifi ilə əlaqə saxlayın.' || chr(13) || chr(10) || chr(13) || chr(10) ||         'Hörmətlə,' || chr(13) || chr(10) ||         '{1} {2}');
execute upsert_tx('order.confirm.email.subject', 'az', 'Crh Sifarişlərin Təsdiqi şöbəsi {0}');
execute upsert_tx('order.confirm.email.order_item', 'az', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.email.message', 'az', 'Hörmətli müştəri,' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Crh şirkətinə sifariş verdiyiniz üçün sizə təşəkkür edirik.' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Sifarişin təsdiqi {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || 'Sizin rəyiniz {15}' || chr(13) || chr(10) || chr(13) || chr(10) ||            '{1}' || chr(13) || chr(10) ||            'Çatdırılma vaxtı' || chr(13) || chr(10) ||            '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) ||            '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Çatdırılma yeri' || chr(13) || chr(10) ||            '  {4} {5}' || chr(13) || chr(10) ||            '  {6} {7}' || chr(13) || chr(10) ||            '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) ||            '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Sifarişi verən:' || chr(13) || chr(10) ||            '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Sifarişin təfərrüatları və cari statusu barədə LOGON veb səhifəsindən öyrənə bilərsiniz.' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Hörmətlə,' || chr(13) || chr(10) ||            'Crh' || chr(13) || chr(10) || chr(13) || chr(10) ||            '-------------------' || chr(13) || chr(10) ||            'Bu e-mail avtomatik yaradılıb. Bu e-mail ünvanına birbaşa cavab mümkün deyil. Suallarınız yarandıqda, Crh daimi əlaqə nömrəsinə zəng edə və ya satış təmsilçisi ilə əlaqə saxlaya bilərsiniz.');
execute upsert_tx('load.confirm.email.subject', 'az', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'az', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'az', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.fax.order_item', 'az', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.fax.message', 'az', 'Crh Sifarişin təsdiqi {0}' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Hörmətli müştəri,' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Crh şirkətinə sifariş verdiyiniz üçün sizə təşəkkür edirik.' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Sifarişin təsdiqi {0}:' || chr(13) || chr(10) || chr(13) || chr(10) ||          '{1}' || chr(13) || chr(10) ||          'Çatdırılma vaxtı' || chr(13) || chr(10) ||          '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) ||          '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Çatdırılma yeri' || chr(13) || chr(10) ||          '  {4} {5}' || chr(13) || chr(10) ||          '  {6} {7}' || chr(13) || chr(10) ||          '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) ||          '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Sifarişi verən:' || chr(13) || chr(10) ||          '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Sifarişin təfərrüatları və cari statusu barədə LOGON veb səhifəsindən öyrənə bilərsiniz.' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Hörmətlə,' || chr(13) || chr(10) ||          'Crh' || chr(13) || chr(10) || chr(13) || chr(10) ||          '-------------------' || chr(13) || chr(10) ||          'Suallarınız yarandıqda, Crh daimi əlaqə nömrəsinə zəng edə və ya satış təmsilçisi ilə əlaqə saxlaya bilərsiniz.');
execute upsert_tx('load.confirm.fax.message', 'az', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.sms.order_item', 'az', '{0} {1} {2} ');
execute upsert_tx('order.confirm.sms.message', 'az', 'Crh Sifarişin təsdiqi {0} / {1}/ {6} / {2,date,dd.MM.yy} {2,time,HH:mm} - {3,time,HH:mm} - Təşəkkür edirik.');
execute upsert_tx('order.confirm.sms.message.1', 'az', 'Crh Sifarişin təsdiqi (1/2) {0} / {1}/ {6}');
execute upsert_tx('order.confirm.sms.message.2', 'az', 'Crh Sifarişin təsdiqi (2/2) {2,date,dd.MM.yy} {2,time,HH:mm} - {3,time,HH:mm} - Təşəkkür edirik.');
execute upsert_tx('load.confirm.sms.message', 'az', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'az', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'az', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('order.delivery.email.subject', 'az', 'Crh fakturası {0}');
execute upsert_tx('order.delivery.email.text', 'az', 'Hörmətli alıcı,' || chr(13) || chr(10)  || chr(13) || chr(10) ||          'Sizin sifariş üçün faktura əlavədə verilmişdir {0}.' || chr(13) || chr(10) || chr(13) || chr(10) ||          'hörmətlə,' || chr(13) || chr(10) ||          'Crh');
execute upsert_tx('special.rail.order.email.subj','az','Vaqon üçün sifariş {0} ({1})');
execute upsert_tx('special.rail.order.email.offer','az','Zəhmət olmasa, təklifinizi bizə göndərin');
execute upsert_tx('special.rail.order.email.rep','az','Təmir /texniki xidmət sifarişi: {0}' || chr(13) || chr(10) || 'Zədə kodları: {1}'|| chr(13) || chr(10));
execute upsert_tx('special.rail.order.email','az','Hörmətli alıcı,'|| chr(13) || chr(10) || chr(13) || chr(10) ||       'Aşağıdakı sifariş {0} indicə yaradılıb:'|| chr(13) || chr(10) || chr(13) || chr(10) ||       'Vaqon'|| chr(13) || chr(10) ||       '  {1}/{2}'|| chr(13) || chr(10) ||       '  {3} {4}'|| chr(13) || chr(10) || chr(13) || chr(10) ||       'Alıcı:'|| chr(13) || chr(10) ||       '  {5}'|| chr(13) || chr(10) || chr(13) || chr(10) ||       'Nəqletmə:'|| chr(13) || chr(10) ||       '  {6} ({7}) -> {8} ({9})'|| chr(13) || chr(10) || chr(13) || chr(10) ||       '{10}'|| chr(13) || chr(10) || chr(13) || chr(10) ||       'Qeydlər:'|| chr(13) || chr(10) ||       '{11}'|| chr(13) || chr(10) || chr(13) || chr(10) || chr(13) || chr(10) ||       '{12}'|| chr(13) || chr(10) || chr(13) || chr(10) ||       'Əlaqələndirici şəxs: {13}'|| chr(13) || chr(10) ||       'hörmətlə,'|| chr(13) || chr(10) ||       '  {14}'|| chr(13) || chr(10) || chr(13) || chr(10) ||       '------------------------------'|| chr(13) || chr(10) ||       '  {15}'|| chr(13) || chr(10) ||       '  {16}'|| chr(13) || chr(10) ||       '  {17}'|| chr(13) || chr(10) ||       'Bu e-mail avtomatik yaradılıb. Suallarınız olduqda, zəhmət olmasa, yuxarıdakı əlaqə məlumatlarından istifadə edin və ya rail-ch@crh.com ünvanına cavab yazın');
execute upsert_tx('new_password1.email.subject', 'az', 'Crh qeydiyyat parolunun bərpası ({0})');
execute upsert_tx('new_password1.email.text', 'az', 'Sizin yeni istifadəçi parolunuz ''''{0}'''' ({1}) ayrıca e-maildə göndəriləcək.');
execute upsert_tx('new_password2.email.subject', 'az', 'Parolun bərpası');
execute upsert_tx('new_password2.email.text', 'az', 'Sizin yeni parolunuz belədir:' || chr(13) || chr(10) || '{0}');

execute upsert_tx('password_reset_token.email.subject', 'az', 'İstifadəçi ''''{0}'''' üçün parolun bərpası qaydaları');
execute upsert_tx('password_reset_token.email.text', 'az',       'Hörmətli istifadəçi,'  || chr(13) || chr(10) || chr(13) || chr(10) ||       'İstifadəçi {0} üçün parolun bərpası sorğusu verilib.'|| chr(13) || chr(10) ||       'Parolunuzu bərpa etmək üçün aşağıdakı linkə basın və ya onu öz brauzerinizə yerləşdirin:' || chr(13) || chr(10) ||       '{1}' || chr(13) || chr(10) || chr(13) || chr(10) ||       'Parolun bərpası üçün sorğu göndərməyibsinizsə, bu e-maili iqnor edin..' || chr(13) || chr(10) || chr(13) || chr(10) ||       'Hörmətlə,' || chr(13) || chr(10) ||       'Crh');

execute upsert_tx('daily_reports.email.subject', 'az', 'Gündəlik hesabat üçün qeydiyyat {0,date,dd.MM.yy}');
execute upsert_tx('daily_reports.email.text', 'az', 'Zəhmət olmasa, əlavədən ''''{1}'''' hesabatını tapın.' || chr(13) || chr(10) || chr(13) || chr(10) || '{2}');
execute upsert_tx('staging_area_report.email.subject', 'az', 'Əməliyyat sahəsi hesabatı ({0}) {1,date,dd.MM.yy}');
execute upsert_tx('staging_area_report.email.text', 'az', 'Zəhmət olmasa, əlavədən ''''{0}'''' hesabatını tapın.');
execute upsert_tx('unlock_password.email.subject', 'az', 'Crh LOGON xarici logini bağlıdır ({0})');
execute upsert_tx('unlock_password.email.text', 'az', 'Hörmətli istifadəçi,' || chr(13) || chr(10) || chr(13) || chr(10) ||           'Sizin {1}-dəki {0} hesabınız baş tutmayan çoxsaylı qeydiyyat cəhdlərinizə görə {3}-dəki {2}-də bağlanıb. Siz aşağıdakı link vasitəsilə hesabınızı aça və parolunuzu bərpa edə bilərsiniz:'|| chr(13) || chr(10) || chr(13) || chr(10) ||           '{4}'|| chr(13) || chr(10) || chr(13) || chr(10) ||           'Siz {1}-də qeydiyyatdan keçirdiyiniz yeni parolunuzu e-mail vasitəsilə alacaqsınız.' || chr(13) || chr(10) || chr(13) || chr(10) ||           'hörmətlə,' || chr(13) || chr(10) ||           'Crh');
execute upsert_tx('unlock_pwdsucc.email.subject', 'az', 'Crh LOGON xarici logini bağlıdır ({0})');
execute upsert_tx('unlock_pwdsucc.email.text', 'az', 'Hörmətli istifadəçi,' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Sizin {1}-dəki {0} hesabınız {3}-dəki {2}-də müvəffəqiyyətlə açılmışdır.' || chr(13) || chr(10) || chr(13) || chr(10) ||          'hörmətlə,' || chr(13) || chr(10) ||          'Crh');
execute upsert_tx('order.deallocate.sms.message', 'az', 'SİFARİŞ {0} silinib');
execute upsert_tx('order.released.sms.message', 'az', 'SİFARİŞ {0} / Başlanğıc: {1} / Vaxt: {2}({3}) - {4}({5}) / Gəmi-hara: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');
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
execute upsert_tx('unloads.for.target.conn.back.email.text',  'az', 'Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject',  'az', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text',  'az', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.short.unload.email.subject', 'az', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'az', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');


prompt INSERTING into TLGNR_TEXT_TX - HR
execute upsert_tx('help.useradmin.authorization_dep', 'hr', 'Kod tvrtke -> Prodajno područje,  Prodajno područje -> Postrojenje, Postrojenja -> Dostavna točka' || chr(13) || chr(10) || 'Kupci -> Odredište');
execute upsert_tx('pdf.collective_deliverynote.footer.text', 'hr', 'Skupna otpremnica podnožje.');
execute upsert_tx('pdf.collective_deliverynote.footer.title', 'hr', 'Skupna otpremnica naslov');
execute upsert_tx('pdf.deliverynote.footer.title', 'hr', 'Špeditera uvjerava da');
execute upsert_tx('pdf.deliverynote.bimodal.truck', 'hr', 'Bimodalna isporuka od narudžbe željeznicom');
execute upsert_tx('pdf.deliverynote.bimodal.qty', 'hr', 'Ukupna količina željezničkih vagona');
execute upsert_tx('pdf.deliverynote.bimodal_ord_wagon_inf','hr','Bimodalna isporuka željezničkog bi (0), vagon (1), ukupni iznos na vagonu: (2)');
execute upsert_tx('pdf.deliverynote.customs', 'hr', 'Periodična kolektivna prijava prema ZG 42 Abs 1 Bes. c, s zahtjev / zahtjeva za povlaštenu carinjenje.Izvoznik robe, na koje se spominju u ovom dokumentu komercijalne, navodi, da su ti proizvodi su povlaštene CE proizvodi s podrijetlom.');
execute upsert_tx('pdf.deliverynote.footer.text', 'hr', 'Potrebna dozvola za prijevoz tereta je izdana. Opterećenje ne prelazi zakonski dopuštenu bruto težinu vozila. Dostava je deklarirana i namjenjena za primatelja.');
execute upsert_tx('pdf.deliverynote.ust-id', 'hr', 'DE142385541');
execute upsert_tx('pdf.delivnote.eichbeleg.intro', 'hr', 'Mjerenja slobodnih kalibriranih dodatnih komponenti. Osnovna mjerenja kalibracije  mogu biti pregledana.');
execute upsert_tx('pdf.delivnote.eichbeleg.tare', 'hr', 'Osnovni broj kalibracije: mjerenje težine tare {0}: {1} ({2}{3})');
execute upsert_tx('pdf.delivnote.eichbeleg.gross', 'hr', 'Mjerenje Bruto težine: {0}: {1} ({2}{3})');
execute upsert_tx('pdf.deliverynote.lds', 'hr', 'LDS');
execute upsert_tx('pdf.deliverynote.lt', 'hr', 'LT');
execute upsert_tx('pdf.deliverynote.custrefno', 'hr', 'Kupac Ref. Ne');
execute upsert_tx('pdf.deliverynote.contractno', 'hr', 'Ugovor');
execute upsert_tx('pdf.deliverynote.deliverytype', 'hr', ' ');
execute upsert_tx('marketinfo.email.subject', 'hr', 'Tržišni podaci iz {0} {1} ({2})');
execute upsert_tx('marketinfo.email.message', 'hr', 'Dragi primatelj,'  || chr(13) || chr(10)  || chr(13) || chr(10) ||         'Sljedeća tržišna poruka  je upravo kreirana:' || chr(13) || chr(10) || chr(13) || chr(10) ||         '---' || chr(13) || chr(10) ||         '  {0}' || chr(13) || chr(10) ||         '---' || chr(13) || chr(10) || chr(13) || chr(10) ||         'Informacije pošiljatelja:' || chr(13) || chr(10) ||         '  Pošiljatelj:         {1} {2} ({3})' || chr(13) || chr(10) ||         '  Vozilo:        {4}, {5}' || chr(13) || chr(10) ||         '  Datum:           {6,date,dd.MM.yy} {6,time,HH:mm}' || chr(13) || chr(10) ||         '  Mjesto:          {7}maps?q={8},{9}&num=1&t=h&ie=UTF8&iwloc=near' || chr(13) || chr(10) ||         '  Mobitel:         {10}' || chr(13) || chr(10) || chr(13) || chr(10) ||         'Trenutni Logon Order:' || chr(13) || chr(10) ||         '  Broj:         {11}' || chr(13) || chr(10) ||         '  Kupac:       {12} {13}' || chr(13) || chr(10) ||         '  Lokacija:       {14} {15}, {16} {17}' || chr(13) || chr(10) ||         '  Regija:         {18}/{19}' || chr(13) || chr(10) ||         '  Prodajna Općina: {20}' || chr(13) || chr(10) ||         '  Prodajni ured:   {21}' || chr(13) || chr(10) ||         '  Prodaja Grupa:    {22}' || chr(13) || chr(10) || chr(13) || chr(10) ||         'This email was created automatically because of a PDA message. A direct response to this email Adress is not possible.' || chr(13) || chr(10) ||         'Za sve dodatne informacije kontaktirajte autora ove poruke.' || chr(13) || chr(10) || chr(13) || chr(10) ||         'Uz srdačne pozdrave,' || chr(13) || chr(10) ||         '{1} {2}');
execute upsert_tx('order.confirm.email.subject', 'hr', 'Crh Potvrda o narudžbi {0}');
execute upsert_tx('order.confirm.email.order_item', 'hr', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.email.message', 'hr', 'Poštovani kupci,' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Puno hvala na vašoj narudžbi u Crhu.' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Potvrda narudžbe {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || 'Vaša referenca {15}' || chr(13) || chr(10) || chr(13) || chr(10) ||            '{1}' || chr(13) || chr(10) ||            'Vrijeme isporuke' || chr(13) || chr(10) ||            '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) ||            '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Mjesto isporuke' || chr(13) || chr(10) ||            '  {4} {5}' || chr(13) || chr(10) ||            '  {6} {7}' || chr(13) || chr(10) ||            '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) ||            '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Naručitelj:' || chr(13) || chr(10) ||            '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Više informacija i trenutni status narudžbe može se naći na web stranici LOGON.' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Lijep pozdrav,' || chr(13) || chr(10) ||            'Crh' || chr(13) || chr(10) || chr(13) || chr(10) ||            '-------------------' || chr(13) || chr(10) ||            'Ovaj e-mail je stvoren automatski.Izravan odgovor na ovu adresu e-pošte nije moguć. U slučaju pitanja molimo nazvati svoj redoviti Crh kontakt ili prodajnog predstavnika.');
execute upsert_tx('load.confirm.email.subject', 'hr', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'hr', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'hr', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.fax.order_item', 'hr', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.fax.message', 'hr', 'Crh Potvrda o narudžbi {0}' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Poštovani kupci,' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Puno hvala na vašoj narudžbi u Crhu.' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Potvrda narudžbe {0}:' || chr(13) || chr(10) || chr(13) || chr(10) ||          '{1}' || chr(13) || chr(10) ||          'Vrijeme isporuke' || chr(13) || chr(10) ||          '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) ||          '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Mjesto isporuke' || chr(13) || chr(10) ||          '  {4} {5}' || chr(13) || chr(10) ||          '  {6} {7}' || chr(13) || chr(10) ||          '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) ||          '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Naručitelj:' || chr(13) || chr(10) ||          '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Više informacija i trenutni status narudžbe može se naći na web stranici LOGON.' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Lijep pozdrav,' || chr(13) || chr(10) ||          'Crh' || chr(13) || chr(10) || chr(13) || chr(10) ||          '-------------------' || chr(13) || chr(10) ||          'U slučaju pitanja molimo nazvati svoj redoviti Crh kontakt ili prodajnog predstavnika.');
execute upsert_tx('load.confirm.fax.message', 'hr', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.sms.order_item', 'hr', '{0} {1} {2} ');
execute upsert_tx('order.confirm.sms.message', 'hr', 'Crh Potvrda o narudžbi {0} / {1}/ {6} / {2,date,dd.MM.yy} {2,time,HH:mm} - {3,time,HH:mm} - Hvala Vam.');
execute upsert_tx('order.confirm.sms.message.1', 'hr', 'Crh Potvrda o narudžbi (1/2) {0} / {1}/ {6}');
execute upsert_tx('order.confirm.sms.message.2', 'hr', 'Crh Potvrda o narudžbi (2/2) {2,date,dd.MM.yy} {2,time,HH:mm} - {3,time,HH:mm} - Hvala Vam.');
execute upsert_tx('load.confirm.sms.message', 'hr', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'hr', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'hr', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('order.delivery.email.subject', 'hr', 'Crh Otpremnica {0}');
execute upsert_tx('order.delivery.email.text', 'hr', 'Dragi primatelj,' || chr(13) || chr(10)  || chr(13) || chr(10) ||          'U prilogu možete pronaći otpremnicu za svoju narudžbu {0}.' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Uz srdačne pozdrave,' || chr(13) || chr(10) ||          'Crh');
execute upsert_tx('special.rail.order.email.subj','hr','Nalog za vagon {0} ({1})');
execute upsert_tx('special.rail.order.email.offer','hr','Molim Vas pošaljite nam ponudu');
execute upsert_tx('special.rail.order.email.rep','hr','Popravak / Održavanje narudžba: {0}' || chr(13) || chr(10) || 'Oznaka štete: {1}'|| chr(13) || chr(10));
execute upsert_tx('special.rail.order.email','hr','Dragi primatelj,'|| chr(13) || chr(10) || chr(13) || chr(10) ||       'Sljedeća narudžba {0} je upravo kreirana:'|| chr(13) || chr(10) || chr(13) || chr(10) ||       'Vagon:'|| chr(13) || chr(10) ||       '  {1}/{2}'|| chr(13) || chr(10) ||       '  {3} {4}'|| chr(13) || chr(10) || chr(13) || chr(10) ||       'Primatelj:'|| chr(13) || chr(10) ||       '  {5}'|| chr(13) || chr(10) || chr(13) || chr(10) ||       'Prijevoz:'|| chr(13) || chr(10) ||       '  {6} ({7}) -> {8} ({9})'|| chr(13) || chr(10) || chr(13) || chr(10) ||       '{10}'|| chr(13) || chr(10) || chr(13) || chr(10) ||       'Primjedbe:'|| chr(13) || chr(10) ||       '{11}'|| chr(13) || chr(10) || chr(13) || chr(10) || chr(13) || chr(10) ||       '{12}'|| chr(13) || chr(10) || chr(13) || chr(10) ||       'Osoba za kontakt: {13}'|| chr(13) || chr(10) ||       's poštovanjem,'|| chr(13) || chr(10) ||       '  {14}'|| chr(13) || chr(10) || chr(13) || chr(10) ||       '------------------------------'|| chr(13) || chr(10) ||       '  {15}'|| chr(13) || chr(10) ||       '  {16}'|| chr(13) || chr(10) ||       '  {17}'|| chr(13) || chr(10) ||       'Ovaj e-mail je generiran automatski. U slučaju pitanja molimo Vas da koristite kontakt iznad ili odgovorite na rail-ch@crh.com');
execute upsert_tx('new_password1.email.subject', 'hr', 'Crh Logon Resetiranje lozinki ({0})');
execute upsert_tx('new_password1.email.text', 'hr', 'Nova lozinka za korisnika ''''{0}'''' ({1}) će biti poslana u zasebnom e-mail-u.');
execute upsert_tx('new_password2.email.subject', 'hr', 'Resetiranje lozinki');
execute upsert_tx('new_password2.email.text', 'hr', 'Vaša nova lozinka je:' || chr(13) || chr(10) || '{0}');

execute upsert_tx('password_reset_token.email.subject', 'hr', 'Resetiranja lozinki - Upute za korisnike ''''{0}''''');
execute upsert_tx('password_reset_token.email.text', 'hr',       'Poštovani korisnik,'  || chr(13) || chr(10) || chr(13) || chr(10) ||       'Zahtjev za ponovno postavljanje lozinke je napravljena za korisnika {0}.'|| chr(13) || chr(10) ||       'Za resetiranje lozinke, kliknite na sljedeći link ili ga zalijepite u vašem pregledniku:' || chr(13) || chr(10) ||       '{1}' || chr(13) || chr(10) || chr(13) || chr(10) ||       'Ako niste zatražili resetiranje zaporke, zanemarite ovaj e-mail.' || chr(13) || chr(10) || chr(13) || chr(10) ||       'Uz srdačne pozdrave,' || chr(13) || chr(10) ||       'Crh');

execute upsert_tx('daily_reports.email.subject', 'hr', 'Prijava dnevnog izvješća {0,date,dd.MM.yy}');
execute upsert_tx('daily_reports.email.text', 'hr', 'Molimo pronađite izvješće ''''{1}'''' prilogu.' || chr(13) || chr(10) || chr(13) || chr(10) || '{2}');
execute upsert_tx('staging_area_report.email.subject', 'hr', 'Izvješću o određenom području ({0}) {1,date,dd.MM.yy}');
execute upsert_tx('staging_area_report.email.text', 'hr', 'Molimo pronađite izvješće ''''{0}'''' prilogu.');
execute upsert_tx('unlock_password.email.subject', 'hr', 'Crh Logon vanjska prijava zaključana ({0})');
execute upsert_tx('unlock_password.email.text', 'hr', 'Poštovani korisnik,' || chr(13) || chr(10) || chr(13) || chr(10) ||           'vaš račun {0} u {1} je zaključan  {2}, na {3} na zbog prevelikog broja neuspjelih pokušaja prijave. Možete otključati vaš račun i resetirati vašu lozinku putem sljedećeg linka:'|| chr(13) || chr(10) || chr(13) || chr(10) ||           '{4}'|| chr(13) || chr(10) || chr(13) || chr(10) ||           'Vi ćete dobiti novu lozinku koju ste registrirali u {1} putem e-maila.' || chr(13) || chr(10) || chr(13) || chr(10) ||           'Uz srdačne pozdrave,' || chr(13) || chr(10) ||           'Crh');
execute upsert_tx('unlock_pwdsucc.email.subject', 'hr', 'Crh Logon vanjska prijava otključana ({0})');
execute upsert_tx('unlock_pwdsucc.email.text', 'hr', 'Poštovani korisnik,' || chr(13) || chr(10) || chr(13) || chr(10) ||          'vaš račun {0} u {1} je uspješno otključan {2}, na {3}.' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Uz srdačne pozdrave,' || chr(13) || chr(10) ||          'Crh');
execute upsert_tx('order.deallocate.sms.message', 'hr', 'NALOG {0} je izbrisan');
execute upsert_tx('order.released.sms.message', 'hr', 'NALOG {0} / Početak: {1} / vrijeme: {2}({3}) - {4}({5}) / Odredište: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');
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
execute upsert_tx('unloads.for.target.conn.back.email.text',  'hr', 'Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject',  'hr', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text',  'hr', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.short.unload.email.subject', 'hr', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'hr', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');


prompt INSERTING into TLGNR_TEXT_TX - SR
execute upsert_tx('help.useradmin.authorization_dep', 'sr', 'Company Code -> Sales Areas, Sales Areas -> Plants, Plants -> Shippingpoint' || chr(13) || chr(10) || 'Kupci -> Odredište');
execute upsert_tx('pdf.collective_deliverynote.footer.text', 'sr', 'Zbirna otpremnica (zaglavlje).');
execute upsert_tx('pdf.collective_deliverynote.footer.title', 'sr', 'Zbirna otpremnica (nalov zaglavlja)');
execute upsert_tx('pdf.deliverynote.footer.title', 'sr', 'Prevoznik se obavezuje da');
execute upsert_tx('pdf.deliverynote.bimodal.truck', 'sr', 'Bimodalna isporuka naloga železnicom');
execute upsert_tx('pdf.deliverynote.bimodal.qty', 'sr', 'Ukupan broj vagona');
execute upsert_tx('pdf.deliverynote.bimodal_ord_wagon_inf','sr','Bimodal delivery of rail order (0), wagon (1), total amount on railway wagon: (2)');
execute upsert_tx('pdf.deliverynote.customs', 'sr', 'Periodical collective application according to ZG 42 Abs 1  Bes. c, with request/application for preferential clearance. The exporter of the goods, to which are being referred to in this commercial document, states, that these goods are preferential CE originating products.');
execute upsert_tx('pdf.deliverynote.footer.text', 'sr', 'Data je potrebna dozvola za prevoz naznačenog tereta. Opterećenje ne prelazi zakonom dozvoljenu bruto težinu vozila. Deklarisana roba je namenjena za isporuku.');
execute upsert_tx('pdf.deliverynote.ust-id', 'sr', 'DE142385541');
execute upsert_tx('pdf.delivnote.eichbeleg.intro', 'sr', 'Merenja slobodnih kalibrisanih dodatnih komponenti. Osnovna kalibraciona merenja mogu biti pregledani.');
execute upsert_tx('pdf.delivnote.eichbeleg.tare', 'sr', 'Broj osnovne kalibracije: Sopstvena teživa merenja {0}: {1} ({2}{3})');
execute upsert_tx('pdf.delivnote.eichbeleg.gross', 'sr', 'Merenje bruto težine: {0}: {1} ({2}{3})');
execute upsert_tx('pdf.deliverynote.lds', 'sr', 'LDS');
execute upsert_tx('pdf.deliverynote.lt', 'sr', 'LT');
execute upsert_tx('pdf.deliverynote.custrefno', 'sr', 'Kupčev Ref. No');
execute upsert_tx('pdf.deliverynote.contractno', 'sr', 'Ugovor');
execute upsert_tx('pdf.deliverynote.deliverytype', 'sr', ' ');
execute upsert_tx('marketinfo.email.subject', 'sr', 'Tržišna informacija od {0} {1} ({2})');
execute upsert_tx('marketinfo.email.message', 'sr', 'Poštovani,'  || chr(13) || chr(10)  || chr(13) || chr(10) ||         'Sledeća tržišna poruka  je upravo kreirana:' || chr(13) || chr(10) || chr(13) || chr(10) ||         '---' || chr(13) || chr(10) ||         '  {0}' || chr(13) || chr(10) ||         '---' || chr(13) || chr(10) || chr(13) || chr(10) ||         'Podaci o pošiljaocu:' || chr(13) || chr(10) ||         '  Pošiljalac:         {1} {2} ({3})' || chr(13) || chr(10) ||         '  Vozilo:        {4}, {5}' || chr(13) || chr(10) ||         '  Datum:           {6,date,dd.MM.yy} {6,time,HH:mm}' || chr(13) || chr(10) ||         '  Mesto:          {7}maps?q={8},{9}&num=1&t=h&ie=UTF8&iwloc=near' || chr(13) || chr(10) ||         '  Broj mobilnog telefona:         {10}' || chr(13) || chr(10) || chr(13) || chr(10) ||         'Trenutni LOGON nalog:' || chr(13) || chr(10) ||         '  Broj:         {11}' || chr(13) || chr(10) ||         '  Kupac:      {12} {13}' || chr(13) || chr(10) ||         '  Lokacija:       {14} {15}, {16} {17}' || chr(13) || chr(10) ||         '  Region:         {18}/{19}' || chr(13) || chr(10) ||         '  Sales District: {20}' || chr(13) || chr(10) ||         '  Sales Office:   {21}' || chr(13) || chr(10) ||         '  Sales Group:    {22}' || chr(13) || chr(10) || chr(13) || chr(10) ||         'This email was created automatically because of a PDA message. A direct response to this email Adress is not possible.' || chr(13) || chr(10) ||         'Za dodatne informacije stojimo na raspolaganju.' || chr(13) || chr(10) || chr(13) || chr(10) ||         'S poštovanjem,' || chr(13) || chr(10) ||         '{1} {2}');
execute upsert_tx('order.confirm.email.subject', 'sr', 'Crh Potvrda o narudžbi {0}');
execute upsert_tx('order.confirm.email.order_item', 'sr', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.email.message', 'sr', 'Poštovani,' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Zahvaljujemo se na Vašoj porudžbini.' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Potvrda narudžbe {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || 'Ваш референтни {15}' || chr(13) || chr(10) || chr(13) || chr(10) ||            '{1}' || chr(13) || chr(10) ||            'Vreme isporuke' || chr(13) || chr(10) ||            '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) ||            '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Lokacija isporuke' || chr(13) || chr(10) ||            '  {4} {5}' || chr(13) || chr(10) ||            '  {6} {7}' || chr(13) || chr(10) ||            '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) ||            '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Poručio:' || chr(13) || chr(10) ||            '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Više informacija i trenutni status narudžbe može se naći na web stranici LOGON.' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Srdačan pozdrav,' || chr(13) || chr(10) ||            'Crh' || chr(13) || chr(10) || chr(13) || chr(10) ||            '-------------------' || chr(13) || chr(10) ||            'Ovaj e-mail je kreiran automatski. Direktan odgovor na ovu adresu e-pošte nije moguć. U slučaju pitanja molimo pozvati Crh info telefon ili prodajnog predstavnika.');
execute upsert_tx('load.confirm.email.subject', 'sr', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'sr', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'sr', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.fax.order_item', 'sr', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.fax.message', 'sr', 'Crh Potvrda o narudžbi {0}' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Poštovani,' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Zahvaljujemo se na Vašoj porudžbini.' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Potvrda narudžbe {0}:' || chr(13) || chr(10) || chr(13) || chr(10) ||          '{1}' || chr(13) || chr(10) ||          'Vreme isporuke' || chr(13) || chr(10) ||          '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) ||          '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Lokacija isporuke' || chr(13) || chr(10) ||          '  {4} {5}' || chr(13) || chr(10) ||          '  {6} {7}' || chr(13) || chr(10) ||          '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) ||          '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Poručio:' || chr(13) || chr(10) ||          '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Više informacija i trenutni status narudžbe može se naći na web stranici LOGON.' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Srdačan pozdrav,' || chr(13) || chr(10) ||          'Crh' || chr(13) || chr(10) || chr(13) || chr(10) ||          '-------------------' || chr(13) || chr(10) ||          'U slučaju pitanja molimo pozvati Crh info telefon ili prodajnog predstavnika.');
execute upsert_tx('load.confirm.fax.message', 'sr', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.sms.order_item', 'sr', '{0} {1} {2} ');
execute upsert_tx('order.confirm.sms.message', 'sr', 'Crh Potvrda o narudžbi {0} / {1}/ {6} / {2,date,dd.MM.yy} {2,time,HH:mm} - {3,time,HH:mm} - Hvala Vam.');
execute upsert_tx('order.confirm.sms.message.1', 'sr', 'Crh Potvrda o narudžbi (1/2) {0} / {1}/ {6}');
execute upsert_tx('order.confirm.sms.message.2', 'sr', 'Crh Potvrda o narudžbi (2/2) {2,date,dd.MM.yy} {2,time,HH:mm} - {3,time,HH:mm} - Hvala Vam.');
execute upsert_tx('load.confirm.sms.message', 'sr', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'sr', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'sr', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('order.delivery.email.subject', 'sr', 'Crh otpremnica {0}');
execute upsert_tx('order.delivery.email.text', 'sr', 'Poštovani,' || chr(13) || chr(10)  || chr(13) || chr(10) ||          'U prilogu možete pronaći otpremnicu za svoju narudžbinu {0}.' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Uz srdačne pozdrave,' || chr(13) || chr(10) ||          'Crh');
execute upsert_tx('special.rail.order.email.subj','sr','Nalog za vagon {0} ({1})');
execute upsert_tx('special.rail.order.email.offer','sr','Molim Vas pošaljite nam ponudu');
execute upsert_tx('special.rail.order.email.rep','sr','Popravka / Narudžbina za održavanje: {0}' || chr(13) || chr(10) || 'Oznaka štete: {1}'|| chr(13) || chr(10));
execute upsert_tx('special.rail.order.email','sr','Poštovani,'|| chr(13) || chr(10) || chr(13) || chr(10) ||       'Sljedeća narudžbina {0} je upravo kreirana:'|| chr(13) || chr(10) || chr(13) || chr(10) ||       'Vagon:'|| chr(13) || chr(10) ||       '  {1}/{2}'|| chr(13) || chr(10) ||       '  {3} {4}'|| chr(13) || chr(10) || chr(13) || chr(10) ||       'Primalac:'|| chr(13) || chr(10) ||       '  {5}'|| chr(13) || chr(10) || chr(13) || chr(10) ||       'Prevoz:'|| chr(13) || chr(10) ||       '  {6} ({7}) -> {8} ({9})'|| chr(13) || chr(10) || chr(13) || chr(10) ||       '{10}'|| chr(13) || chr(10) || chr(13) || chr(10) ||       'Primedbe:'|| chr(13) || chr(10) ||       '{11}'|| chr(13) || chr(10) || chr(13) || chr(10) || chr(13) || chr(10) ||       '{12}'|| chr(13) || chr(10) || chr(13) || chr(10) ||       'Osoba za kontakt: {13}'|| chr(13) || chr(10) ||       's poštovanjem,'|| chr(13) || chr(10) ||       '  {14}'|| chr(13) || chr(10) || chr(13) || chr(10) ||       '------------------------------'|| chr(13) || chr(10) ||       '  {15}'|| chr(13) || chr(10) ||       '  {16}'|| chr(13) || chr(10) ||       '  {17}'|| chr(13) || chr(10) ||       'Ovaj e-mail je generiran automatski. U slučaju pitanja molimo Vas da koristite kontakt iznad');
execute upsert_tx('new_password1.email.subject', 'sr', 'Crh Logon Resetovanje lozinki ({0})');
execute upsert_tx('new_password1.email.text', 'sr', 'Nova lozinka za korisnika ''''{0}'''' ({1}) će biti poslata u zasebnom e-mail-u.');
execute upsert_tx('new_password2.email.subject', 'sr', 'Resetovanje lozinki');
execute upsert_tx('new_password2.email.text', 'sr', 'YVaša nova lozinka je:' || chr(13) || chr(10) || '{0}');

execute upsert_tx('password_reset_token.email.subject', 'sr', 'Resetovanje lozinki - Instrukcije za korisnike ''''{0}''''');
execute upsert_tx('password_reset_token.email.text', 'sr',       'Poštovani korisniče,'  || chr(13) || chr(10) || chr(13) || chr(10) ||       'Zahtev za ponovno postavljanje lozinke je napravljena za korisnika {0}.'|| chr(13) || chr(10) ||       'Za resetovanje lozinke, kliknite na sledeći link ili ga kopirajte u Vašem internet pregledaču:' || chr(13) || chr(10) ||       '{1}' || chr(13) || chr(10) || chr(13) || chr(10) ||       'Ako niste zatražili resetovanje šifre, zanemarite ovaj e-mail.' || chr(13) || chr(10) || chr(13) || chr(10) ||       'Uz srdačne pozdrave,' || chr(13) || chr(10) ||       'Crh');

execute upsert_tx('daily_reports.email.subject', 'sr', 'Prijava dnevnog izvještaja {0,date,dd.MM.yy}');
execute upsert_tx('daily_reports.email.text', 'sr', 'Molimo pronađite izvještaj ''''{1}'''' u prilogu.' || chr(13) || chr(10) || chr(13) || chr(10) || '{2}');
execute upsert_tx('staging_area_report.email.subject', 'sr', 'Izveštaj o određenom području ({0}) {1,date,dd.MM.yy}');
execute upsert_tx('staging_area_report.email.text', 'sr', 'Molimo pronađite izvještaj ''''{0}'''' u prilogu.');
execute upsert_tx('unlock_password.email.subject', 'sr', 'Crh Logon eksterna prijava zaključana ({0})');
execute upsert_tx('unlock_password.email.text', 'sr', 'Poštovani korisniče,' || chr(13) || chr(10) || chr(13) || chr(10) ||           'vaš račun {0} u {1} je zaključan {2}, na {3} zbog prevelikog broja neuspelih pokušaja prijave. Možete otključati Vaš račun i resetovati Vašu lozinku putem sledećeg linka:'|| chr(13) || chr(10) || chr(13) || chr(10) ||           '{4}'|| chr(13) || chr(10) || chr(13) || chr(10) ||           'Vi ćete dobiti novu lozinku koju ste registrovali u {1} putem e-maila.' || chr(13) || chr(10) || chr(13) || chr(10) ||           'Uz srdačne pozdrave,' || chr(13) || chr(10) ||           'Crh');
execute upsert_tx('unlock_pwdsucc.email.subject', 'sr', 'Crh Logon eksterna prijava otključana ({0})');
execute upsert_tx('unlock_pwdsucc.email.text', 'sr', 'Poštovani korisniče,' || chr(13) || chr(10) || chr(13) || chr(10) ||          'vaš račun {0} u {1} je uspešno otključan {2}, na {3}.' || chr(13) || chr(10) || chr(13) || chr(10) ||          'Uz srdačne pozdrave,' || chr(13) || chr(10) ||          'Crh');
execute upsert_tx('order.deallocate.sms.message', 'sr', 'NALOG {0} je izbrisan');
execute upsert_tx('order.released.sms.message', 'sr', 'NALOG {0} / Početak: {1} / vreme: {2}({3}) - {4}({5}) / Odredište: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');
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
execute upsert_tx('unloads.for.target.conn.back.email.text',  'sr', 'Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject',  'sr', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text',  'sr', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.short.unload.email.subject', 'sr', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'sr', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');


prompt INSERTING into TLGNR_TEXT_TX - TR
execute upsert_tx('help.useradmin.authorization_dep', 'tr', 'Company Code -> Sales Areas, Sales Areas -> Plants, Plants -> Shippingpoint' || chr(13) || chr(10) || 'Customers -> Ship To');
execute upsert_tx('pdf.collective_deliverynote.footer.text', 'tr', 'Collective del. note footer text.');
execute upsert_tx('pdf.collective_deliverynote.footer.title', 'tr', 'Collective del. note footer title');
execute upsert_tx('pdf.deliverynote.footer.title', 'tr', 'The forwarding agent assures that');
execute upsert_tx('pdf.deliverynote.bimodal.truck', 'tr', 'Bimodal delivery from rail order');
execute upsert_tx('pdf.deliverynote.bimodal.qty', 'tr', 'Total amount of railway wagons');
execute upsert_tx('pdf.deliverynote.bimodal_ord_wagon_inf','tr','Bimodal delivery of rail order (0), wagon (1), total amount on railway wagon: (2)');
execute upsert_tx('pdf.deliverynote.customs', 'tr', 'Periodical collective application according to ZG 42 Abs 1  Bes. c, with request/application for preferential clearance. The exporter of the goods, to which are being referred to in this commercial document, states, that these goods are preferential CE originating products.');
execute upsert_tx('pdf.deliverynote.footer.text', 'tr', 'The required permission to transport this freight is given. The load does not exceed the legally allowed gross weight of the vehicle. The delivery is intended for the declared goods recipient.');
execute upsert_tx('pdf.deliverynote.ust-id', 'tr', 'DE142385541');
execute upsert_tx('pdf.delivnote.eichbeleg.intro', 'tr', 'Measurements of free calibrated additional components. Basic calibration measurements can be reviewed.');
execute upsert_tx('pdf.delivnote.eichbeleg.tare', 'tr', 'Basic calibration number: tare weight measurement {0}: {1} ({2}{3})');
execute upsert_tx('pdf.delivnote.eichbeleg.gross', 'tr', 'Gross weight measurement: {0}: {1} ({2}{3})');
execute upsert_tx('pdf.deliverynote.lds', 'tr', 'LDS');
execute upsert_tx('pdf.deliverynote.lt', 'tr', 'LT');
execute upsert_tx('pdf.deliverynote.custrefno', 'tr', 'Customer Ref. No');
execute upsert_tx('pdf.deliverynote.contractno', 'tr', 'Sözleşme');
execute upsert_tx('pdf.deliverynote.deliverytype', 'tr', ' ');
execute upsert_tx('marketinfo.email.subject', 'tr', 'Market information from {0} {1} ({2})');
execute upsert_tx('marketinfo.email.message', 'tr', 'Dear recipient,'  || chr(13) || chr(10)  || chr(13) || chr(10) || 'The following market message was just created:' || chr(13) || chr(10) || chr(13) || chr(10) || '---' || chr(13) || chr(10) || '  {0}' || chr(13) || chr(10) || '---' || chr(13) || chr(10) || chr(13) || chr(10) || 'Sender Information:' || chr(13) || chr(10) || '  Sender:         {1} {2} ({3})' || chr(13) || chr(10) || '  Vehicle:        {4}, {5}' || chr(13) || chr(10) || '  Date:           {6,date,dd.MM.yy} {6,time,HH:mm}' || chr(13) || chr(10) || '  Place:          {7}maps?q={8},{9}&num=1&t=h&ie=UTF8&iwloc=near' || chr(13) || chr(10) || '  Mobile:         {10}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Current LOGON Order:' || chr(13) || chr(10) || '  Number:         {11}' || chr(13) || chr(10) || '  Customer:       {12} {13}' || chr(13) || chr(10) || '  Location:       {14} {15}, {16} {17}' || chr(13) || chr(10) || '  Region:         {18}/{19}' || chr(13) || chr(10) || '  Sales District: {20}' || chr(13) || chr(10) || '  Sales Office:   {21}' || chr(13) || chr(10) || '  Sales Group:    {22}' || chr(13) || chr(10) || chr(13) || chr(10) || 'This email was created automatically because of a PDA message. A direct response to this email Adress is not possible.' || chr(13) || chr(10) || 'For further information please contact the author of this message.' || chr(13) || chr(10) || chr(13) || chr(10) || 'with best regards,' || chr(13) || chr(10) || '{1} {2}');
execute upsert_tx('order.confirm.email.subject', 'tr', 'Crh Order Confirmation {0}');
execute upsert_tx('order.confirm.email.order_item', 'tr', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.email.message', 'tr', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Many thanks for your order at Crh.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Order confirmation {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your reference {15}' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Delivery Time' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Delivery Location' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.email.subject', 'tr', 'Crh Load Confirmation {0}');
execute upsert_tx('load.confirm.email.order_item', 'tr', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('load.confirm.email.message', 'tr', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.fax.order_item', 'tr', '  {0} {1}' || chr(9) || chr(9) || '{2}' || chr(13) || chr(10));
execute upsert_tx('order.confirm.fax.message', 'tr', 'Crh Order Confirmation {0}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Many thanks for your order at Crh.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Order confirmation {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Delivery Time' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Delivery Location' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('load.confirm.fax.message', 'tr', 'Crh Load Confirmation {2}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your delivery to {0} {1} (nr {2})'  || chr(13) || chr(10) || 'Has been loaded in truck/wagon {3} ({4})' || chr(13) || chr(10) || 'with {5} ' || chr(13) || chr(10) || 'and is planned to arrive after {6,date,EEEE dd.MM.yyyy} {6,time,HH:mm}.'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {7}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'Crh' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular Crh contact or sales representative.');
execute upsert_tx('order.confirm.sms.order_item', 'tr', '{0} {1} {2} ');
execute upsert_tx('order.confirm.sms.message', 'tr', 'Crh order confirmation {0} / {1}/ {6} / {2,date,dd.MM.yy} {2,time,HH:mm} - {3,time,HH:mm} - Thank you.');
execute upsert_tx('order.confirm.sms.message.1', 'tr', 'Crh order confirmation (1/2) {0} / {1}/ {6}');
execute upsert_tx('order.confirm.sms.message.2', 'tr', 'Crh order confirmation (2/2) {2,date,dd.MM.yy} {2,time,HH:mm} - {3,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message', 'tr', 'Crh load confirmation {2} / {5} / {0} {1} / {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('load.confirm.sms.message.1', 'tr', 'Crh load confirmation (1/2) {2} / {5}/ {0} {1}');
execute upsert_tx('load.confirm.sms.message.2', 'tr', 'Crh load confirmation (2/2) {6,date,dd.MM.yy} {6,time,HH:mm} - Thank you.');
execute upsert_tx('order.delivery.email.subject', 'tr', 'Crh Delivery Note {0}');
execute upsert_tx('order.delivery.email.text', 'tr', 'Dear recipient,' || chr(13) || chr(10)  || chr(13) || chr(10) || 'Attached please find the delivery note to your order {0}.' || chr(13) || chr(10) || chr(13) || chr(10) || 'with best regards,' || chr(13) || chr(10) || 'Crh');
execute upsert_tx('special.rail.order.email.subj','tr','Order for wagon {0} ({1})');
execute upsert_tx('special.rail.order.email.offer','tr','Please send us your offer');
execute upsert_tx('special.rail.order.email.rep','tr','Repair/Maintenance Order: {0}' || chr(13) || chr(10) || 'Damage codes: {1}'|| chr(13) || chr(10));
execute upsert_tx('special.rail.order.email','tr','Dear recipient,'|| chr(13) || chr(10) || chr(13) || chr(10) || 'The following order {0} was just created:'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Wagon:'|| chr(13) || chr(10) || '  {1}/{2}'|| chr(13) || chr(10) || '  {3} {4}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Recipient:'|| chr(13) || chr(10) || '  {5}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Transport:'|| chr(13) || chr(10) || '  {6} ({7}) -> {8} ({9})'|| chr(13) || chr(10) || chr(13) || chr(10) || '{10}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Remarks:'|| chr(13) || chr(10) || '{11}'|| chr(13) || chr(10) || chr(13) || chr(10) || chr(13) || chr(10) || '{12}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Contact person: {13}'|| chr(13) || chr(10) || 'with kind regards,'|| chr(13) || chr(10) || '  {14}'|| chr(13) || chr(10) || chr(13) || chr(10) || '------------------------------'|| chr(13) || chr(10) || '  {15}'|| chr(13) || chr(10) || '  {16}'|| chr(13) || chr(10) || '  {17}'|| chr(13) || chr(10) || 'This email was generated automatically. In case of questions please use the contact details above or reply to rail-ch@crh.com');
execute upsert_tx('new_password1.email.subject', 'tr', 'Crh Logon Password reset ({0})');
execute upsert_tx('new_password1.email.text', 'tr', 'Your new password for user ''''{0}'''' ({1}) will be sent in a separate email.');
execute upsert_tx('new_password2.email.subject', 'tr', 'Password reset');
execute upsert_tx('new_password2.email.text', 'tr', 'Your new password is:' || chr(13) || chr(10) || '{0}');

execute upsert_tx('password_reset_token.email.subject', 'tr', 'Password reset instructions for user ''''{0}''''');
execute upsert_tx('password_reset_token.email.text', 'tr', 'Dear user,'  || chr(13) || chr(10) || chr(13) || chr(10) || 'A password reset request has been made for the user {0}.'|| chr(13) || chr(10) || 'To reset your password, click the following link or paste it in your browser:' || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || chr(13) || chr(10) || 'If you did not request a password reset, please ignore this e-mail.' || chr(13) || chr(10) || chr(13) || chr(10) || 'With best regards,' || chr(13) || chr(10) || 'Crh');

execute upsert_tx('daily_reports.email.subject', 'tr', 'Logon daily report {0,date,dd.MM.yy}');
execute upsert_tx('daily_reports.email.text', 'tr', 'Please find the report ''''{1}'''' attached.' || chr(13) || chr(10) || chr(13) || chr(10) || '{2}');
execute upsert_tx('staging_area_report.email.subject', 'tr', 'Staging area report ({0}) {1,date,dd.MM.yy}');
execute upsert_tx('staging_area_report.email.text', 'tr', 'Please find the report ''''{0}'''' attached.');
execute upsert_tx('unlock_password.email.subject', 'tr', 'Crh LOGON external login locked ({0})');
execute upsert_tx('unlock_password.email.text', 'tr', 'Dear user,' || chr(13) || chr(10) || chr(13) || chr(10) || 'your account {0} in {1} has been locked on {2}, at {3} due to too many failed login attempts. You can unlock your account and reset your password via the following link:'|| chr(13) || chr(10) || chr(13) || chr(10) || '{4}'|| chr(13) || chr(10) || chr(13) || chr(10) || 'You will receive your new password you have registered in {1} via email.' || chr(13) || chr(10) || chr(13) || chr(10) || 'with best regards,' || chr(13) || chr(10) || 'Crh');
execute upsert_tx('unlock_pwdsucc.email.subject', 'tr', 'Crh LOGON external login unlocked ({0})');
execute upsert_tx('unlock_pwdsucc.email.text', 'tr', 'Dear user,' || chr(13) || chr(10) || chr(13) || chr(10) || 'your account {0} in {1} was successfully unlocked on {2}, at {3}.' || chr(13) || chr(10) || chr(13) || chr(10) || 'with best regards,' || chr(13) || chr(10) || 'Crh');
execute upsert_tx('order.deallocate.sms.message', 'tr', 'ORDER {0} has been deleted');
execute upsert_tx('order.released.sms.message', 'tr', 'ORDER {0} / Start: {1} / Time: {2}({3}) - {4}({5}) / Ship-to: {6}, {7}, {8}, {9}{10,choice,0# / PIN: {11}|1#}');
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
execute upsert_tx('unloads.for.target.conn.back.email.text',  'tr', 'Connection to iTrack recovered.  Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.reloaded.email.subject',  'tr', '[iTrack] Truck already reloaded - {0}');
execute upsert_tx('unloads.for.target.reloaded.email.text',  'tr', 'No unload information was received until the next loading of the truck. Order had been set to delivered in Logon Truck plate number: {0}, Order Number: {1}');
execute upsert_tx('unloads.for.target.short.unload.email.subject', 'tr', 'Short unloading time failure');
execute upsert_tx('unloads.for.target.short.unload.email.text', 'tr', 'Unloading time was too short, please check the information about the unload in I-Data! Truck plate number: {0}, Order Number: {1}');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

