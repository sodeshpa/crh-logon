-- $Revision$
-- $Date$
--
-- Logon internal refdata. Adding new values requires code changes, see tlgnc_order_item.return_quant_handling_cd.

--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_return_quant_handling.sql ----------'

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
-- tlgnr_return_quant_handling_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_return_quant_handling_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_return_quant_handling_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_return_quant_handling_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.return_quant_handling_cd)
    into v_exists_flag
    from tlgnr_return_quant_handling_bs bs
    where bs.return_quant_handling_cd = p_return_quant_handling_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_return_quant_handling_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_return_quant_handling_bs set
/* return_quant_handling_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          return_quant_handling_cd = p_return_quant_handling_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_return_quant_handling_cd || ').');
      end if;
    else
      insert into tlgnr_return_quant_handling_bs (
        return_quant_handling_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_return_quant_handling_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_return_quant_handling_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_return_quant_handling_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_return_quant_handling_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_return_quant_handling_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_return_quant_handling_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.return_quant_handling_cd) into v_exists_bs_flag
    from tlgnr_return_quant_handling_bs bs
    where bs.return_quant_handling_cd = p_return_quant_handling_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.return_quant_handling_cd) into v_exists_flag
      from tlgnr_return_quant_handling_tx tx
      where tx.return_quant_handling_cd = p_return_quant_handling_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_return_quant_handling_tx set
/* p_return_quant_handling_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          return_quant_handling_cd = p_return_quant_handling_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_return_quant_handling_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_return_quant_handling_tx (
          return_quant_handling_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_return_quant_handling_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_return_quant_handling_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_return_quant_handling_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_RETURN_QUANT_HANDLING_BS
execute upsert_bs(0, 1);
execute upsert_bs(1, 1);
execute upsert_bs(2, 1);
execute upsert_bs(3, 1);
execute upsert_bs(4, 1);

prompt INSERTING into TLGNR_RETURN_QUANT_HANDLING_TX - EN
execute upsert_tx(0, 'en', 'No adjustment needed (or credit note refused)');
execute upsert_tx(1, 'en', 'Delivered quantity adjustment needed');
execute upsert_tx(2, 'en', 'Delivered quantity adjusted');
execute upsert_tx(3, 'en', 'Credit note needed');
execute upsert_tx(4, 'en', 'Credit note created');

prompt INSERTING into TLGNR_RETURN_QUANT_HANDLING_TX - DE
execute upsert_tx(0, 'de', 'Keine Anpassung notwendig (oder ignoriert)');
execute upsert_tx(1, 'de', 'Liefermenge Anpassung notwendig');
execute upsert_tx(2, 'de', 'Liefermenge angepasst');
execute upsert_tx(3, 'de', 'Gutschrift notwendig');
execute upsert_tx(4, 'de', 'Gutschrift erstellt');

prompt INSERTING into TLGNR_RETURN_QUANT_HANDLING_TX - FR
execute upsert_tx(0, 'fr', 'Aucun ajustement nécessaire (ou crédit non refusé)');
execute upsert_tx(1, 'fr', 'Ajustement de quantité livrée nécessaire');
execute upsert_tx(2, 'fr', 'Quantité livrée ajustée');
execute upsert_tx(3, 'fr', 'Note de crédit nécessaire');
execute upsert_tx(4, 'fr', 'Bulletin de livraison créé');

prompt INSERTING into TLGNR_RETURN_QUANT_HANDLING_TX - IT
execute upsert_tx(0, 'it', 'Alcun aggiustamento necessario (o nota di credito fifiutata)');
execute upsert_tx(1, 'it', 'Regolazione della quantità consegnata necessarie');
execute upsert_tx(2, 'it', 'Quantità consegnata regolata');
execute upsert_tx(3, 'it', 'Nota credito necessarie');
execute upsert_tx(4, 'it', 'Nota credito creata');

prompt INSERTING into TLGNR_RETURN_QUANT_HANDLING_TX - ES
execute upsert_tx(0, 'es', 'Ningún ajuste necesario (o nota de crédito rechazada)');
execute upsert_tx(1, 'es', 'Ajuste cantidad entregada necesario');
execute upsert_tx(2, 'es', 'Cantidad entregada ajustada');
execute upsert_tx(3, 'es', 'Nota de crédito necesaria');
execute upsert_tx(4, 'es', 'Nota de crédito creada');

prompt INSERTING into TLGNR_RETURN_QUANT_HANDLING_TX - CS
execute upsert_tx(0, 'cs', 'Žádné nastavení není třeba (nebo dobropis odmítnut)');
execute upsert_tx(1, 'cs', 'Nastavení dodaného množství je potřebné');
execute upsert_tx(2, 'cs', 'Dodaného množství nastaveno');
execute upsert_tx(3, 'cs', 'Dobropis je potřebný');
execute upsert_tx(4, 'cs', 'Dobropis založen');

prompt INSERTING into TLGNR_RETURN_QUANT_HANDLING_TX - SK
execute upsert_tx(0, 'sk', 'Opravy nepotrebné (alebo odmietnutý dobropis)');
execute upsert_tx(1, 'sk', 'Potrebné upraviť dodané množstvá');
execute upsert_tx(2, 'sk', 'Dodané množstvá upravené');
execute upsert_tx(3, 'sk', 'Dobropis potrebný');
execute upsert_tx(4, 'sk', 'Dobropis vytvorený');

prompt INSERTING into TLGNR_RETURN_QUANT_HANDLING_TX - HU
execute upsert_tx(0, 'hu', 'Nem szükséges beállítás (vagy jóváírás visszautasítása)');
execute upsert_tx(1, 'hu', 'Kiszállított mennyiség beállítása szükséges');
execute upsert_tx(2, 'hu', 'Kiszállított mennyiség korrigálása');
execute upsert_tx(3, 'hu', 'Jóváírás szükséges');
execute upsert_tx(4, 'hu', 'Jóváírás létrehozása');

prompt INSERTING into TLGNR_RETURN_QUANT_HANDLING_TX - RO
execute upsert_tx(0, 'ro', 'Nu este necesara nicio ajustare (sau nota credit refuzata)');
execute upsert_tx(1, 'ro', 'Modificare cantitate livrata este necesara');
execute upsert_tx(2, 'ro', 'Cantitate livrata modificata');
execute upsert_tx(3, 'ro', 'Nota credit necesara');
execute upsert_tx(4, 'ro', 'Nota credit creata');

prompt INSERTING into TLGNR_RETURN_QUANT_HANDLING_TX - BG
execute upsert_tx(0, 'bg', 'Не се налага корекция ( или кредитното известие е отказано)');
execute upsert_tx(1, 'bg', 'Налага се корекция на доставеното количество');
execute upsert_tx(2, 'bg', 'Доставеното количество е коригирано');
execute upsert_tx(3, 'bg', 'Необходимо е кредитно известие');
execute upsert_tx(4, 'bg', 'Създадено е кредитно известие');

prompt INSERTING into TLGNR_RETURN_QUANT_HANDLING_TX - RU (ENGLISH COPY)
execute upsert_tx(0, 'ru', 'Корректировка не требуется (или в credit note отказано)');
execute upsert_tx(1, 'ru', 'необходима корректировка доставленного количества');
execute upsert_tx(2, 'ru', 'доставленное количество откорректировано');
execute upsert_tx(3, 'ru', 'необходима корректировка финансовых документов');
execute upsert_tx(4, 'ru', 'созданы корректирующие финансовые документы');

prompt INSERTING into TLGNR_RETURN_QUANT_HANDLING_TX - PL
execute upsert_tx(0, 'pl', 'Regulacje nie wymagane (lub nota kredytowa odrzucona)');
execute upsert_tx(1, 'pl', 'Wymagane ustalenie dostarczonej ilości');
execute upsert_tx(2, 'pl', 'Dostarczona ilość skorygowana');
execute upsert_tx(3, 'pl', 'Potrzeba nota kredytowa');
execute upsert_tx(4, 'pl', 'Nota kredytowa utworzona');

prompt INSERTING into TLGNR_RETURN_QUANT_HANDLING_TX - AZ (ENGLISH COPY)
execute upsert_tx(0, 'az', 'Nizamlamaya ehtiyac yoxdur (və ya kredit notunan imtina)');

execute upsert_tx(1, 'az', 'Çatdırılmış miqdarın nizamlanmasına ehtiyac var');
execute upsert_tx(2, 'az', 'Çatdırılmış miqdar nizamlandı');
execute upsert_tx(3, 'az', 'Kredit avizosu tələb olunur');
execute upsert_tx(4, 'az', 'Kredit avizosu yaradılıb');

prompt INSERTING into TLGNR_RETURN_QUANT_HANDLING_TX - HR
execute upsert_tx(0, 'hr', 'Nije potrebna prilagodba (ili kreditna bilješka odbijena)');
execute upsert_tx(1, 'hr', 'Potrebno prilagoditi isporučenu količinu');
execute upsert_tx(2, 'hr', 'Isporučena količina prilagođena');
execute upsert_tx(3, 'hr', 'Potrebne kreditne bilješke');
execute upsert_tx(4, 'hr', 'Kreditne bilješke stvorene');

prompt INSERTING into TLGNR_RETURN_QUANT_HANDLING_TX - SR (ENGLISH COPY)
execute upsert_tx(0, 'sr', 'No adjustment needed (or credit note refused)');
execute upsert_tx(1, 'sr', 'Potrebno prilagoditi isporučenu količinu');
execute upsert_tx(2, 'sr', 'Isporučena količina prilagođena');
execute upsert_tx(3, 'sr', 'Potreban Credit note');
execute upsert_tx(4, 'sr', 'Kreiran Credit note');

prompt INSERTING into TLGNR_RETURN_QUANT_HANDLING_TX - TR
execute upsert_tx(0, 'tr', 'No adjustment needed (or credit note refused)');
execute upsert_tx(1, 'tr', 'Delivered quantity adjustment needed');
execute upsert_tx(2, 'tr', 'Delivered quantity adjusted');
execute upsert_tx(3, 'tr', 'Credit note needed');
execute upsert_tx(4, 'tr', 'Credit note created');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

