-- $Revision$
-- $Date$
--
prompt '---------- refdata/tlgnr_weigh_system_sp.sql ----------'

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
-- tlgnr_weigh_system_sp_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_weigh_system_sp_cd   in number,
  p_push_mode_on         in number,
  p_enable_tours         in number,
  p_config_table_infix   in varchar2,
  p_client_type          in varchar2,
  p_lds_offline_mode_enable in number,
  p_ctl_act_cd           in number)
is
  v_table_name    varchar2(48) := 'tlgnr_weigh_system_sp_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_weigh_system_sp_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.weigh_system_sp_cd)
    into v_exists_flag
    from tlgnr_weigh_system_sp_bs bs
    where bs.weigh_system_sp_cd = p_weigh_system_sp_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_weigh_system_sp_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_weigh_system_sp_bs set
/* weigh_system_sp_cd: cannot update because this is the key */
          push_mode_on                   = p_push_mode_on,
          enable_tours                   = p_enable_tours,
          config_table_infix             = p_config_table_infix,
          client_type                    = p_client_type,
          lds_offline_mode_enable		 = p_lds_offline_mode_enable,
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          weigh_system_sp_cd = p_weigh_system_sp_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_weigh_system_sp_cd || ').');
      end if;
    else
      insert into tlgnr_weigh_system_sp_bs (
        weigh_system_sp_cd,
        push_mode_on,
        enable_tours,
        config_table_infix,
        client_type,
        lds_offline_mode_enable,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_weigh_system_sp_cd,
        p_push_mode_on,
        p_enable_tours,
        p_config_table_infix,
        p_client_type,
        p_lds_offline_mode_enable,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_weigh_system_sp_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_weigh_system_sp_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_weigh_system_sp_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_weigh_system_sp_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_weigh_system_sp_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.weigh_system_sp_cd) into v_exists_bs_flag
    from tlgnr_weigh_system_sp_bs bs
    where bs.weigh_system_sp_cd = p_weigh_system_sp_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.weigh_system_sp_cd) into v_exists_flag
      from tlgnr_weigh_system_sp_tx tx
      where tx.weigh_system_sp_cd = p_weigh_system_sp_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_weigh_system_sp_tx set
