-- $Revision$
-- $Date$
--
-- Add new Status AT THE END, always
-- Reason:
-- - Do not break existing codes (constants)
-- - Do not break existing views (there, no constants are in use!!!)
--
-- State comparison with < and > in the workflow is forbidden anyway (new status codes may be added in the future)
-- In the UI, attribute sorting_no can be used to control the appearence in a drop down field

--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_custorder_status.sql ----------'

prompt INSERTING into TLGNR_CUSTORDER_STATUS_BS

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
-- tlgnr_custorder_status_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_custorder_status_cd     in number,
  p_sorting_no              in number,
  p_ctl_act_cd              in number)
is
  v_table_name    varchar2(48) := 'tlgnr_custorder_status_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_custorder_status_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.custorder_status_cd)
    into v_exists_flag
    from tlgnr_custorder_status_bs bs
    where bs.custorder_status_cd = p_custorder_status_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_custorder_status_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_custorder_status_bs set
/* custorder_status_cd: cannot update because this is the key */
          sorting_no                     = p_sorting_no,
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          custorder_status_cd = p_custorder_status_cd
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_custorder_status_cd || ').');
      end if;
    else
      insert into tlgnr_custorder_status_bs (
        custorder_status_cd,
        sorting_no,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_custorder_status_cd,
        p_sorting_no,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_custorder_status_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_custorder_status_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_custorder_status_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_custorder_status_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_custorder_status_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.custorder_status_cd) into v_exists_bs_flag
    from tlgnr_custorder_status_bs bs
    where bs.custorder_status_cd = p_custorder_status_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.custorder_status_cd) into v_exists_flag
      from tlgnr_custorder_status_tx tx
      where tx.custorder_status_cd = p_custorder_status_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_custorder_status_tx set
/* p_custorder_status_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          custorder_status_cd = p_custorder_status_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_custorder_status_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_custorder_status_tx (
          custorder_status_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_custorder_status_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_custorder_status_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_custorder_status_cd || ').');
    end if;

  end upsert_tx;
/
show errors;


execute upsert_bs(1,  10, 1);
execute upsert_bs(2,  20, 1);
execute upsert_bs(3,  30, 1);

prompt INSERTING into TLGNR_CUSTORDER_STATUS_TX - EN
execute upsert_tx( 1, 'en', 'New'           );
execute upsert_tx( 2, 'en', 'Open'          );
execute upsert_tx( 3, 'en', 'Finished'      );

prompt INSERTING into TLGNR_CUSTORDER_STATUS_TX - DE
execute upsert_tx( 1, 'de', 'Neu'           );
execute upsert_tx( 2, 'de', 'Offen'         );
execute upsert_tx( 3, 'de', 'Abgeschlossen' );

prompt INSERTING into TLGNR_CUSTORDER_STATUS_TX - FR
execute upsert_tx( 1, 'fr', 'Nouveau'       );
execute upsert_tx( 2, 'fr', 'Ouvert'        );
execute upsert_tx( 3, 'fr', 'Terminé'       );

prompt INSERTING into TLGNR_CUSTORDER_STATUS_TX - IT
execute upsert_tx( 1, 'it', 'Nuovo'         );
execute upsert_tx( 2, 'it', 'Aperto'        );
execute upsert_tx( 3, 'it', 'Terminato'     );

prompt INSERTING into TLGNR_CUSTORDER_STATUS_TX - ES
execute upsert_tx( 1, 'es', 'Nuevo'           );
execute upsert_tx( 2, 'es', 'Abierto'          );
execute upsert_tx( 3, 'es', 'Finalizado'      );

prompt INSERTING into TLGNR_CUSTORDER_STATUS_TX - CS
execute upsert_tx( 1, 'cs', 'Nový'           );
execute upsert_tx( 2, 'cs', 'Otevřeno'          );
execute upsert_tx( 3, 'cs', 'Ukončeno'      );

prompt INSERTING into TLGNR_CUSTORDER_STATUS_TX - SK
execute upsert_tx( 1, 'sk', 'Nový'           );
execute upsert_tx( 2, 'sk', 'Otvorené'          );
execute upsert_tx( 3, 'sk', 'Ukončené'      );

prompt INSERTING into TLGNR_CUSTORDER_STATUS_TX - HU
execute upsert_tx( 1, 'hu', 'Új'           );
execute upsert_tx( 2, 'hu', 'Nyitott'          );
execute upsert_tx( 3, 'hu', 'Befejezett'      );

prompt INSERTING into TLGNR_CUSTORDER_STATUS_TX - RO
execute upsert_tx( 1, 'ro', 'Nou'           );
execute upsert_tx( 2, 'ro', 'Deschide'          );
execute upsert_tx( 3, 'ro', 'Terminat'      );

prompt INSERTING into TLGNR_CUSTORDER_STATUS_TX - BG
execute upsert_tx( 1, 'bg', 'Нов'           );
execute upsert_tx( 2, 'bg', 'Отворен'          );
execute upsert_tx( 3, 'bg', 'Приключен'      );

prompt INSERTING into TLGNR_CUSTORDER_STATUS_TX - RU
execute upsert_tx( 1, 'ru', 'новый'           );
execute upsert_tx( 2, 'ru', 'открыть'          );
execute upsert_tx( 3, 'ru', 'окончание'      );

prompt INSERTING into TLGNR_CUSTORDER_STATUS_TX - PL
execute upsert_tx( 1, 'pl', 'Nowy'           );
execute upsert_tx( 2, 'pl', 'Otwarty'          );
execute upsert_tx( 3, 'pl', 'Zakończony'      );

prompt INSERTING into TLGNR_CUSTORDER_STATUS_TX - AZ
execute upsert_tx( 1, 'az', 'Yeni'           );
execute upsert_tx( 2, 'az', 'Açıq'          );
execute upsert_tx( 3, 'az', 'Başa çatıb'      );

prompt INSERTING into TLGNR_CUSTORDER_STATUS_TX - HR
execute upsert_tx( 1, 'hr', 'Novi'           );
execute upsert_tx( 2, 'hr', 'Otvoreno'          );
execute upsert_tx( 3, 'hr', 'Gotov'      );

prompt INSERTING into TLGNR_CUSTORDER_STATUS_TX - SR
execute upsert_tx( 1, 'sr', 'Novi'           );
execute upsert_tx( 2, 'sr', 'Otvoriti'          );
execute upsert_tx( 3, 'sr', 'Realizovano'      );

prompt INSERTING into TLGNR_CUSTORDER_STATUS_TX - TR
execute upsert_tx( 1, 'tr', 'New'           );
execute upsert_tx( 2, 'tr', 'Open'          );
execute upsert_tx( 3, 'tr', 'Finished'      );

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;
