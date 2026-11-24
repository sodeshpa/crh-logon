-- $Revision$
-- $Date$
--
--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_wagon_inactive_reason.sql ----------'

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
-- tlgnr_wagon_inactive_reason_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_wagon_inactive_reason_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_wagon_inactive_reason_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_wagon_inactive_reason_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.wagon_inactive_reason_cd)
    into v_exists_flag
    from tlgnr_wagon_inactive_reason_bs bs
    where bs.wagon_inactive_reason_cd = p_wagon_inactive_reason_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_wagon_inactive_reason_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_wagon_inactive_reason_bs set
/* wagon_inactive_reason_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          wagon_inactive_reason_cd = p_wagon_inactive_reason_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_wagon_inactive_reason_cd || ').');
      end if;
    else
      insert into tlgnr_wagon_inactive_reason_bs (
        wagon_inactive_reason_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_wagon_inactive_reason_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_wagon_inactive_reason_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_wagon_inactive_reason_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_wagon_inactive_reason_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_wagon_inactive_reason_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_wagon_inactive_reason_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.wagon_inactive_reason_cd) into v_exists_bs_flag
    from tlgnr_wagon_inactive_reason_bs bs
    where bs.wagon_inactive_reason_cd = p_wagon_inactive_reason_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.wagon_inactive_reason_cd) into v_exists_flag
      from tlgnr_wagon_inactive_reason_tx tx
      where tx.wagon_inactive_reason_cd = p_wagon_inactive_reason_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_wagon_inactive_reason_tx set
/* p_wagon_inactive_reason_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          wagon_inactive_reason_cd = p_wagon_inactive_reason_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_wagon_inactive_reason_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_wagon_inactive_reason_tx (
          wagon_inactive_reason_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_wagon_inactive_reason_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_wagon_inactive_reason_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_wagon_inactive_reason_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_WAGON_INACTIVE_REASON_BS
execute upsert_bs( 1, 1);
execute upsert_bs( 2, 1);
execute upsert_bs( 3, 1);
execute upsert_bs( 4, 1);

prompt INSERTING into TLGNR_WAGON_INACTIVE_REASON_TX - EN
execute upsert_tx( 1, 'en', 'Not available');
execute upsert_tx( 2, 'en', 'Rented out');
execute upsert_tx( 3, 'en', 'Scrapped');
execute upsert_tx( 4, 'en', 'Invalid wagon number');

prompt INSERTING into TLGNR_WAGON_INACTIVE_REASON_TX - DE
execute upsert_tx( 1, 'de', 'Nicht verfügbar');
execute upsert_tx( 2, 'de', 'Verschrottet / verkauft');
execute upsert_tx( 3, 'de', 'Vermietet');
execute upsert_tx( 4, 'de', 'Fehlerfassung');

prompt INSERTING into TLGNR_WAGON_INACTIVE_REASON_TX - FR
execute upsert_tx( 1, 'fr', 'N''est pas disponible');
execute upsert_tx( 2, 'fr', 'Loué');
execute upsert_tx( 3, 'fr', 'Mis au rebut');
execute upsert_tx( 4, 'fr', 'Numéro de wagon non valide');

prompt INSERTING into TLGNR_WAGON_INACTIVE_REASON_TX - IT
execute upsert_tx( 1, 'it', 'Non disponibile');
execute upsert_tx( 2, 'it', 'Affittato');
execute upsert_tx( 3, 'it', 'Rottamati');
execute upsert_tx( 4, 'it', 'Numero di vagone non valido');

prompt INSERTING into TLGNR_WAGON_INACTIVE_REASON_TX - ES
execute upsert_tx( 1, 'es', 'No disponible');
execute upsert_tx( 2, 'es', 'Alquilado');
execute upsert_tx( 3, 'es', 'Desechado');
execute upsert_tx( 4, 'es', 'Número de vagón no válido');

prompt INSERTING into TLGNR_WAGON_INACTIVE_REASON_TX - CS
execute upsert_tx( 1, 'cs', 'Nedostupný');
execute upsert_tx( 2, 'cs', 'Pronajato ze společnosti');
execute upsert_tx( 3, 'cs', 'Sešrotováno');
execute upsert_tx( 4, 'cs', 'Chybné číslo vagónu');

prompt INSERTING into TLGNR_WAGON_INACTIVE_REASON_TX - SK
execute upsert_tx( 1, 'sk', 'Nie je k dispozícii');
execute upsert_tx( 2, 'sk', 'Prenajaté');
execute upsert_tx( 3, 'sk', 'Vyradené');
execute upsert_tx( 4, 'sk', 'Neplatné číslo vagóna');

prompt INSERTING into TLGNR_WAGON_INACTIVE_REASON_TX - HU
execute upsert_tx( 1, 'hu', 'Nem áll rendelkezésre');
execute upsert_tx( 2, 'hu', 'Bérbe adott');
execute upsert_tx( 3, 'hu', 'Leselejtezett');
execute upsert_tx( 4, 'hu', 'Érvénytelen vagon szám');

prompt INSERTING into TLGNR_WAGON_INACTIVE_REASON_TX - RO
execute upsert_tx( 1, 'ro', 'Indisponibil');
execute upsert_tx( 2, 'ro', 'Inchiriat');
execute upsert_tx( 3, 'ro', 'Casat');
execute upsert_tx( 4, 'ro', 'Numar vagon invalid');

prompt INSERTING into TLGNR_WAGON_INACTIVE_REASON_TX - BG
execute upsert_tx( 1, 'bg', 'Не е наличен');
execute upsert_tx( 2, 'bg', 'Отдаден');
execute upsert_tx( 3, 'bg', 'Изхвърлям');
execute upsert_tx( 4, 'bg', 'Невалиден номер на вагон');

prompt INSERTING into TLGNR_WAGON_INACTIVE_REASON_TX - RU (ENGLISH COPY)
execute upsert_tx( 1, 'ru', 'нет в наличии');
execute upsert_tx( 2, 'ru', 'арендованный');
execute upsert_tx( 3, 'ru', 'уничтожен');
execute upsert_tx( 4, 'ru', 'неверный номер вагонаr');

prompt INSERTING into TLGNR_WAGON_INACTIVE_REASON_TX - PL
execute upsert_tx( 1, 'pl', 'Niedostępne');
execute upsert_tx( 2, 'pl', 'Wynajmowane');
execute upsert_tx( 3, 'pl', 'Złomowany');
execute upsert_tx( 4, 'pl', 'Nieprawidłowy numer wagonu');

prompt INSERTING into TLGNR_WAGON_INACTIVE_REASON_TX - AZ (ENGLISH COPY)
execute upsert_tx( 1, 'az', 'Not available');
execute upsert_tx( 2, 'az', 'Rented out');
execute upsert_tx( 3, 'az', 'Scrapped');
execute upsert_tx( 4, 'az', 'Invalid wagon number');

prompt INSERTING into TLGNR_WAGON_INACTIVE_REASON_TX - HR
execute upsert_tx( 1, 'hr', 'Nije dostupno');
execute upsert_tx( 2, 'hr', 'Iznajmljen');
execute upsert_tx( 3, 'hr', 'Otpisan');
execute upsert_tx( 4, 'hr', 'Neispravan broj vagona');

prompt INSERTING into TLGNR_WAGON_INACTIVE_REASON_TX - SR
execute upsert_tx( 1, 'sr', 'Nije dostupno');
execute upsert_tx( 2, 'sr', 'Iznajmljen');
execute upsert_tx( 3, 'sr', 'Otpisan');
execute upsert_tx( 4, 'sr', 'Neispravan broj vagona');

prompt INSERTING into TLGNR_WAGON_INACTIVE_REASON_TX - TR
execute upsert_tx( 1, 'tr', 'Not available');
execute upsert_tx( 2, 'tr', 'Rented out');
execute upsert_tx( 3, 'tr', 'Scrapped');
execute upsert_tx( 4, 'tr', 'Invalid wagon number');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

