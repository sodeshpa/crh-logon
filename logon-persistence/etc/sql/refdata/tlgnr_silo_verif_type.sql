-- $Revision$
-- $Date$
--
prompt '---------- refdata/tlgnr_silo_verif_type.sql ----------'

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
-- tlgnr_silo_verif_type_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_silo_verif_type_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_silo_verif_type_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_silo_verif_type_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.silo_verif_type_cd)
    into v_exists_flag
    from tlgnr_silo_verif_type_bs bs
    where bs.silo_verif_type_cd = p_silo_verif_type_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_silo_verif_type_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_silo_verif_type_bs set
/* silo_verif_type_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          silo_verif_type_cd = p_silo_verif_type_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_silo_verif_type_cd || ').');
      end if;
    else
      insert into tlgnr_silo_verif_type_bs (
        silo_verif_type_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_silo_verif_type_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_silo_verif_type_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_silo_verif_type_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_silo_verif_type_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_silo_verif_type_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_silo_verif_type_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.silo_verif_type_cd) into v_exists_bs_flag
    from tlgnr_silo_verif_type_bs bs
    where bs.silo_verif_type_cd = p_silo_verif_type_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.silo_verif_type_cd) into v_exists_flag
      from tlgnr_silo_verif_type_tx tx
      where tx.silo_verif_type_cd = p_silo_verif_type_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_silo_verif_type_tx set
/* p_silo_verif_type_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          silo_verif_type_cd = p_silo_verif_type_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_silo_verif_type_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_silo_verif_type_tx (
          silo_verif_type_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_silo_verif_type_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_silo_verif_type_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_silo_verif_type_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_SILO_VERIF_TYPE_BS

execute upsert_bs(1, 1);
execute upsert_bs(2, 1);
execute upsert_bs(3, 1);
execute upsert_bs(4, 1);

prompt INSERTING into TLGNR_SILO_VERIF_TYPE_TX - EN
execute upsert_tx(1, 'en', 'Not verified');
execute upsert_tx(2, 'en', 'RFID scan');
execute upsert_tx(3, 'en', 'RFID name');
execute upsert_tx(4, 'en', 'RFID label');

prompt INSERTING into TLGNR_SILO_VERIF_TYPE_TX - DE
execute upsert_tx(1, 'de', 'Nicht geprüft');
execute upsert_tx(2, 'de', 'RFID Scan');
execute upsert_tx(3, 'de', 'RFID Name');
execute upsert_tx(4, 'de', 'RFID label');

prompt INSERTING into TLGNR_SILO_VERIF_TYPE_TX - FR
execute upsert_tx(1, 'fr', 'Not verified');
execute upsert_tx(2, 'fr', 'RFID scan');
execute upsert_tx(3, 'fr', 'RFID name');
execute upsert_tx(4, 'fr', 'RFID label');

prompt INSERTING into TLGNR_SILO_VERIF_TYPE_TX - IT
execute upsert_tx(1, 'it', 'Non verificato');
execute upsert_tx(2, 'it', 'Scan RFID');
execute upsert_tx(3, 'it', 'Nome RFID');
execute upsert_tx(4, 'it', 'Etichetta RFID');

prompt INSERTING into TLGNR_SILO_VERIF_TYPE_TX - ES
execute upsert_tx(1, 'es', 'Sin revisar');
execute upsert_tx(2, 'es', 'Escanear RFID');
execute upsert_tx(3, 'es', 'Nombre RFID');
execute upsert_tx(4, 'es', 'Etiqueta RFID');

prompt INSERTING into TLGNR_SILO_VERIF_TYPE_TX - CS
execute upsert_tx(1, 'cs', 'Nepotvrzeno');
execute upsert_tx(2, 'cs', 'RFID scan');
execute upsert_tx(3, 'cs', 'RFID jméno');
execute upsert_tx(4, 'cs', 'RFID nálepka');

prompt INSERTING into TLGNR_SILO_VERIF_TYPE_TX - SK
execute upsert_tx(1, 'sk', 'Neoverené');
execute upsert_tx(2, 'sk', 'RFID skenované');
execute upsert_tx(3, 'sk', 'RFID meno');
execute upsert_tx(4, 'sk', 'RFID označenie');

prompt INSERTING into TLGNR_SILO_VERIF_TYPE_TX - HU
execute upsert_tx(1, 'hu', 'Nem ellenőrzött');
execute upsert_tx(2, 'hu', 'RFID szkennelés');
execute upsert_tx(3, 'hu', 'RFID név');
execute upsert_tx(4, 'hu', 'RFID címke');

prompt INSERTING into TLGNR_SILO_VERIF_TYPE_TX - RO
execute upsert_tx(1, 'ro', 'Neverificat');
execute upsert_tx(2, 'ro', 'Scanare RFID');
execute upsert_tx(3, 'ro', 'Nume RFID');
execute upsert_tx(4, 'ro', 'Eticheta RFID');

prompt INSERTING into TLGNR_SILO_VERIF_TYPE_TX - BG
execute upsert_tx(1, 'bg', 'Не е проверен');
execute upsert_tx(2, 'bg', 'RFID сканиране');
execute upsert_tx(3, 'bg', 'RFID име');
execute upsert_tx(4, 'bg', 'RFID етикет');

prompt INSERTING into TLGNR_SILO_VERIF_TYPE_TX - RU
execute upsert_tx(1, 'ru', 'не проверен');
execute upsert_tx(2, 'ru', 'сканирование карты');
execute upsert_tx(3, 'ru', 'имя карты');
execute upsert_tx(4, 'ru', 'этикетка карты');

prompt INSERTING into TLGNR_SILO_VERIF_TYPE_TX - PL
execute upsert_tx(1, 'pl', 'Nie zweryfikowano');
execute upsert_tx(2, 'pl', 'Skanowanie RFID');
execute upsert_tx(3, 'pl', 'Nazwa RFID');
execute upsert_tx(4, 'pl', 'Etykieta RFID');

prompt INSERTING into TLGNR_SILO_VERIF_TYPE_TX - AZ
execute upsert_tx(1, 'az', 'Not verified');
execute upsert_tx(2, 'az', 'RFID scan');
execute upsert_tx(3, 'az', 'RFID name');
execute upsert_tx(4, 'az', 'RFID label');

prompt INSERTING into TLGNR_SILO_VERIF_TYPE_TX - HR
execute upsert_tx(1, 'hr', 'Nije potvrđeno');
execute upsert_tx(2, 'hr', 'RFID skeniranje');
execute upsert_tx(3, 'hr', 'RFID ime');
execute upsert_tx(4, 'hr', 'RFID oznaka');

prompt INSERTING into TLGNR_SILO_VERIF_TYPE_TX - SR
execute upsert_tx(1, 'sr', 'Nije verifikovano');
execute upsert_tx(2, 'sr', 'RFID sken');
execute upsert_tx(3, 'sr', 'RFID ime');
execute upsert_tx(4, 'sr', 'RFID oznaka');

prompt INSERTING into TLGNR_SILO_VERIF_TYPE_TX - TR
execute upsert_tx(1, 'tr', 'Not verified');
execute upsert_tx(2, 'tr', 'RFID scan');
execute upsert_tx(3, 'tr', 'RFID name');
execute upsert_tx(4, 'tr', 'RFID label');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

