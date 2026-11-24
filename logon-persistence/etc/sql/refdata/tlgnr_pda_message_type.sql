-- $Revision$
-- $Date$
--
--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_pda_message_type.sql ----------'

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
-- tlgnr_pda_message_type_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_pda_message_type_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_pda_message_type_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_pda_message_type_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.pda_message_type_cd)
    into v_exists_flag
    from tlgnr_pda_message_type_bs bs
    where bs.pda_message_type_cd = p_pda_message_type_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_pda_message_type_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_pda_message_type_bs set
/* pda_message_type_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          pda_message_type_cd = p_pda_message_type_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_pda_message_type_cd || ').');
      end if;
    else
      insert into tlgnr_pda_message_type_bs (
        pda_message_type_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_pda_message_type_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_pda_message_type_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_pda_message_type_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_pda_message_type_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_pda_message_type_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_pda_message_type_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.pda_message_type_cd) into v_exists_bs_flag
    from tlgnr_pda_message_type_bs bs
    where bs.pda_message_type_cd = p_pda_message_type_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.pda_message_type_cd) into v_exists_flag
      from tlgnr_pda_message_type_tx tx
      where tx.pda_message_type_cd = p_pda_message_type_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_pda_message_type_tx set
/* p_pda_message_type_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          pda_message_type_cd = p_pda_message_type_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_pda_message_type_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_pda_message_type_tx (
          pda_message_type_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_pda_message_type_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_pda_message_type_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_pda_message_type_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_PDA_MESSAGE_TYPE_BS
execute upsert_bs(1, 1);
execute upsert_bs(2, 1);
execute upsert_bs(3, 1);
execute upsert_bs(4, 1);

prompt INSERTING into TLGNR_PDA_MESSAGE_TYPE_TX - EN
execute upsert_tx(1, 'en', 'Message to Driver');
execute upsert_tx(2, 'en', 'Message to Dispatcher');
execute upsert_tx(3, 'en', 'General Error');
execute upsert_tx(4, 'en', 'Market Info');

prompt INSERTING into TLGNR_PDA_MESSAGE_TYPE_TX - DE
execute upsert_tx(1, 'de', 'Textmeldung an Fahrer');
execute upsert_tx(2, 'de', 'Textmeldung an Disponent');
execute upsert_tx(3, 'de', 'Allgemeiner Fehler');
execute upsert_tx(4, 'de', 'Marktmeldung');

prompt INSERTING into TLGNR_PDA_MESSAGE_TYPE_TX - FR
execute upsert_tx(1, 'fr', 'Message à Chauffeur');
execute upsert_tx(2, 'fr', 'Message à Disponent');
execute upsert_tx(3, 'fr', 'Erreur Générale');
execute upsert_tx(4, 'fr', 'Info Marché');

prompt INSERTING into TLGNR_PDA_MESSAGE_TYPE_TX - IT
execute upsert_tx(1, 'it', 'Messaggio all''Austista');
execute upsert_tx(2, 'it', 'Messaggio a Disponente');
execute upsert_tx(3, 'it', 'Errore Generale');
execute upsert_tx(4, 'it', 'Info Mercato');

prompt INSERTING into TLGNR_PDA_MESSAGE_TYPE_TX - ES
execute upsert_tx(1, 'es', 'Mensaje para Conductor');
execute upsert_tx(2, 'es', 'Mensaje para Expedidor');
execute upsert_tx(3, 'es', 'Error General');
execute upsert_tx(4, 'es', 'Info Mercado');

prompt INSERTING into TLGNR_PDA_MESSAGE_TYPE_TX - CS
execute upsert_tx(1, 'cs', 'Zpráva řidiči');
execute upsert_tx(2, 'cs', 'Zpráva dispečeru');
execute upsert_tx(3, 'cs', 'Obecná chyba');
execute upsert_tx(4, 'cs', 'Informace o trhu');

prompt INSERTING into TLGNR_PDA_MESSAGE_TYPE_TX - SK
execute upsert_tx(1, 'sk', 'Správa pre vodiča');
execute upsert_tx(2, 'sk', 'Správa pre dispečera');
execute upsert_tx(3, 'sk', 'Chyba');
execute upsert_tx(4, 'sk', 'Trhová informácia');

prompt INSERTING into TLGNR_PDA_MESSAGE_TYPE_TX - HU
execute upsert_tx(1, 'hu', 'Üzenet a sofőrnek');
execute upsert_tx(2, 'hu', 'Üzenet a diszpécsernek');
execute upsert_tx(3, 'hu', 'Általános hiba');
execute upsert_tx(4, 'hu', 'Piaci információk');

prompt INSERTING into TLGNR_PDA_MESSAGE_TYPE_TX - RO
execute upsert_tx(1, 'ro', 'Mesaj catre Sofer');
execute upsert_tx(2, 'ro', 'Mesaj catre Dispecer');
execute upsert_tx(3, 'ro', 'Eroare generala');
execute upsert_tx(4, 'ro', 'Informatii Piata');

prompt INSERTING into TLGNR_PDA_MESSAGE_TYPE_TX - BG
execute upsert_tx(1, 'bg', 'Съобщение до шофьора');
execute upsert_tx(2, 'bg', 'Съобщение до диспечера');
execute upsert_tx(3, 'bg', 'Основна грешка');
execute upsert_tx(4, 'bg', 'Информация за пазара');

prompt INSERTING into TLGNR_PDA_MESSAGE_TYPE_TX - RU (ENGLISH COPY)
execute upsert_tx(1, 'ru', 'сообщение водителю');
execute upsert_tx(2, 'ru', 'сообщение диспетчеру');
execute upsert_tx(3, 'ru', 'общая ошибка');
execute upsert_tx(4, 'ru', 'информация о рынке');

prompt INSERTING into TLGNR_PDA_MESSAGE_TYPE_TX - PL
execute upsert_tx(1, 'pl', 'Wiadomość dla kierowcy');
execute upsert_tx(2, 'pl', 'Wiadomość dla dyspozytora');
execute upsert_tx(3, 'pl', 'Błąd główny');
execute upsert_tx(4, 'pl', 'Informacje rynkowe');

prompt INSERTING into TLGNR_PDA_MESSAGE_TYPE_TX - AZ (ENGLISH COPY)
execute upsert_tx(1, 'az', 'Message to Driver');
execute upsert_tx(2, 'az', 'Message to Dispatcher');
execute upsert_tx(3, 'az', 'General Error');
execute upsert_tx(4, 'az', 'Market Info');

prompt INSERTING into TLGNR_PDA_MESSAGE_TYPE_TX - HR
execute upsert_tx(1, 'hr', 'Poruka vozaču');
execute upsert_tx(2, 'hr', 'Poruka dispečeru');
execute upsert_tx(3, 'hr', 'Opća greška');
execute upsert_tx(4, 'hr', 'Tržnišni info');

prompt INSERTING into TLGNR_PDA_MESSAGE_TYPE_TX - SR
execute upsert_tx(1, 'sr', 'Poruka vozaču');
execute upsert_tx(2, 'sr', 'Poruka dispečeru');
execute upsert_tx(3, 'sr', 'Generalna greška');
execute upsert_tx(4, 'sr', 'Info sa tržišta');

prompt INSERTING into TLGNR_PDA_MESSAGE_TYPE_TX - TR
execute upsert_tx(1, 'tr', 'Message to Driver');
execute upsert_tx(2, 'tr', 'Message to Dispatcher');
execute upsert_tx(3, 'tr', 'General Error');
execute upsert_tx(4, 'tr', 'Market Info');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

