-- $Revision$
-- $Date$
--
-- This is an internal, technical table:
-- - No translations needed
-- - Extending the possible values requires code changes

--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_assign_status.sql ----------' 

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
-- tlgnr_assign_status_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_assign_status_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_assign_status_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_assign_status_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.assign_status_cd)
    into v_exists_flag
    from tlgnr_assign_status_bs bs
    where bs.assign_status_cd = p_assign_status_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_assign_status_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_assign_status_bs set
          /* assign_status_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          assign_status_cd = p_assign_status_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_assign_status_cd || ').');
      end if;
    else
      insert into tlgnr_assign_status_bs (
        assign_status_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_assign_status_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_assign_status_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_assign_status_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_assign_status_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_assign_status_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_assign_status_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

    ----------------------------------------------------------------------------
    -- Lookup BS entry first
    ----------------------------------------------------------------------------
    select count(bs.assign_status_cd) into v_exists_bs_flag
    from tlgnr_assign_status_bs bs
    where bs.assign_status_cd = p_assign_status_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.assign_status_cd) into v_exists_flag
      from tlgnr_assign_status_tx tx
      where tx.assign_status_cd = p_assign_status_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_assign_status_tx set
          /* p_assign_status_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          assign_status_cd = p_assign_status_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_assign_status_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_assign_status_tx (
          assign_status_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_assign_status_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_assign_status_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_assign_status_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_ASSIGN_STATUS_BS
execute upsert_bs(0, 1);
execute upsert_bs(1, 1);
execute upsert_bs(2, 1);
execute upsert_bs(3, 1);
execute upsert_bs(4, 1);
execute upsert_bs(5, 1);
execute upsert_bs(6, 1);
execute upsert_bs(7, 1);
execute upsert_bs(8, 1);

prompt INSERTING into TLGNR_ASSIGN_STATUS_TX - EN
execute upsert_tx(0, 'en', 'New');
execute upsert_tx(1, 'en', 'Pre-load');
execute upsert_tx(2, 'en', 'Loading');
execute upsert_tx(3, 'en', 'Shipping');
execute upsert_tx(4, 'en', 'Unloading');
execute upsert_tx(5, 'en', 'Returning');
execute upsert_tx(6, 'en', 'Finished');
execute upsert_tx(7, 'en', 'Cancelled');
execute upsert_tx(8, 'en', 'Prescheduling done');

prompt INSERTING into TLGNR_ASSIGN_STATUS_TX - DE
execute upsert_tx(0, 'de', 'Neu');
execute upsert_tx(1, 'de', 'Vorladung');
execute upsert_tx(2, 'de', 'Beladung');
execute upsert_tx(3, 'de', 'Unterwegs');
execute upsert_tx(4, 'de', 'Entladung');
execute upsert_tx(5, 'de', 'Rückfahrt');
execute upsert_tx(6, 'de', 'Abgeschlossen');
execute upsert_tx(7, 'de', 'Storniert');
execute upsert_tx(8, 'de', 'Vordisposition erledigt');

prompt INSERTING into TLGNR_ASSIGN_STATUS_TX - FR
execute upsert_tx(0, 'fr', 'Nouveau');
execute upsert_tx(1, 'fr', 'Préchargement');
execute upsert_tx(2, 'fr', 'Chargement');
execute upsert_tx(3, 'fr', 'En route');
execute upsert_tx(4, 'fr', 'Déchargement');
execute upsert_tx(5, 'fr', 'En retour');
execute upsert_tx(6, 'fr', 'Terminé');
execute upsert_tx(7, 'fr', 'Annulé');
execute upsert_tx(8, 'fr', 'Pré-planification faite');

prompt INSERTING into TLGNR_ASSIGN_STATUS_TX - IT
execute upsert_tx(0, 'it', 'Nuovo');
execute upsert_tx(1, 'it', 'Precarico');
execute upsert_tx(2, 'it', 'Caricando');
execute upsert_tx(3, 'it', 'Shipping');
execute upsert_tx(4, 'it', 'Scaricando');
execute upsert_tx(5, 'it', 'Ritornando');
execute upsert_tx(6, 'it', 'Finito');
execute upsert_tx(7, 'it', 'Cancellato');
execute upsert_tx(8, 'it', 'Prescheduling fatto');

prompt INSERTING into TLGNR_ASSIGN_STATUS_TX - ES
execute upsert_tx(0, 'es', 'Nuevo');
execute upsert_tx(1, 'es', 'Precargando');
execute upsert_tx(2, 'es', 'Cargando');
execute upsert_tx(3, 'es', 'Expidiendo');
execute upsert_tx(4, 'es', 'Descargando');
execute upsert_tx(5, 'es', 'Volviendo');
execute upsert_tx(6, 'es', 'Finalizado');
execute upsert_tx(7, 'es', 'Cancelado');
execute upsert_tx(8, 'es', 'Pre-planificación completada');

prompt INSERTING into TLGNR_ASSIGN_STATUS_TX - CS
execute upsert_tx(0, 'cs', 'Nový');
execute upsert_tx(1, 'cs', 'Přednakládka');
execute upsert_tx(2, 'cs', 'Nakládka');
execute upsert_tx(3, 'cs', 'Doprava');
execute upsert_tx(4, 'cs', 'Vykládka');
execute upsert_tx(5, 'cs', 'Vrácení');
execute upsert_tx(6, 'cs', 'Ukončeno');
execute upsert_tx(7, 'cs', 'Přerušeno');
execute upsert_tx(8, 'cs', 'Čas změněn');

prompt INSERTING into TLGNR_ASSIGN_STATUS_TX - SK
execute upsert_tx(0, 'sk', 'Nový');
execute upsert_tx(1, 'sk', 'Pred nakládka');
execute upsert_tx(2, 'sk', 'Nakládka');
execute upsert_tx(3, 'sk', 'Preprava');
execute upsert_tx(4, 'sk', 'Vykládka');
execute upsert_tx(5, 'sk', 'Návrat');
execute upsert_tx(6, 'sk', 'Ukončenie');
execute upsert_tx(7, 'sk', 'Zrušené');
execute upsert_tx(8, 'sk', 'Plánovanie hotové');

prompt INSERTING into TLGNR_ASSIGN_STATUS_TX - HU
execute upsert_tx(0, 'hu', 'Új');
execute upsert_tx(1, 'hu', 'Előrakodás');
execute upsert_tx(2, 'hu', 'Rakodás');
execute upsert_tx(3, 'hu', 'Szállítás');
execute upsert_tx(4, 'hu', 'Kirakodás');
execute upsert_tx(5, 'hu', 'Visszatérés');
execute upsert_tx(6, 'hu', 'Befejezett');
execute upsert_tx(7, 'hu', 'Törölt');
execute upsert_tx(8, 'hu', 'Előütemezés vége');

prompt INSERTING into TLGNR_ASSIGN_STATUS_TX - RO
execute upsert_tx(0, 'ro', 'Nou');
execute upsert_tx(1, 'ro', 'Pre-incarcare');
execute upsert_tx(2, 'ro', 'Incarcare');
execute upsert_tx(3, 'ro', 'Expeditie');
execute upsert_tx(4, 'ro', 'Descarcare');
execute upsert_tx(5, 'ro', 'Returnare');
execute upsert_tx(6, 'ro', 'Terminat');
execute upsert_tx(7, 'ro', 'Anulat');
execute upsert_tx(8, 'ro', 'Pre-programare realizata');

prompt INSERTING into TLGNR_ASSIGN_STATUS_TX - BG
execute upsert_tx(0, 'bg', 'Нов');
execute upsert_tx(1, 'bg', 'Презареждане');
execute upsert_tx(2, 'bg', 'Товарене');
execute upsert_tx(3, 'bg', 'Доставяне');
execute upsert_tx(4, 'bg', 'Разтоварване');
execute upsert_tx(5, 'bg', 'Връщане');
execute upsert_tx(6, 'bg', 'Приключен');
execute upsert_tx(7, 'bg', 'Прекратен');
execute upsert_tx(8, 'bg', 'Предварителен график завършен');

prompt INSERTING into TLGNR_ASSIGN_STATUS_TX - RU (ENGLISH COPY)
execute upsert_tx(0, 'ru', 'новый');
execute upsert_tx(1, 'ru', 'погрузка в счет завтра');
execute upsert_tx(2, 'ru', 'погрузка');
execute upsert_tx(3, 'ru', 'доставка');
execute upsert_tx(4, 'ru', 'выгрузка');
execute upsert_tx(5, 'ru', 'возврат');
execute upsert_tx(6, 'ru', 'окончание');
execute upsert_tx(7, 'ru', 'отмена');
execute upsert_tx(8, 'ru', 'запланировано');

prompt INSERTING into TLGNR_ASSIGN_STATUS_TX - PL (ENGLISH COPY)
execute upsert_tx(0, 'pl', 'Nowy');
execute upsert_tx(1, 'pl', 'Ładowanie wstępne');
execute upsert_tx(2, 'pl', 'Załadunek');
execute upsert_tx(3, 'pl', 'Spedycja');
execute upsert_tx(4, 'pl', 'Rozładunek');
execute upsert_tx(5, 'pl', 'Zwrot');
execute upsert_tx(6, 'pl', 'Zakończony');
execute upsert_tx(7, 'pl', 'Anulowany');
execute upsert_tx(8, 'pl', 'Planowanie wstępne zrobione');

prompt INSERTING into TLGNR_ASSIGN_STATUS_TX - AZ (ENGLISH COPY)
execute upsert_tx(0, 'az', 'New');
execute upsert_tx(1, 'az', 'Pre-load');
execute upsert_tx(2, 'az', 'Loading');
execute upsert_tx(3, 'az', 'Shipping');
execute upsert_tx(4, 'az', 'Unloading');
execute upsert_tx(5, 'az', 'Returning');
execute upsert_tx(6, 'az', 'Finished');
execute upsert_tx(7, 'az', 'Cancelled');
execute upsert_tx(8, 'az', 'Prescheduling done');

prompt INSERTING into TLGNR_ASSIGN_STATUS_TX - HR (ENGLISH COPY)
execute upsert_tx(0, 'hr', 'Novi');
execute upsert_tx(1, 'hr', 'Prije utovara');
execute upsert_tx(2, 'hr', 'Utovar');
execute upsert_tx(3, 'hr', 'Isporuka');
execute upsert_tx(4, 'hr', 'Istovar');
execute upsert_tx(5, 'hr', 'Vraćanje');
execute upsert_tx(6, 'hr', 'Gotov');
execute upsert_tx(7, 'hr', 'Otkazan');
execute upsert_tx(8, 'hr', 'Preusmjeravanje učinjeno');

prompt INSERTING into TLGNR_ASSIGN_STATUS_TX - SR (ENGLISH COPY)
execute upsert_tx(0, 'sr', 'Novi');
execute upsert_tx(1, 'sr', 'Pre utovara');
execute upsert_tx(2, 'sr', 'Utovar');
execute upsert_tx(3, 'sr', 'Isporuka');
execute upsert_tx(4, 'sr', 'Istovar');
execute upsert_tx(5, 'sr', 'Povraćaj');
execute upsert_tx(6, 'sr', 'Realizovano');
execute upsert_tx(7, 'sr', 'Otkazano');
execute upsert_tx(8, 'sr', 'Preraspoređivanje obavljeno');

prompt INSERTING into TLGNR_ASSIGN_STATUS_TX - TR
execute upsert_tx(0, 'tr', 'New');
execute upsert_tx(1, 'tr', 'Pre-load');
execute upsert_tx(2, 'tr', 'Loading');
execute upsert_tx(3, 'tr', 'Shipping');
execute upsert_tx(4, 'tr', 'Unloading');
execute upsert_tx(5, 'tr', 'Returning');
execute upsert_tx(6, 'tr', 'Finished');
execute upsert_tx(7, 'tr', 'Cancelled');
execute upsert_tx(8, 'tr', 'Prescheduling done');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

