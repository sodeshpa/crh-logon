-- $Revision$
-- $Date$
--
-- 2.9.2009 clarified with Crh, because "Tiltable" seems to be a TRAILER property, not container.
--
-- Explanation:
-- Trailer: FEATURE: does the trailer have facilities to tilt its load?
--     Currently, all trailers for containers (trailer_trsp_cat=container) at crh have this.
-- Container: REQUIREMENT: Must the container be tilted for unloading, or can it be unloaded also horizontally?
--     There are also fix installed containers on rail wagons, which have unloading pipes on the side => can be unloaded horizontally.
--     The common containers for trucks/trailers have their unloading pipes at the back, and must be tilted to unload.
-- => Container Type 'horizontal' == unloading horizontally     == unloading at side
--    Container Type 'tiltable'   == unloading only when tilted == unloading at back

--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_container_type.sql ----------'

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
-- tlgnr_container_type_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_container_type_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_container_type_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_container_type_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.container_type_cd)
    into v_exists_flag
    from tlgnr_container_type_bs bs
    where bs.container_type_cd = p_container_type_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_container_type_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_container_type_bs set
/* container_type_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          container_type_cd = p_container_type_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_container_type_cd || ').');
      end if;
    else
      insert into tlgnr_container_type_bs (
        container_type_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_container_type_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_container_type_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_container_type_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_container_type_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_container_type_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_container_type_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.container_type_cd) into v_exists_bs_flag
    from tlgnr_container_type_bs bs
    where bs.container_type_cd = p_container_type_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.container_type_cd) into v_exists_flag
      from tlgnr_container_type_tx tx
      where tx.container_type_cd = p_container_type_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_container_type_tx set
/* p_container_type_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          container_type_cd = p_container_type_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_container_type_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_container_type_tx (
          container_type_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_container_type_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_container_type_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_container_type_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_CONTAINER_TYPE_BS
execute upsert_bs(1, 1);
execute upsert_bs(2, 1);

prompt INSERTING into TLGNR_CONTAINER_TYPE_TX - EN
execute upsert_tx(1, 'en', 'Unloading horizontally');
execute upsert_tx(2, 'en', 'Unloading only when tilted');

prompt INSERTING into TLGNR_CONTAINER_TYPE_TX - DE
execute upsert_tx(1, 'de', 'Horizontal entladbar');
execute upsert_tx(2, 'de', 'Nur gekippt entladbar');

prompt INSERTING into TLGNR_CONTAINER_TYPE_TX - FR
execute upsert_tx(1, 'fr', 'Déchargement horizontal (FR)');
execute upsert_tx(2, 'fr', 'Déchargement par basculement (FR)');

prompt INSERTING into TLGNR_CONTAINER_TYPE_TX - IT
execute upsert_tx(1, 'it', 'Scarico orizzontale (IT)');
execute upsert_tx(2, 'it', 'Scarico per inclinazione (IT)');

prompt INSERTING into TLGNR_CONTAINER_TYPE_TX - ES
execute upsert_tx(1, 'es', 'Descarga horizontal');
execute upsert_tx(2, 'es', 'Descarga mediante volcado');

prompt INSERTING into TLGNR_CONTAINER_TYPE_TX - CS
execute upsert_tx(1, 'cs', 'Vykládka vodorovně');
execute upsert_tx(2, 'cs', 'Vykládka pouze sklopením');

prompt INSERTING into TLGNR_CONTAINER_TYPE_TX - SK
execute upsert_tx(1, 'sk', 'Horizontálna vykládka');
execute upsert_tx(2, 'sk', 'Vykládka len pri vykladaní');

prompt INSERTING into TLGNR_CONTAINER_TYPE_TX - HU
execute upsert_tx(1, 'hu', 'Vízszintes kirakodás');
execute upsert_tx(2, 'hu', 'Kirakodás ha billenthető (ponyvatetős)');

prompt INSERTING into TLGNR_CONTAINER_TYPE_TX - RO
execute upsert_tx(1, 'ro', 'Descarcare orizontala');
execute upsert_tx(2, 'ro', 'Descarcare doar inclinata');

prompt INSERTING into TLGNR_CONTAINER_TYPE_TX - BG
execute upsert_tx(1, 'bg', 'Хоризонтално разтоварване');
execute upsert_tx(2, 'bg', 'Разтоварване само при накланяне');

prompt INSERTING into TLGNR_CONTAINER_TYPE_TX - RU
execute upsert_tx(1, 'ru', 'Горизонтальная выгрузка');
execute upsert_tx(2, 'ru', 'Выгрузка только при наклоне');

prompt INSERTING into TLGNR_CONTAINER_TYPE_TX - PL
execute upsert_tx(1, 'pl', 'Rozładunek w poziomie');
execute upsert_tx(2, 'pl', 'Rozładunek tylko w przechyleniu');

prompt INSERTING into TLGNR_CONTAINER_TYPE_TX - AZ
execute upsert_tx(1, 'az', 'Unloading horizontally');
execute upsert_tx(2, 'az', 'Unloading only when tilted');

prompt INSERTING into TLGNR_CONTAINER_TYPE_TX - HR
execute upsert_tx(1, 'hr', 'Istovar vodoravno');
execute upsert_tx(2, 'hr', 'Istovar kad je nagnut');

prompt INSERTING into TLGNR_CONTAINER_TYPE_TX - SR
execute upsert_tx(1, 'sr', 'Horizontalni istovar');
execute upsert_tx(2, 'sr', 'Istovar pod nagibom');

prompt INSERTING into TLGNR_CONTAINER_TYPE_TX - TR
execute upsert_tx(1, 'tr', 'Unloading horizontally');
execute upsert_tx(2, 'tr', 'Unloading only when tilted');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

