-- $Revision$
-- $Date$
--
-- Logon internal refdata.

prompt '---------- refdata/tlgnr_return_material_usage.sql ----------'

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
-- tlgnr_return_material_usage_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_return_material_usage_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_return_material_usage_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_return_material_usage_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.return_material_usage_cd)
    into v_exists_flag
    from tlgnr_return_material_usage_bs bs
    where bs.return_material_usage_cd = p_return_material_usage_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_return_material_usage_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_return_material_usage_bs set
/* return_material_usage_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          return_material_usage_cd = p_return_material_usage_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_return_material_usage_cd || ').');
      end if;
    else
      insert into tlgnr_return_material_usage_bs (
        return_material_usage_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_return_material_usage_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_return_material_usage_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_return_material_usage_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_return_material_usage_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_return_material_usage_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_return_material_usage_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.return_material_usage_cd) into v_exists_bs_flag
    from tlgnr_return_material_usage_bs bs
    where bs.return_material_usage_cd = p_return_material_usage_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.return_material_usage_cd) into v_exists_flag
      from tlgnr_return_material_usage_tx tx
      where tx.return_material_usage_cd = p_return_material_usage_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_return_material_usage_tx set
/* p_return_material_usage_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          return_material_usage_cd = p_return_material_usage_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_return_material_usage_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_return_material_usage_tx (
          return_material_usage_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_return_material_usage_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_return_material_usage_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_return_material_usage_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_RETURN_MATERIAL_USAGE_BS
execute upsert_bs(0, 1);
execute upsert_bs(1, 1);
execute upsert_bs(2, 1);

prompt INSERTING into TLGNR_RETURN_MATERIAL_USAGE_TX - EN
execute upsert_tx(0, 'en', 'Refilling the vehicle');
execute upsert_tx(1, 'en', 'Put in Storage Location for Mixed Qualities');
execute upsert_tx(2, 'en', 'Rubish');

prompt INSERTING into TLGNR_RETURN_MATERIAL_USAGE_TX - DE
execute upsert_tx(0, 'de', 'Wiederbefüllung des Fahrzeugs');
execute upsert_tx(1, 'de', 'In Lagerort für gemischte Qualität geschrieben');
execute upsert_tx(2, 'de', 'Abfall');

prompt INSERTING into TLGNR_RETURN_MATERIAL_USAGE_TX - FR
execute upsert_tx(0, 'fr', 'Refilling the vehicle');
execute upsert_tx(1, 'fr', 'Put in Storage Location for Mixed Qualities');
execute upsert_tx(2, 'fr', 'Rubish');

prompt INSERTING into TLGNR_RETURN_MATERIAL_USAGE_TX - IT
execute upsert_tx(0, 'it', 'Riempire di nuovo il veicolo');
execute upsert_tx(1, 'it', 'Immagazzinato per qualità mixate');
execute upsert_tx(2, 'it', 'Rifiuto');

prompt INSERTING into TLGNR_RETURN_MATERIAL_USAGE_TX - ES
execute upsert_tx(0, 'es', 'Mantener en el vehículo');
execute upsert_tx(1, 'es', 'Vaciar en el Contenedor de Mezcla de Calidades');
execute upsert_tx(2, 'es', 'Deshecho');

prompt INSERTING into TLGNR_RETURN_MATERIAL_USAGE_TX - CS
execute upsert_tx(0, 'cs', 'Doplnění vozidla');
execute upsert_tx(1, 'cs', 'Vyložení do skladu se smíšenými materiály');
execute upsert_tx(2, 'cs', 'Likvidace materiálu');

prompt INSERTING into TLGNR_RETURN_MATERIAL_USAGE_TX - SK
execute upsert_tx(0, 'sk', 'Napĺňanie vozidla');
execute upsert_tx(1, 'sk', 'Uložené do skladu pre zmiešané kvality');
execute upsert_tx(2, 'sk', 'Odpad');

prompt INSERTING into TLGNR_RETURN_MATERIAL_USAGE_TX - HU
execute upsert_tx(0, 'hu', 'Feltöltött jármű');
execute upsert_tx(1, 'hu', 'Kevert minőségek feltöltése a tárhelyre');
execute upsert_tx(2, 'hu', 'Szemcsés');

prompt INSERTING into TLGNR_RETURN_MATERIAL_USAGE_TX - RO
execute upsert_tx(0, 'ro', 'Reumplere vehicul');
execute upsert_tx(1, 'ro', 'Puneti in Spatiu Depozitare pentru Calitati Mixte');
execute upsert_tx(2, 'ro', 'Gunoi');

prompt INSERTING into TLGNR_RETURN_MATERIAL_USAGE_TX - BG
execute upsert_tx(0, 'bg', 'Зареждане на превозно средство');
execute upsert_tx(1, 'bg', 'Поставяне в място за съхранение на смесени качества');
execute upsert_tx(2, 'bg', 'Боклук');

prompt INSERTING into TLGNR_RETURN_MATERIAL_USAGE_TX - RU (ENGLISH COPY)
execute upsert_tx(0, 'ru', 'заправка автомобиля');
execute upsert_tx(1, 'ru', 'Положить в место хранения для смешанного качества');
execute upsert_tx(2, 'ru', 'мусор');

prompt INSERTING into TLGNR_RETURN_MATERIAL_USAGE_TX - PL
execute upsert_tx(0, 'pl', 'Uzupełnianie pojazdu');
execute upsert_tx(1, 'pl', 'Umieścić w magazynie dla cech mieszanych');
execute upsert_tx(2, 'pl', 'Śmieci');

prompt INSERTING into TLGNR_RETURN_MATERIAL_USAGE_TX - AZ (ENGLISH COPY)
execute upsert_tx(0, 'az', 'Refilling the vehicle');
execute upsert_tx(1, 'az', 'Put in Storage Location for Mixed Qualities');
execute upsert_tx(2, 'az', 'Rubish');

prompt INSERTING into TLGNR_RETURN_MATERIAL_USAGE_TX - HR (ENGLISH COPY)
execute upsert_tx(0, 'hr', 'Punjenje vozila');
execute upsert_tx(1, 'hr', 'Stavite u mjesto pohrane za mješovite kvalitete');
execute upsert_tx(2, 'hr', 'Rubish');

prompt INSERTING into TLGNR_RETURN_MATERIAL_USAGE_TX - SR (ENGLISH COPY)
execute upsert_tx(0, 'sr', 'Punjenje vozila');
execute upsert_tx(1, 'sr', 'Stavite u mesto utovara za mešan kvalitet');
execute upsert_tx(2, 'sr', 'Rubish');

prompt INSERTING into TLGNR_RETURN_MATERIAL_USAGE_TX - TR
execute upsert_tx(0, 'tr', 'Refilling the vehicle');
execute upsert_tx(1, 'tr', 'Put in Storage Location for Mixed Qualities');
execute upsert_tx(2, 'tr', 'Rubish');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

