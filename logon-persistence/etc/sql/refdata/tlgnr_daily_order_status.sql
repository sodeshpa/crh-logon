-- $Revision$
-- $Date$
--
-- This is a table without any logic in the application, and can be extended freely by the application.
-- As a consequence, the application must display the options in a dropdown, not as radiobuttons.

--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_daily_order_status.sql ----------'

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
-- tlgnr_daily_order_status_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_daily_order_status_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_daily_order_status_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_daily_order_status_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.daily_order_status_cd)
    into v_exists_flag
    from tlgnr_daily_order_status_bs bs
    where bs.daily_order_status_cd = p_daily_order_status_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_daily_order_status_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_daily_order_status_bs set
/* daily_order_status_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          daily_order_status_cd = p_daily_order_status_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_daily_order_status_cd || ').');
      end if;
    else
      insert into tlgnr_daily_order_status_bs (
        daily_order_status_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_daily_order_status_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_daily_order_status_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_daily_order_status_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_daily_order_status_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_daily_order_status_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_daily_order_status_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.daily_order_status_cd) into v_exists_bs_flag
    from tlgnr_daily_order_status_bs bs
    where bs.daily_order_status_cd = p_daily_order_status_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.daily_order_status_cd) into v_exists_flag
      from tlgnr_daily_order_status_tx tx
      where tx.daily_order_status_cd = p_daily_order_status_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_daily_order_status_tx set
/* p_daily_order_status_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          daily_order_status_cd = p_daily_order_status_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_daily_order_status_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_daily_order_status_tx (
          daily_order_status_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_daily_order_status_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_daily_order_status_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_daily_order_status_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_DAILY_ORDER_STATUS_BS
execute upsert_bs(1, 1);
execute upsert_bs(2, 1);
execute upsert_bs(3, 1);
execute upsert_bs(4, 1);

prompt INSERTING into TLGNR_DAILY_ORDER_STATUS_TX - EN
execute upsert_tx(1, 'en', 'Not contacted yet');
execute upsert_tx(2, 'en', 'Not reachable');
execute upsert_tx(3, 'en', 'No order for today');
execute upsert_tx(4, 'en', 'Already ordered');

prompt INSERTING into TLGNR_DAILY_ORDER_STATUS_TX - DE
execute upsert_tx(1, 'de', 'Noch nicht kontaktiert');
execute upsert_tx(2, 'de', 'Nicht erreicht');
execute upsert_tx(3, 'de', 'Heute keine Bestellung');
execute upsert_tx(4, 'de', 'Bereits bestellt');

prompt INSERTING into TLGNR_DAILY_ORDER_STATUS_TX - FR
execute upsert_tx(1, 'fr', 'Pas encore contacté');
execute upsert_tx(2, 'fr', 'Non joignable');
execute upsert_tx(3, 'fr', 'Aucune commande aujourd''hui');
execute upsert_tx(4, 'fr', 'Déjà commandé');

prompt INSERTING into TLGNR_DAILY_ORDER_STATUS_TX - IT
execute upsert_tx(1, 'it', 'Non ancora contattato');
execute upsert_tx(2, 'it', 'Non raggiungibile');
execute upsert_tx(3, 'it', 'Nessun ordine per oggi');
execute upsert_tx(4, 'it', 'Già ordinato');

prompt INSERTING into TLGNR_DAILY_ORDER_STATUS_TX - ES
execute upsert_tx(1, 'es', 'No contactado aún');
execute upsert_tx(2, 'es', 'No disponible');
execute upsert_tx(3, 'es', 'No hay pedidos hoy');
execute upsert_tx(4, 'es', 'Pedido ya hecho');

prompt INSERTING into TLGNR_DAILY_ORDER_STATUS_TX - CS
execute upsert_tx(1, 'cs', 'Dosud nekontaktován');
execute upsert_tx(2, 'cs', 'Není k dosažení');
execute upsert_tx(3, 'cs', 'Žádné objednávky dnes');
execute upsert_tx(4, 'cs', 'Již objednáno');

prompt INSERTING into TLGNR_DAILY_ORDER_STATUS_TX - SK
execute upsert_tx(1, 'sk', 'Ešte nekontaktovaný');
execute upsert_tx(2, 'sk', 'Nezastihnuteľný');
execute upsert_tx(3, 'sk', 'Dnes bez objednávky');
execute upsert_tx(4, 'sk', 'Objednané');

prompt INSERTING into TLGNR_DAILY_ORDER_STATUS_TX - HU
execute upsert_tx(1, 'hu', 'Még nincs kapcsolat');
execute upsert_tx(2, 'hu', 'Nem elérhető');
execute upsert_tx(3, 'hu', 'Nincs mai rendelés');
execute upsert_tx(4, 'hu', 'Már megrendelt');

prompt INSERTING into TLGNR_DAILY_ORDER_STATUS_TX - RO
execute upsert_tx(1, 'ro', 'Nu este contactat inca');
execute upsert_tx(2, 'ro', 'Nu poate fi contactat');
execute upsert_tx(3, 'ro', 'Nicio comanda astazi');
execute upsert_tx(4, 'ro', 'Deja comandat');

prompt INSERTING into TLGNR_DAILY_ORDER_STATUS_TX - BG
execute upsert_tx(1, 'bg', 'Още не е свързан');
execute upsert_tx(2, 'bg', 'Извън обсег');
execute upsert_tx(3, 'bg', 'Няма поръчка за днес');
execute upsert_tx(4, 'bg', 'Вече поръчан');

prompt INSERTING into TLGNR_DAILY_ORDER_STATUS_TX - RU (ENGLISH COPY)
execute upsert_tx(1, 'ru', 'пока не связался');
execute upsert_tx(2, 'ru', 'не доступен');
execute upsert_tx(3, 'ru', 'сегодня нет заказа');
execute upsert_tx(4, 'ru', 'уже заказан');

prompt INSERTING into TLGNR_DAILY_ORDER_STATUS_TX - PL (ENGLISH COPY)
execute upsert_tx(1, 'pl', 'Jeszcze nie skontaktow. się');
execute upsert_tx(2, 'pl', 'Nieosiągalny');
execute upsert_tx(3, 'pl', 'Nie ma na dzisiaj zamówień');
execute upsert_tx(4, 'pl', 'Już zamówiono');

prompt INSERTING into TLGNR_DAILY_ORDER_STATUS_TX - AZ (ENGLISH COPY)
execute upsert_tx(1, 'az', 'Not contacted yet');
execute upsert_tx(2, 'az', 'Not reachable');
execute upsert_tx(3, 'az', 'No order for today');
execute upsert_tx(4, 'az', 'Already ordered');

prompt INSERTING into TLGNR_DAILY_ORDER_STATUS_TX - HR (ENGLISH COPY)
execute upsert_tx(1, 'hr', 'Nije kontaktiran još');
execute upsert_tx(2, 'hr', 'Nije dostupan');
execute upsert_tx(3, 'hr', 'Nema narudžbi za danas');
execute upsert_tx(4, 'hr', 'Već naručen');

prompt INSERTING into TLGNR_DAILY_ORDER_STATUS_TX - SR (ENGLISH COPY)
execute upsert_tx(1, 'sr', 'Nije kontaktiran još');
execute upsert_tx(2, 'sr', 'Nedostupan');
execute upsert_tx(3, 'sr', 'Nema naloga za danas');
execute upsert_tx(4, 'sr', 'Već naručeno');

prompt INSERTING into TLGNR_DAILY_ORDER_STATUS_TX - TR
execute upsert_tx(1, 'tr', 'Not contacted yet');
execute upsert_tx(2, 'tr', 'Not reachable');
execute upsert_tx(3, 'tr', 'No order for today');
execute upsert_tx(4, 'tr', 'Already ordered');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

