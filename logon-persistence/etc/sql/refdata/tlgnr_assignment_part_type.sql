-- $Revision$
-- $Date$
--
-- This is an internal, technical table:
-- - No translations needed
-- - Extending the possible values requires code changes

--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_assignment_part_type.sql ----------'

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
-- tlgnr_assignment_part_type_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_assignment_part_type_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_assignment_part_type_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_assignment_part_type_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.assignment_part_type_cd)
    into v_exists_flag
    from tlgnr_assignment_part_type_bs bs
    where bs.assignment_part_type_cd = p_assignment_part_type_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_assignment_part_type_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_assignment_part_type_bs set
          /* assignment_part_type_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          assignment_part_type_cd = p_assignment_part_type_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_assignment_part_type_cd || ').');
      end if;
    else
      insert into tlgnr_assignment_part_type_bs (
        assignment_part_type_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_assignment_part_type_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_assignment_part_type_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_assignment_part_type_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_assignment_part_type_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2) 
is
  v_table_name      varchar2(48) := 'tlgnr_assignment_part_type_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_assignment_part_type_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

    ----------------------------------------------------------------------------
    -- Lookup BS entry first
    ----------------------------------------------------------------------------
    select count(bs.assignment_part_type_cd) into v_exists_bs_flag
    from tlgnr_assignment_part_type_bs bs
    where bs.assignment_part_type_cd = p_assignment_part_type_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.assignment_part_type_cd) into v_exists_flag
      from tlgnr_assignment_part_type_tx tx
      where tx.assignment_part_type_cd = p_assignment_part_type_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_assignment_part_type_tx set
          /* p_assignment_part_type_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          assignment_part_type_cd = p_assignment_part_type_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_assignment_part_type_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_assignment_part_type_tx (
          assignment_part_type_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_assignment_part_type_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_assignment_part_type_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_assignment_part_type_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_ASSIGNMENT_PART_TYPE_BS
execute upsert_bs(0, 1);
execute upsert_bs(1, 1);
execute upsert_bs(2, 1);
execute upsert_bs(3, 1);
execute upsert_bs(4, 1);
execute upsert_bs(5, 1);

prompt INSERTING into TLGNR_ASSIGNMENT_PART_TYPE_TX - EN
execute upsert_tx(0, 'en', 'Pre-loading');
execute upsert_tx(1, 'en', 'Loading');
execute upsert_tx(2, 'en', 'Driving-to');
execute upsert_tx(3, 'en', 'Unloading');
execute upsert_tx(4, 'en', 'Driving-from');
execute upsert_tx(5, 'en', 'Finished');

prompt INSERTING into TLGNR_ASSIGNMENT_PART_TYPE_TX - DE
execute upsert_tx(0, 'de', 'Pre-loading');
execute upsert_tx(1, 'de', 'Loading');
execute upsert_tx(2, 'de', 'Driving-to');
execute upsert_tx(3, 'de', 'Unloading');
execute upsert_tx(4, 'de', 'Driving-from');
execute upsert_tx(5, 'de', 'Finished');

prompt INSERTING into TLGNR_ASSIGNMENT_PART_TYPE_TX - FR
execute upsert_tx(0, 'fr', 'Préchargement');
execute upsert_tx(1, 'fr', 'En chargement');
execute upsert_tx(2, 'fr', 'Déplacement à');
execute upsert_tx(3, 'fr', 'En déchargement');
execute upsert_tx(4, 'fr', 'Déplacement de');
execute upsert_tx(5, 'fr', 'Terminé');

prompt INSERTING into TLGNR_ASSIGNMENT_PART_TYPE_TX - IT
execute upsert_tx(0, 'it', 'Precaricando');
execute upsert_tx(1, 'it', 'Carico in corso');
execute upsert_tx(2, 'it', 'Guidando a');
execute upsert_tx(3, 'it', 'Scarico in corso');
execute upsert_tx(4, 'it', 'Guidando da');
execute upsert_tx(5, 'it', 'Terminato');

prompt INSERTING into TLGNR_ASSIGNMENT_PART_TYPE_TX - ES
execute upsert_tx(0, 'es', 'Precargando');
execute upsert_tx(1, 'es', 'Cargando');
execute upsert_tx(2, 'es', 'Conduciendo hacia');
execute upsert_tx(3, 'es', 'Descargando');
execute upsert_tx(4, 'es', 'Conduciendo desde');
execute upsert_tx(5, 'es', 'Finalizado');

prompt INSERTING into TLGNR_ASSIGNMENT_PART_TYPE_TX - CS
execute upsert_tx(0, 'cs', 'Přednakládka');
execute upsert_tx(1, 'cs', 'Nakládka');
execute upsert_tx(2, 'cs', 'Řízení do');
execute upsert_tx(3, 'cs', 'Vykládka');
execute upsert_tx(4, 'cs', 'Řízení z');
execute upsert_tx(5, 'cs', 'Ukončeno');

prompt INSERTING into TLGNR_ASSIGNMENT_PART_TYPE_TX - SK
execute upsert_tx(0, 'sk', 'Pred nakládka');
execute upsert_tx(1, 'sk', 'Nakládka');
execute upsert_tx(2, 'sk', 'Jazda do');
execute upsert_tx(3, 'sk', 'Vykládka');
execute upsert_tx(4, 'sk', 'Jazda z');
execute upsert_tx(5, 'sk', 'Ukončenie');

prompt INSERTING into TLGNR_ASSIGNMENT_PART_TYPE_TX - HU
execute upsert_tx(0, 'hu', 'Elő rakodás');
execute upsert_tx(1, 'hu', 'Rakodás');
execute upsert_tx(2, 'hu', 'Leszállítás');
execute upsert_tx(3, 'hu', 'Kirakodás');
execute upsert_tx(4, 'hu', 'Elszállítás');
execute upsert_tx(5, 'hu', 'Befejezés');

prompt INSERTING into TLGNR_ASSIGNMENT_PART_TYPE_TX - RO
execute upsert_tx(0, 'ro', 'Pre-incarcare');
execute upsert_tx(1, 'ro', 'Incarcare');
execute upsert_tx(2, 'ro', 'Punct destinatie');
execute upsert_tx(3, 'ro', 'Descarcare');
execute upsert_tx(4, 'ro', 'Punct plecare');
execute upsert_tx(5, 'ro', 'Terminat');

prompt INSERTING into TLGNR_ASSIGNMENT_PART_TYPE_TX - BG
execute upsert_tx(0, 'bg', 'Презареждане');
execute upsert_tx(1, 'bg', 'Товарене');
execute upsert_tx(2, 'bg', 'Шофиране до');
execute upsert_tx(3, 'bg', 'Разтоварване');
execute upsert_tx(4, 'bg', 'Шофиране от');
execute upsert_tx(5, 'bg', 'Приключен');

prompt INSERTING into TLGNR_ASSIGNMENT_PART_TYPE_TX - RU (ENGLISH COPY)
execute upsert_tx(0, 'ru', 'погрузка в счет завтра');
execute upsert_tx(1, 'ru', 'погрузка');
execute upsert_tx(2, 'ru', 'подъехать к');
execute upsert_tx(3, 'ru', 'выгрузка');
execute upsert_tx(4, 'ru', 'отъехать от');
execute upsert_tx(5, 'ru', 'окончание');

prompt INSERTING into TLGNR_ASSIGNMENT_PART_TYPE_TX - PL (ENGLISH COPY)
execute upsert_tx(0, 'pl', 'Wstępne ładowanie');
execute upsert_tx(1, 'pl', 'Ładowanie');
execute upsert_tx(2, 'pl', 'Dojazd');
execute upsert_tx(3, 'pl', 'Rozładunek');
execute upsert_tx(4, 'pl', 'Wyjazd');
execute upsert_tx(5, 'pl', 'Zakończony');

prompt INSERTING into TLGNR_ASSIGNMENT_PART_TYPE_TX - AZ (ENGLISH COPY)
execute upsert_tx(0, 'az', 'Pre-loading');
execute upsert_tx(1, 'az', 'Loading');
execute upsert_tx(2, 'az', 'Driving-to');
execute upsert_tx(3, 'az', 'Unloading');
execute upsert_tx(4, 'az', 'Driving-from');
execute upsert_tx(5, 'az', 'Finished');

prompt INSERTING into TLGNR_ASSIGNMENT_PART_TYPE_TX - HR (ENGLISH COPY)
execute upsert_tx(0, 'hr', 'Prije utovara');
execute upsert_tx(1, 'hr', 'Utovar');
execute upsert_tx(2, 'hr', 'Vožnja do');
execute upsert_tx(3, 'hr', 'Istovar');
execute upsert_tx(4, 'hr', 'Vožnja od-');
execute upsert_tx(5, 'hr', 'Gotov');

prompt INSERTING into TLGNR_ASSIGNMENT_PART_TYPE_TX - SR (ENGLISH COPY)
execute upsert_tx(0, 'sr', 'Pre utovara');
execute upsert_tx(1, 'sr', 'Utovar');
execute upsert_tx(2, 'sr', 'Voziti do');
execute upsert_tx(3, 'sr', 'Istovar');
execute upsert_tx(4, 'sr', 'Voziti od');
execute upsert_tx(5, 'sr', 'Realizovano');

prompt INSERTING into TLGNR_ASSIGNMENT_PART_TYPE_TX - TR
execute upsert_tx(0, 'tr', 'Pre-loading');
execute upsert_tx(1, 'tr', 'Loading');
execute upsert_tx(2, 'tr', 'Driving-to');
execute upsert_tx(3, 'tr', 'Unloading');
execute upsert_tx(4, 'tr', 'Driving-from');
execute upsert_tx(5, 'tr', 'Finished');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

