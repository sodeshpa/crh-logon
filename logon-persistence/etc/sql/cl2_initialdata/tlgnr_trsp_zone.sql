-- $Revision$
-- $Date$
--
-- based on ticket ADN71680

prompt '--------- cl2_initialdata/tlgnr_trsp_zone.sql ----------'

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
-- tlgnr_trsp_zone_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_trsp_zone_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_trsp_zone_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_trsp_zone_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.trsp_zone_cd)
    into v_exists_flag
    from tlgnr_trsp_zone_bs bs
    where bs.trsp_zone_cd = p_trsp_zone_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_trsp_zone_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_trsp_zone_bs set
          /* trsp_zone_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          trsp_zone_cd = p_trsp_zone_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_trsp_zone_cd || ').');
      end if;
    else
      insert into tlgnr_trsp_zone_bs (
        trsp_zone_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_trsp_zone_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_trsp_zone_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_trsp_zone_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_trsp_zone_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_trsp_zone_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_trsp_zone_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

    ----------------------------------------------------------------------------
    -- Lookup BS entry first
    ----------------------------------------------------------------------------
    select count(bs.trsp_zone_cd) into v_exists_bs_flag
    from tlgnr_trsp_zone_bs bs
    where bs.trsp_zone_cd = p_trsp_zone_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.trsp_zone_cd) into v_exists_flag
      from tlgnr_trsp_zone_tx tx
      where tx.trsp_zone_cd = p_trsp_zone_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_trsp_zone_tx set
          /* p_trsp_zone_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          trsp_zone_cd = p_trsp_zone_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_trsp_zone_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_trsp_zone_tx (
          trsp_zone_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_trsp_zone_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_trsp_zone_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_trsp_zone_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_TRSP_ZONE_BS
execute upsert_bs('BG-A01', 1);
execute upsert_bs('RS-A01', 1);
execute upsert_bs('SK-A01', 1);
execute upsert_bs('PL-A01', 1);
execute upsert_bs('RO-A01', 1);
execute upsert_bs('HU-A01', 1);
execute upsert_bs('HR-A01', 1);
execute upsert_bs('AT-A01', 1);

prompt INSERTING into TLGNR_TRSP_ZONE_TX - EN
execute upsert_tx('BG-A01', 'en', 'Bulgaria' );
execute upsert_tx('RS-A01', 'en', 'Serbia' );
execute upsert_tx('SK-A01', 'en', 'Slovakia' );
execute upsert_tx('PL-A01', 'en', 'Poland' );
execute upsert_tx('RO-A01', 'en', 'Romania' );
execute upsert_tx('HU-A01', 'en', 'Hungary' );
execute upsert_tx('HR-A01', 'en', 'Croatia' );
execute upsert_tx('AT-A01', 'en', 'Austria' );

prompt INSERTING into TLGNR_TRSP_ZONE_TX - DE
execute upsert_tx('BG-A01', 'de', 'Bulgaria');
execute upsert_tx('RS-A01', 'de', 'Serbia' );
execute upsert_tx('SK-A01', 'de', 'Slovakia' );
execute upsert_tx('PL-A01', 'de', 'Poland' );
execute upsert_tx('RO-A01', 'de', 'Romania' );
execute upsert_tx('HU-A01', 'de', 'Hungary' );
execute upsert_tx('HR-A01', 'de', 'Croatia' );
execute upsert_tx('AT-A01', 'de', 'Austria' );

prompt INSERTING into TLGNR_TRSP_ZONE_TX - FR
execute upsert_tx('BG-A01', 'fr', 'Bulgaria' );
execute upsert_tx('RS-A01', 'fr', 'Serbia' );
execute upsert_tx('SK-A01', 'fr', 'Slovakia' );
execute upsert_tx('PL-A01', 'fr', 'Poland' );
execute upsert_tx('RO-A01', 'fr', 'Romania' );
execute upsert_tx('HU-A01', 'fr', 'Hungary' );
execute upsert_tx('HR-A01', 'fr', 'Croatia' );
execute upsert_tx('AT-A01', 'fr', 'Austria' );

prompt INSERTING into TLGNR_TRSP_ZONE_TX - IT
execute upsert_tx('BG-A01', 'it', 'Bulgaria' );
execute upsert_tx('RS-A01', 'it', 'Serbia' );
execute upsert_tx('SK-A01', 'it', 'Slovakia' );
execute upsert_tx('PL-A01', 'it', 'Poland' );
execute upsert_tx('RO-A01', 'it', 'Romania' );
execute upsert_tx('HU-A01', 'it', 'Hungary' );
execute upsert_tx('HR-A01', 'it', 'Croatia' );
execute upsert_tx('AT-A01', 'it', 'Austria' );

prompt INSERTING into TLGNR_TRSP_ZONE_TX - ES
execute upsert_tx('BG-A01', 'es', 'Bulgaria' );
execute upsert_tx('RS-A01', 'es', 'Serbia' );
execute upsert_tx('SK-A01', 'es', 'Slovakia' );
execute upsert_tx('PL-A01', 'es', 'Poland' );
execute upsert_tx('RO-A01', 'es', 'Romania' );
execute upsert_tx('HU-A01', 'es', 'Hungary' );
execute upsert_tx('HR-A01', 'es', 'Croatia' );
execute upsert_tx('AT-A01', 'es', 'Austria' );

prompt INSERTING into TLGNR_TRSP_ZONE_TX - CS
execute upsert_tx('BG-A01', 'cs', 'Bulgaria' );
execute upsert_tx('RS-A01', 'cs', 'Serbia' );
execute upsert_tx('SK-A01', 'cs', 'Slovakia' );
execute upsert_tx('PL-A01', 'cs', 'Poland' );
execute upsert_tx('RO-A01', 'cs', 'Romania' );
execute upsert_tx('HU-A01', 'cs', 'Hungary' );
execute upsert_tx('HR-A01', 'cs', 'Croatia' );
execute upsert_tx('AT-A01', 'cs', 'Austria' );

prompt INSERTING into TLGNR_TRSP_ZONE_TX - SK
execute upsert_tx('BG-A01', 'sk', 'Bulgaria' );
execute upsert_tx('RS-A01', 'sk', 'Serbia' );
execute upsert_tx('SK-A01', 'sk', 'Slovakia' );
execute upsert_tx('PL-A01', 'sk', 'Poland' );
execute upsert_tx('RO-A01', 'sk', 'Romania' );
execute upsert_tx('HU-A01', 'sk', 'Hungary' );
execute upsert_tx('HR-A01', 'sk', 'Croatia' );
execute upsert_tx('AT-A01', 'sk', 'Austria' );

prompt INSERTING into TLGNR_TRSP_ZONE_TX - HU
execute upsert_tx('BG-A01', 'hu', 'Bulgaria' );
execute upsert_tx('RS-A01', 'hu', 'Serbia' );
execute upsert_tx('SK-A01', 'hu', 'Slovakia' );
execute upsert_tx('PL-A01', 'hu', 'Poland' );
execute upsert_tx('RO-A01', 'hu', 'Romania' );
execute upsert_tx('HU-A01', 'hu', 'Hungary' );
execute upsert_tx('HR-A01', 'hu', 'Croatia' );
execute upsert_tx('AT-A01', 'hu', 'Austria' );

prompt INSERTING into TLGNR_TRSP_ZONE_TX - RO
execute upsert_tx('BG-A01', 'ro', 'Bulgaria' );
execute upsert_tx('RS-A01', 'ro', 'Serbia' );
execute upsert_tx('SK-A01', 'ro', 'Slovakia' );
execute upsert_tx('PL-A01', 'ro', 'Poland' );
execute upsert_tx('RO-A01', 'ro', 'Romania' );
execute upsert_tx('HU-A01', 'ro', 'Hungary' );
execute upsert_tx('HR-A01', 'ro', 'Croatia' );
execute upsert_tx('AT-A01', 'ro', 'Austria' );

prompt INSERTING into TLGNR_TRSP_ZONE_TX - BG
execute upsert_tx('BG-A01', 'bg', 'Bulgaria' );
execute upsert_tx('RS-A01', 'bg', 'Serbia' );
execute upsert_tx('SK-A01', 'bg', 'Slovakia' );
execute upsert_tx('PL-A01', 'bg', 'Poland' );
execute upsert_tx('RO-A01', 'bg', 'Romania' );
execute upsert_tx('HU-A01', 'bg', 'Hungary' );
execute upsert_tx('HR-A01', 'bg', 'Croatia' );
execute upsert_tx('AT-A01', 'bg', 'Austria' );

prompt INSERTING into TLGNR_TRSP_ZONE_TX - RU
execute upsert_tx('BG-A01', 'ru', 'Bulgaria' );
execute upsert_tx('RS-A01', 'ru', 'Serbia' );
execute upsert_tx('SK-A01', 'ru', 'Slovakia' );
execute upsert_tx('PL-A01', 'ru', 'Poland' );
execute upsert_tx('RO-A01', 'ru', 'Romania' );
execute upsert_tx('HU-A01', 'ru', 'Hungary' );
execute upsert_tx('HR-A01', 'ru', 'Croatia' );
execute upsert_tx('AT-A01', 'ru', 'Austria' );

prompt INSERTING into TLGNR_TRSP_ZONE_TX - PL
execute upsert_tx('BG-A01', 'pl', 'Bulgaria' );
execute upsert_tx('RS-A01', 'pl', 'Serbia' );
execute upsert_tx('SK-A01', 'pl', 'Slovakia' );
execute upsert_tx('PL-A01', 'pl', 'Poland' );
execute upsert_tx('RO-A01', 'pl', 'Romania' );
execute upsert_tx('HU-A01', 'pl', 'Hungary' );
execute upsert_tx('HR-A01', 'pl', 'Croatia' );
execute upsert_tx('AT-A01', 'pl', 'Austria' );

prompt INSERTING into TLGNR_TRSP_ZONE_TX - AZ
execute upsert_tx('BG-A01', 'az', 'Bulgaria' );
execute upsert_tx('RS-A01', 'az', 'Serbia' );
execute upsert_tx('SK-A01', 'az', 'Slovakia' );
execute upsert_tx('PL-A01', 'az', 'Poland' );
execute upsert_tx('RO-A01', 'az', 'Romania' );
execute upsert_tx('HU-A01', 'az', 'Hungary' );
execute upsert_tx('HR-A01', 'az', 'Croatia' );
execute upsert_tx('AT-A01', 'az', 'Austria' );

prompt INSERTING into TLGNR_TRSP_ZONE_TX - HR
execute upsert_tx('BG-A01', 'hr', 'Bulgaria' );
execute upsert_tx('RS-A01', 'hr', 'Serbia' );
execute upsert_tx('SK-A01', 'hr', 'Slovakia' );
execute upsert_tx('PL-A01', 'hr', 'Poland' );
execute upsert_tx('RO-A01', 'hr', 'Romania' );
execute upsert_tx('HU-A01', 'hr', 'Hungary' );
execute upsert_tx('HR-A01', 'hr', 'Croatia' );
execute upsert_tx('AT-A01', 'hr', 'Austria' );

prompt INSERTING into TLGNR_TRSP_ZONE_TX - SR
execute upsert_tx('BG-A01', 'sr', 'Bulgaria' );
execute upsert_tx('RS-A01', 'sr', 'Serbia' );
execute upsert_tx('SK-A01', 'sr', 'Slovakia' );
execute upsert_tx('PL-A01', 'sr', 'Poland' );
execute upsert_tx('RO-A01', 'sr', 'Romania' );
execute upsert_tx('HU-A01', 'sr', 'Hungary' );
execute upsert_tx('HR-A01', 'sr', 'Croatia' );
execute upsert_tx('AT-A01', 'sr', 'Austria' );

prompt INSERTING into TLGNR_TRSP_ZONE_TX - TR
execute upsert_tx('BG-A01', 'tr', 'Bulgaria' );
execute upsert_tx('RS-A01', 'tr', 'Serbia' );
execute upsert_tx('SK-A01', 'tr', 'Slovakia' );
execute upsert_tx('PL-A01', 'tr', 'Poland' );
execute upsert_tx('RO-A01', 'tr', 'Romania' );
execute upsert_tx('HU-A01', 'tr', 'Hungary' );
execute upsert_tx('HR-A01', 'tr', 'Croatia' );
execute upsert_tx('AT-A01', 'tr', 'Austria' );

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

