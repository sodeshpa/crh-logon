-- $Revision$
-- $Date$
--
-- List

prompt '---------- refdata/tlgnr_simple_vehicle_type.sql ----------'

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
-- tlgnr_simple_vehicle_type_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_simple_vehicle_type_cd     in number,
  p_volume_m3                  in number,
  p_planning_net_weight_t      in number,
  p_ctl_act_cd                 in number)
is
  v_table_name    varchar2(48) := 'tlgnr_simple_vehicle_type_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_simple_vehicle_type_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.simple_vehicle_type_cd)
    into v_exists_flag
    from tlgnr_simple_vehicle_type_bs bs
    where bs.simple_vehicle_type_cd = p_simple_vehicle_type_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_simple_vehicle_type_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_simple_vehicle_type_bs set
/* simple_vehicle_type_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          volume_m3                      = p_volume_m3,
          planning_net_weight_t          = p_planning_net_weight_t,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          simple_vehicle_type_cd = p_simple_vehicle_type_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_simple_vehicle_type_cd || ').');
      end if;
    else
      insert into tlgnr_simple_vehicle_type_bs (
        simple_vehicle_type_cd,
        volume_m3,
        planning_net_weight_t,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_simple_vehicle_type_cd,
        p_volume_m3,
        p_planning_net_weight_t,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_simple_vehicle_type_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_simple_vehicle_type_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_simple_vehicle_type_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_simple_vehicle_type_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_simple_vehicle_type_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.simple_vehicle_type_cd) into v_exists_bs_flag
    from tlgnr_simple_vehicle_type_bs bs
    where bs.simple_vehicle_type_cd = p_simple_vehicle_type_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.simple_vehicle_type_cd) into v_exists_flag
      from tlgnr_simple_vehicle_type_tx tx
      where tx.simple_vehicle_type_cd = p_simple_vehicle_type_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_simple_vehicle_type_tx set
/* p_simple_vehicle_type_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          simple_vehicle_type_cd = p_simple_vehicle_type_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_simple_vehicle_type_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_simple_vehicle_type_tx (
          simple_vehicle_type_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_simple_vehicle_type_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_simple_vehicle_type_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_simple_vehicle_type_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_SIMPLE_VEHICLE_TYPE_BS
execute upsert_bs(1, 3600, 3600, 1);
execute upsert_bs(2, 1500, 1500, 1);

prompt INSERTING into TLGNR_SIMPLE_VEHICLE_TYPE_TX - EN
execute upsert_tx(1, 'en', 'Ship');
execute upsert_tx(2, 'en', 'Block train');

prompt INSERTING into TLGNR_SIMPLE_VEHICLE_TYPE_TX - DE
execute upsert_tx(1, 'de', 'Schiff'    );
execute upsert_tx(2, 'de', 'Blockzug'    );

prompt INSERTING into TLGNR_SIMPLE_VEHICLE_TYPE_TX - FR
execute upsert_tx(1, 'fr', 'Bateau'    );
execute upsert_tx(2, 'fr', 'Train entier'    );

prompt INSERTING into TLGNR_SIMPLE_VEHICLE_TYPE_TX - IT
execute upsert_tx(1, 'it', 'Nave'    );
execute upsert_tx(2, 'it', 'Treno'    );

prompt INSERTING into TLGNR_SIMPLE_VEHICLE_TYPE_TX - ES
execute upsert_tx(1, 'es', 'Barco'    );
execute upsert_tx(2, 'es', 'Tren completo'    );

prompt INSERTING into TLGNR_SIMPLE_VEHICLE_TYPE_TX - CS (ENGLISH COPY)
execute upsert_tx(1, 'cs', 'Loď');
execute upsert_tx(2, 'cs', 'Ucelený vlak');

prompt INSERTING into TLGNR_SIMPLE_VEHICLE_TYPE_TX - SK (ENGLISH COPY)
execute upsert_tx(1, 'sk', 'Loď');
execute upsert_tx(2, 'sk', 'Ucelený vlak');

prompt INSERTING into TLGNR_SIMPLE_VEHICLE_TYPE_TX - HU (ENGLISH COPY)
execute upsert_tx(1, 'hu', 'Szállít');
execute upsert_tx(2, 'hu', 'Szerelvény');

prompt INSERTING into TLGNR_SIMPLE_VEHICLE_TYPE_TX - RO (ENGLISH COPY)
execute upsert_tx(1, 'ro', 'Vapor');
execute upsert_tx(2, 'ro', 'Tren');

prompt INSERTING into TLGNR_SIMPLE_VEHICLE_TYPE_TX - BG (ENGLISH COPY)
execute upsert_tx(1, 'bg', 'Кораб');
execute upsert_tx(2, 'bg', 'Блок влак');

prompt INSERTING into TLGNR_SIMPLE_VEHICLE_TYPE_TX - RU (ENGLISH COPY)
execute upsert_tx(1, 'ru', 'баржа');
execute upsert_tx(2, 'ru', 'маршрутный поезд');

prompt INSERTING into TLGNR_SIMPLE_VEHICLE_TYPE_TX - PL
execute upsert_tx(1, 'pl', 'Statek');
execute upsert_tx(2, 'pl', 'Zabl. pociągu');

prompt INSERTING into TLGNR_SIMPLE_VEHICLE_TYPE_TX - AZ (ENGLISH COPY)
execute upsert_tx(1, 'az', 'Gəmi');
execute upsert_tx(2, 'az', 'Marşrut qatarı');

prompt INSERTING into TLGNR_SIMPLE_VEHICLE_TYPE_TX - HR
execute upsert_tx(1, 'hr', 'Brod');
execute upsert_tx(2, 'hr', 'Blokiraj vlak');

prompt INSERTING into TLGNR_SIMPLE_VEHICLE_TYPE_TX - SR
execute upsert_tx(1, 'sr', 'Brod');
execute upsert_tx(2, 'sr', 'Blok vagona');

prompt INSERTING into TLGNR_SIMPLE_VEHICLE_TYPE_TX - TR
execute upsert_tx(1, 'tr', 'Ship');
execute upsert_tx(2, 'tr', 'Block train');

commit;


drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;
