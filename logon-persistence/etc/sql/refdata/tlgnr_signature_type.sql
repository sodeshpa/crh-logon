-- $Revision$
-- $Date$
--
--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_signature_type.sql ----------'

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
-- tlgnr_signature_type_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_signature_type_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_signature_type_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_signature_type_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.signature_type_cd)
    into v_exists_flag
    from tlgnr_signature_type_bs bs
    where bs.signature_type_cd = p_signature_type_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_signature_type_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_signature_type_bs set
/* signature_type_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          signature_type_cd = p_signature_type_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_signature_type_cd || ').');
      end if;
    else
      insert into tlgnr_signature_type_bs (
        signature_type_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_signature_type_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_signature_type_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_signature_type_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_signature_type_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_signature_type_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_signature_type_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.signature_type_cd) into v_exists_bs_flag
    from tlgnr_signature_type_bs bs
    where bs.signature_type_cd = p_signature_type_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.signature_type_cd) into v_exists_flag
      from tlgnr_signature_type_tx tx
      where tx.signature_type_cd = p_signature_type_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_signature_type_tx set
/* p_signature_type_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          signature_type_cd = p_signature_type_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_signature_type_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_signature_type_tx (
          signature_type_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_signature_type_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_signature_type_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_signature_type_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_SIGNATURE_TYPE_BS
execute upsert_bs(0, 1);
execute upsert_bs(1, 1);
execute upsert_bs(2, 1);

prompt INSERTING into TLGNR_SIGNATURE_TYPE_TX - EN
execute upsert_tx(0, 'en', 'mandatory'          );
execute upsert_tx(1, 'en', 'optional'           );
execute upsert_tx(2, 'en', 'no signature'       );

prompt INSERTING into TLGNR_SIGNATURE_TYPE_TX - DE
execute upsert_tx(0, 'de', 'obligatorisch'      );
execute upsert_tx(1, 'de', 'optional'           );
execute upsert_tx(2, 'de', 'keine Unterschrift' );

prompt INSERTING into TLGNR_SIGNATURE_TYPE_TX - FR
execute upsert_tx(0, 'fr', 'obligatoire'         );
execute upsert_tx(1, 'fr', 'optionnel'           );
execute upsert_tx(2, 'fr', 'sans signature'      );

prompt INSERTING into TLGNR_SIGNATURE_TYPE_TX - IT
execute upsert_tx(0, 'it', 'obbligatorio'      );
execute upsert_tx(1, 'it', 'opzionale'         );
execute upsert_tx(2, 'it', 'senza firma'       );

prompt INSERTING into TLGNR_SIGNATURE_TYPE_TX - ES
execute upsert_tx(0, 'es', 'obligatorio'        );
execute upsert_tx(1, 'es', 'opcional'           );
execute upsert_tx(2, 'es', 'sin firma'          );

prompt INSERTING into TLGNR_SIGNATURE_TYPE_TX - CS
execute upsert_tx(0, 'cs', 'povinné'          );
execute upsert_tx(1, 'cs', 'volitelné'           );
execute upsert_tx(2, 'cs', 'bez podpisu'       );

prompt INSERTING into TLGNR_SIGNATURE_TYPE_TX - SK
execute upsert_tx(0, 'sk', 'povinný'          );
execute upsert_tx(1, 'sk', 'dobrovoľný'           );
execute upsert_tx(2, 'sk', 'bez podpisu'       );

prompt INSERTING into TLGNR_SIGNATURE_TYPE_TX - HU
execute upsert_tx(0, 'hu', 'Kötelező'          );
execute upsert_tx(1, 'hu', 'Választható'           );
execute upsert_tx(2, 'hu', 'Nincs aláírás'       );

prompt INSERTING into TLGNR_SIGNATURE_TYPE_TX - RO
execute upsert_tx(0, 'ro', 'obligatoriu'          );
execute upsert_tx(1, 'ro', 'optional'           );
execute upsert_tx(2, 'ro', 'fara semnatura'       );

prompt INSERTING into TLGNR_SIGNATURE_TYPE_TX - BG
execute upsert_tx(0, 'bg', 'Задължителен'          );
execute upsert_tx(1, 'bg', 'По избор'           );
execute upsert_tx(2, 'bg', 'Без подпис'       );

prompt INSERTING into TLGNR_SIGNATURE_TYPE_TX - RU (ENGLISH COPY)
execute upsert_tx(0, 'ru', 'обязательный'          );
execute upsert_tx(1, 'ru', 'не обязательный'           );
execute upsert_tx(2, 'ru', 'не требуемый'       );

prompt INSERTING into TLGNR_SIGNATURE_TYPE_TX - PL
execute upsert_tx(0, 'pl', 'obowiązkowe'          );
execute upsert_tx(1, 'pl', 'opcjonalne'           );
execute upsert_tx(2, 'pl', 'bez podpisu'       );

prompt INSERTING into TLGNR_SIGNATURE_TYPE_TX - AZ (ENGLISH COPY)
execute upsert_tx(0, 'az', 'mandatory'          );
execute upsert_tx(1, 'az', 'optional'           );
execute upsert_tx(2, 'az', 'no signature'       );

prompt INSERTING into TLGNR_SIGNATURE_TYPE_TX - HR
execute upsert_tx(0, 'hr', 'obavezan'          );
execute upsert_tx(1, 'hr', 'izborni'           );
execute upsert_tx(2, 'hr', 'nema potpisa'       );

prompt INSERTING into TLGNR_SIGNATURE_TYPE_TX - SR
execute upsert_tx(0, 'sr', 'obavezno'          );
execute upsert_tx(1, 'sr', 'opciono'           );
execute upsert_tx(2, 'sr', 'bez potpisa'       );

prompt INSERTING into TLGNR_SIGNATURE_TYPE_TX - TR
execute upsert_tx(0, 'tr', 'mandatory'          );
execute upsert_tx(1, 'tr', 'optional'           );
execute upsert_tx(2, 'tr', 'no signature'       );

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

