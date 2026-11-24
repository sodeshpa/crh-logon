-- $Revision$
-- $Date$
--
-- List as received from Crh via E-Mail, including English translations.

-- 2.9.2009, Removed "Container"
-- Clarifications from Crh/Christoph in E-mail "Container Questions, Transport Categories and Types again",
-- why "Container" is NOT a trailer type, but just a trailer_transport_category:
--
-- Q: It seems like "tiltable" AND "Container" would be possible at the same time?
-- A: Yes, that is inconsistent. As we have already the trailer_CATEGORY "container",
--    for trailer_TYPE "container" is not required
-- -> can be removed (we use only tiltable and horizontal)

-- 3.9.2009 - Removed "Mehrkammersilo" ("Multi chamber Silo")
-- It is redundant to trailer.NR_CHAMBERS (similar to truck.NR_CHAMBERS):
-- Christoph E-mIL 3.9.:
-- I assume "number of chambers" for trucks are visible on the master data screen of truck and trailer as a additional separate field (visible for user).
-- If yes, I agree to remove "Mehrkammersilo" according your proposition. Looks for me much nicer and cleaner. Also to have it consistent with container_type.

--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_trailer_type.sql ----------'

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
-- tlgnr_trailer_type_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_trailer_type_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_trailer_type_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_trailer_type_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.trailer_type_cd)
    into v_exists_flag
    from tlgnr_trailer_type_bs bs
    where bs.trailer_type_cd = p_trailer_type_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_trailer_type_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_trailer_type_bs set
/* trailer_type_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          trailer_type_cd = p_trailer_type_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_trailer_type_cd || ').');
      end if;
    else
      insert into tlgnr_trailer_type_bs (
        trailer_type_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_trailer_type_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_trailer_type_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_trailer_type_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_trailer_type_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_trailer_type_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_trailer_type_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.trailer_type_cd) into v_exists_bs_flag
    from tlgnr_trailer_type_bs bs
    where bs.trailer_type_cd = p_trailer_type_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.trailer_type_cd) into v_exists_flag
      from tlgnr_trailer_type_tx tx
      where tx.trailer_type_cd = p_trailer_type_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_trailer_type_tx set
/* p_trailer_type_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          trailer_type_cd = p_trailer_type_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_trailer_type_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_trailer_type_tx (
          trailer_type_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_trailer_type_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_trailer_type_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_trailer_type_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_TRAILER_TYPE_BS
execute upsert_bs(1, 1);
execute upsert_bs(2, 1);
-- "Container":      removed 2.9.2009
-- "Mehrkammersilo": removed 2.9.2009

prompt INSERTING into TLGNR_TRAILER_TYPE_TX - EN
execute upsert_tx(1, 'en', 'Horizontal'         );
execute upsert_tx(2, 'en', 'Tiltable'           );

prompt INSERTING into TLGNR_TRAILER_TYPE_TX - DE
execute upsert_tx(1, 'de', 'Liegend'            );
execute upsert_tx(2, 'de', 'Kippbar'            );

prompt INSERTING into TLGNR_TRAILER_TYPE_TX - FR
execute upsert_tx(1, 'fr', 'Horizontal'         );
execute upsert_tx(2, 'fr', 'Basculant'          );

prompt INSERTING into TLGNR_TRAILER_TYPE_TX - IT
execute upsert_tx(1, 'it', 'Orizzontale'        );
execute upsert_tx(2, 'it', 'Ribaltabile'         );

prompt INSERTING into TLGNR_TRAILER_TYPE_TX - ES
execute upsert_tx(1, 'es', 'Horizontal'         );
execute upsert_tx(2, 'es', 'Basculante'         );

prompt INSERTING into TLGNR_TRAILER_TYPE_TX - CS
execute upsert_tx(1, 'cs', 'Plato'         );
execute upsert_tx(2, 'cs', 'Sklápěcí'           );

prompt INSERTING into TLGNR_TRAILER_TYPE_TX - SK
execute upsert_tx(1, 'sk', 'Horizontálne'         );
execute upsert_tx(2, 'sk', 'Sklápateľné'           );

prompt INSERTING into TLGNR_TRAILER_TYPE_TX - HU
execute upsert_tx(1, 'hu', 'Vízszintes'         );
execute upsert_tx(2, 'hu', 'Dönthető/billenthető'           );

prompt INSERTING into TLGNR_TRAILER_TYPE_TX - RO
execute upsert_tx(1, 'ro', 'Orizontal'         );
execute upsert_tx(2, 'ro', 'Inclinat'           );

prompt INSERTING into TLGNR_TRAILER_TYPE_TX - BG
execute upsert_tx(1, 'bg', 'Хоризонтален'         );
execute upsert_tx(2, 'bg', 'Регулируем по височи'           );

prompt INSERTING into TLGNR_TRAILER_TYPE_TX - RU (ENGLISH COPY)
execute upsert_tx(1, 'ru', 'горизонтальный'         );
execute upsert_tx(2, 'ru', 'наклонный'           );

prompt INSERTING into TLGNR_TRAILER_TYPE_TX - PL
execute upsert_tx(1, 'pl', 'Poziomy'         );
execute upsert_tx(2, 'pl', 'Odchylony'           );

prompt INSERTING into TLGNR_TRAILER_TYPE_TX - AZ (ENGLISH COPY)
execute upsert_tx(1, 'az', 'Horizontal'         );
execute upsert_tx(2, 'az', 'Tiltable'           );

prompt INSERTING into TLGNR_TRAILER_TYPE_TX - HR
execute upsert_tx(1, 'hr', 'Vodoravan'         );
execute upsert_tx(2, 'hr', 'Ukosljivi'           );

prompt INSERTING into TLGNR_TRAILER_TYPE_TX - SR
execute upsert_tx(1, 'sr', 'Horizontalan'         );
execute upsert_tx(2, 'sr', 'Zakošen'           );

prompt INSERTING into TLGNR_TRAILER_TYPE_TX - TR
execute upsert_tx(1, 'tr', 'Horizontal'         );
execute upsert_tx(2, 'tr', 'Tiltable'           );

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