/* p_weigh_system_sp_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          weigh_system_sp_cd = p_weigh_system_sp_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_weigh_system_sp_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_weigh_system_sp_tx (
          weigh_system_sp_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_weigh_system_sp_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_weigh_system_sp_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_weigh_system_sp_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_WEIGH_SYSTEM_SP_BS
execute upsert_bs( 0, 0, 0, null,       null,0,       1);
execute upsert_bs( 1, 0, 0, 'parallel', 'Parallel',0, 1);
execute upsert_bs( 4, 0, 0, null,       null,0,       1);
execute upsert_bs( 5, 0, 0, null,       null,0,       1);
execute upsert_bs( 6, 0, 0, null,       'WeWaS-NGE',0,1);
execute upsert_bs( 7, 1, 0, 'fenics',   'FENICS',1,   1);
execute upsert_bs( 8, 1, 0, 'dorner',   'Dorner',0,   1);
execute upsert_bs( 9, 1, 0, 'kdtruck',  'KDTruck',0,  1);
execute upsert_bs(10, 0, 0, null,       'D800',0,     1);
execute upsert_bs(11, 1, 1, 'schenck',  'Schennk',0,  1);
execute upsert_bs(12, 1, 0, 'hodim',    'Hodim',0,    1);

prompt INSERTING into TLGNR_WEIGH_SYSTEM_SP_TX - EN
execute upsert_tx( 0, 'en', 'No LDS (only manual LOGON/PDA)');
execute upsert_tx( 1, 'en', 'Parallel truck bulk');
execute upsert_tx( 4, 'en', 'Container loading');
execute upsert_tx( 5, 'en', 'Parallel rail bulk');
execute upsert_tx( 6, 'en', 'WeWas bulk');
execute upsert_tx( 7, 'en', 'Fenics FBNL');
execute upsert_tx( 8, 'en', 'Dorner');
execute upsert_tx( 9, 'en', 'KDTruck');
execute upsert_tx(10, 'en', 'D800');
execute upsert_tx(11, 'en', 'Schenck');
execute upsert_tx(12, 'en', 'Hodim');

prompt INSERTING into TLGNR_WEIGH_SYSTEM_SP_TX - DE
execute upsert_tx( 0, 'de', 'Kein LDS (nur manuell LOGON/PDA)');
execute upsert_tx( 1, 'de', 'Parallel LKW lose');
execute upsert_tx( 4, 'de', 'Containerumlad');
execute upsert_tx( 5, 'de', 'Parallel Bahn lose');
execute upsert_tx( 6, 'de', 'WeWas lose');
execute upsert_tx( 7, 'de', 'Fenics FBNL');
execute upsert_tx( 8, 'de', 'Dorner');
execute upsert_tx( 9, 'de', 'KDTruck');
execute upsert_tx(10, 'de', 'D800');
execute upsert_tx(11, 'de', 'Schenck');
execute upsert_tx(12, 'de', 'Hodim');

prompt INSERTING into TLGNR_WEIGH_SYSTEM_SP_TX - FR (ENGLISH COPIED)
execute upsert_tx( 0, 'fr', 'Pas de LDS (chargement Manuel LOGON /PDA)');
execute upsert_tx( 1, 'fr', 'Parallel camion vrac');
execute upsert_tx( 4, 'fr', 'Chargement du container');
execute upsert_tx( 5, 'fr', 'Parallel rail vrac');
execute upsert_tx( 6, 'fr', 'WeWas vrac');
execute upsert_tx( 7, 'fr', 'Fenics FBNL');
execute upsert_tx( 8, 'fr', 'Dorner');
execute upsert_tx( 9, 'fr', 'KDTruck');
execute upsert_tx(10, 'fr', 'D800');
execute upsert_tx(11, 'fr', 'Schenck');
execute upsert_tx(12, 'fr', 'Hodim');

prompt INSERTING into TLGNR_WEIGH_SYSTEM_SP_TX - IT (ENGLISH COPIED)
execute upsert_tx( 0, 'it', 'Non LDS (solo manuale LOGON/PDA)');
execute upsert_tx( 1, 'it', 'Parallel camion sfuso');
execute upsert_tx( 4, 'it', 'Caricamento del container');
execute upsert_tx( 5, 'it', 'Parallel ferrovia sfuso');
execute upsert_tx( 6, 'it', 'WeWas sfuso');
execute upsert_tx( 7, 'it', 'Fenics FBNL');
execute upsert_tx( 8, 'it', 'Dorner');
execute upsert_tx( 9, 'it', 'KDTruck');
execute upsert_tx(10, 'it', 'D800');
execute upsert_tx(11, 'it', 'Schenck');
execute upsert_tx(12, 'it', 'Hodim');

prompt INSERTING into TLGNR_WEIGH_SYSTEM_SP_TX - ES (ENGLISH COPIED)
execute upsert_tx( 0, 'es', 'Sin LDS (sólo manual LOGON/PDA)');
execute upsert_tx( 1, 'es', 'Camión Parallel de material a granel');
execute upsert_tx( 4, 'es', 'Carga contenedor');
execute upsert_tx( 5, 'es', 'Tren Parallel de material a granel');
execute upsert_tx( 6, 'es', 'WeWas granel');
execute upsert_tx( 7, 'es', 'Fenics FBNL');
execute upsert_tx( 8, 'es', 'Dorner');
execute upsert_tx( 9, 'es', 'KDTruck');
execute upsert_tx(10, 'es', 'D800');
execute upsert_tx(11, 'es', 'Schenck');
execute upsert_tx(12, 'es', 'Hodim');

prompt INSERTING into TLGNR_WEIGH_SYSTEM_SP_TX - CS (ENGLISH COPY)
execute upsert_tx( 0, 'cs', 'Ne LDS (pouze manuální LOGON/PDA)');
execute upsert_tx( 1, 'cs', 'Parallel vážní systém - vozidlo');
execute upsert_tx( 4, 'cs', 'Nakládka kontejneru');
execute upsert_tx( 5, 'cs', 'Parallel vážní systém - železnice');
execute upsert_tx( 6, 'cs', 'WeWas vážní systém - volně ložený');
execute upsert_tx( 7, 'cs', 'Fenics FBNL');
execute upsert_tx( 8, 'cs', 'Dorner');
execute upsert_tx( 9, 'cs', 'KDTruck');
execute upsert_tx(11, 'cs', 'Schenck');
execute upsert_tx(12, 'cs', 'Hodim');

prompt INSERTING into TLGNR_WEIGH_SYSTEM_SP_TX - SK (ENGLISH COPY)
execute upsert_tx( 0, 'sk', 'Žiadne LDS (len manuálný LOGON/PDA)');
execute upsert_tx( 1, 'sk', 'Parallel voľne ložený ťahač');
execute upsert_tx( 4, 'sk', 'Kontajner nakladaný');
execute upsert_tx( 5, 'sk', 'Parallel voľne ložený vlak');
execute upsert_tx( 6, 'sk', 'WeWas voľne ložený');
execute upsert_tx( 7, 'sk', 'Fenics FBNL');
execute upsert_tx( 8, 'sk', 'Dorner');
execute upsert_tx( 9, 'sk', 'KDTruck');
execute upsert_tx(10, 'sk', 'D800');
execute upsert_tx(11, 'sk', 'Schenck');
execute upsert_tx(12, 'sk', 'Hodim');

prompt INSERTING into TLGNR_WEIGH_SYSTEM_SP_TX - HU (ENGLISH COPY)
execute upsert_tx( 0, 'hu', 'Nincs LDS (csak manuális LOGON/PDA)');
execute upsert_tx( 1, 'hu', 'Parallel kamion töltés ömlesztettnél');
execute upsert_tx( 4, 'hu', 'Konténer töltés');
execute upsert_tx( 5, 'hu', 'Parallel vasúti töltés ömlesztetnél');
execute upsert_tx( 6, 'hu', 'WEWas ömlesztett');
execute upsert_tx( 7, 'hu', 'Fenics FBNL');
execute upsert_tx( 8, 'hu', 'Dorner');
execute upsert_tx( 9, 'hu', 'KDTruck');
execute upsert_tx(10, 'hu', 'D800');
execute upsert_tx(11, 'hu', 'Schenck');
execute upsert_tx(12, 'hu', 'Hodim');

prompt INSERTING into TLGNR_WEIGH_SYSTEM_SP_TX - RO (ENGLISH COPY)
execute upsert_tx( 0, 'ro', 'Nu LDS (doar LOGON/PDA manual)');
execute upsert_tx( 1, 'ro', 'Vrac camion Parallel');
execute upsert_tx( 4, 'ro', 'Incarcare container');
execute upsert_tx( 5, 'ro', 'Vrac tren Parallel');
execute upsert_tx( 6, 'ro', 'Vrac WeWas');
execute upsert_tx( 7, 'ro', 'Fenics FBNL');
execute upsert_tx( 8, 'ro', 'Dorner');
execute upsert_tx( 9, 'ro', 'KDTruck');
execute upsert_tx(10, 'ro', 'D800');
execute upsert_tx(11, 'ro', 'Schenck');
execute upsert_tx(12, 'ro', 'Hodim');

prompt INSERTING into TLGNR_WEIGH_SYSTEM_SP_TX - BG (ENGLISH COPY)
execute upsert_tx( 0, 'bg', 'без LDS (само ръчен LOGON/PDA)');
execute upsert_tx( 1, 'bg', 'Parallel камион насипен');
execute upsert_tx( 4, 'bg', 'Контейнер товарене');
execute upsert_tx( 5, 'bg', 'Parallel жп насипен');
execute upsert_tx( 6, 'bg', 'WeWas  насипен');
execute upsert_tx( 7, 'bg', 'Fenics FBNL');
execute upsert_tx( 8, 'bg', 'Dorner');
execute upsert_tx( 9, 'bg', 'KDTruck');
execute upsert_tx(10, 'bg', 'D800');
execute upsert_tx(11, 'bg', 'Schenck');
execute upsert_tx(12, 'bg', 'Hodim');

prompt INSERTING into TLGNR_WEIGH_SYSTEM_SP_TX - RU (ENGLISH COPY)
execute upsert_tx( 0, 'ru', 'Нет LDS (только в ручную LOGON / КПК)');
execute upsert_tx( 1, 'ru', 'Parallel truck bulk');
execute upsert_tx( 4, 'ru', 'Загрузка контейнера');
execute upsert_tx( 5, 'ru', 'Parallel rail bulk');
execute upsert_tx( 6, 'ru', 'WeWas bulk');
execute upsert_tx( 7, 'ru', 'Fenics FBNL');
execute upsert_tx( 8, 'ru', 'Dorner');
execute upsert_tx( 9, 'ru', 'KDTruck');
execute upsert_tx(10, 'ru', 'D800');
execute upsert_tx(11, 'ru', 'Schenck');
execute upsert_tx(12, 'ru', 'Hodim');

prompt INSERTING into TLGNR_WEIGH_SYSTEM_SP_TX - PL (ENGLISH COPY)
execute upsert_tx( 0, 'pl', 'No LDS (tylko ręczny LOGON/PDA)');
execute upsert_tx( 1, 'pl', 'Parallel truck bulk');
execute upsert_tx( 4, 'pl', 'Ładowanie kontenera');
execute upsert_tx( 5, 'pl', 'Parallel rail bulk');
execute upsert_tx( 6, 'pl', 'WeWas bulk');
execute upsert_tx( 7, 'pl', 'Fenics FBNL');
execute upsert_tx( 8, 'pl', 'Dorner');
execute upsert_tx( 9, 'pl', 'KDTruck');
execute upsert_tx(10, 'pl', 'D800');
execute upsert_tx(11, 'pl', 'Schenck');
execute upsert_tx(12, 'pl', 'Hodim');

prompt INSERTING into TLGNR_WEIGH_SYSTEM_SP_TX - AZ (ENGLISH COPY)
execute upsert_tx( 0, 'az', 'Heç bir LDS (yalnız əllə LOGON/PDA)');
execute upsert_tx( 1, 'az', 'Parallel Yük maşınının qalaq');
execute upsert_tx( 4, 'az', 'Konteynerə yükləmə');
execute upsert_tx( 5, 'az', 'Parallel Dəmiryol Qalaq');
execute upsert_tx( 6, 'az', 'WeWas Qalaq');
execute upsert_tx( 7, 'az', 'Fenics FBNL');
execute upsert_tx( 8, 'az', 'Dorner');
execute upsert_tx( 9, 'az', 'KDTruck');
execute upsert_tx(10, 'az', 'D800');
execute upsert_tx(11, 'az', 'Schenck');
execute upsert_tx(12, 'az', 'Hodim');

prompt INSERTING into TLGNR_WEIGH_SYSTEM_SP_TX - HR (ENGLISH COPY)
execute upsert_tx( 0, 'hr', 'Nema LDS-a (samo ručno u  Logon/PDA)');
execute upsert_tx( 1, 'hr', 'Parallel truck bulk');
execute upsert_tx( 4, 'hr', 'Utovar kontejner');
execute upsert_tx( 5, 'hr', 'Parallel rail bulk');
execute upsert_tx( 6, 'hr', 'WeWas bulk');
execute upsert_tx( 7, 'hr', 'Fenics FBNL');
execute upsert_tx( 8, 'hr', 'Dorner');
execute upsert_tx( 9, 'hr', 'KDTruck');
execute upsert_tx(10, 'hr', 'D800');
execute upsert_tx(11, 'hr', 'Schenck');
execute upsert_tx(12, 'hr', 'Hodim');

prompt INSERTING into TLGNR_WEIGH_SYSTEM_SP_TX - SR (ENGLISH COPY)
execute upsert_tx( 0, 'sr', 'Nema LDS-a (samo ručno u  Logon/PDA)');
execute upsert_tx( 1, 'sr', 'Parallel truck bulk');
execute upsert_tx( 4, 'sr', 'Utovar kontejner');
execute upsert_tx( 5, 'sr', 'Parallel rail bulk');
execute upsert_tx( 6, 'sr', 'WeWas bulk');
execute upsert_tx( 7, 'sr', 'Fenics FBNL');
execute upsert_tx( 8, 'sr', 'Dorner');
execute upsert_tx( 9, 'sr', 'KDTruck');
execute upsert_tx(10, 'sr', 'D800');
execute upsert_tx(11, 'sr', 'Schenck');
execute upsert_tx(12, 'sr', 'Hodim');

prompt INSERTING into TLGNR_WEIGH_SYSTEM_SP_TX - TR
execute upsert_tx( 0, 'tr', 'No LDS (only manual LOGON/PDA)');
execute upsert_tx( 1, 'tr', 'Parallel truck bulk');
execute upsert_tx( 4, 'tr', 'Container loading');
execute upsert_tx( 5, 'tr', 'Parallel rail bulk');
execute upsert_tx( 6, 'tr', 'WeWas bulk');
execute upsert_tx( 7, 'tr', 'Fenics FBNL');
execute upsert_tx( 8, 'tr', 'Dorner');
execute upsert_tx( 9, 'tr', 'KDTruck');
execute upsert_tx(10, 'tr', 'D800');
execute upsert_tx(11, 'tr', 'Schenck');
execute upsert_tx(12, 'tr', 'Hodim');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

