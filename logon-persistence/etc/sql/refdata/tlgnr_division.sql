-- $Revision$
-- $Date$
--
-- This is an internal, technical table:
-- - No translations needed
-- - Extending the possible values requires code changes

--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_division.sql ----------'

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
-- tlgnr_division_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_division_cd               in varchar2,
  p_segment_code              in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_division_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_division_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.division_cd)
    into v_exists_flag
    from tlgnr_division_bs bs
    where bs.division_cd = p_division_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_division_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_division_bs set
/* division_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          segment_code                   = p_segment_code,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          division_cd = p_division_cd
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_division_cd || ').');
      end if;
    else
      insert into tlgnr_division_bs (
        division_cd,
        segment_code,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_division_cd,
        p_segment_code,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_division_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_division_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_division_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_division_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_division_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.division_cd) into v_exists_bs_flag
    from tlgnr_division_bs bs
    where bs.division_cd = p_division_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.division_cd) into v_exists_flag
      from tlgnr_division_tx tx
      where tx.division_cd = p_division_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_division_tx set
/* p_division_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          division_cd = p_division_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_division_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_division_tx (
          division_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_division_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_division_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_division_cd || ').');
    end if;

  end upsert_tx;
/
show errors;


prompt INSERTING into TLGNR_DIVISION_BS
execute upsert_bs('10', 'C', 1);
execute upsert_bs('11', 'A', 1);
execute upsert_bs('20', 'C', 1);
execute upsert_bs('30', 'G', 1);
execute upsert_bs('40', 'R', 1);
execute upsert_bs('50', 'M', 1);
execute upsert_bs('60', 'X', 1);
execute upsert_bs('70', 'X', 1);
execute upsert_bs('80', 'T', 1);
execute upsert_bs('90', 'O', 1);
execute upsert_bs('99', 'X', 1);

--these divisions (12,81) were present in a CH db, therefore we need to have them
execute upsert_bs('12', 'X', 1);
execute upsert_bs('81', 'X', 1);

set define off;
prompt INSERTING into TLGNR_DIVISION_TX - EN
--these divisions (12,81) were present in a CH db, therefore we need to have them
execute upsert_tx('10', 'en', 'Clinker & Cement');
execute upsert_tx('11', 'en', 'AFR');
execute upsert_tx('12', 'en', '12 (unknown CH)');
execute upsert_tx('20', 'en', 'MIC');
execute upsert_tx('30', 'en', 'Aggregates');
execute upsert_tx('40', 'en', 'RMX Concrete');
execute upsert_tx('50', 'en', 'Concrete products');
execute upsert_tx('60', 'en', 'Asphalt');
execute upsert_tx('70', 'en', 'Construction and pav');
execute upsert_tx('80', 'en', 'Trading');
execute upsert_tx('81', 'en', '81 (unknown CH)');
execute upsert_tx('90', 'en', 'Other Produc.&Serv.');
execute upsert_tx('99', 'en', 'Corporate');

prompt INSERTING into TLGNR_DIVISION_TX - DE (ENGLISH COPIED)
execute upsert_tx('10', 'de', 'Clinker & Cement');
execute upsert_tx('11', 'de', 'AFR');
execute upsert_tx('12', 'de', '12 (unknown CH)');
execute upsert_tx('20', 'de', 'MIC');
execute upsert_tx('30', 'de', 'Aggregates');
execute upsert_tx('40', 'de', 'Concrete');
execute upsert_tx('50', 'de', 'Concrete products');
execute upsert_tx('60', 'de', 'Asphalt');
execute upsert_tx('70', 'de', 'Construction and pav');
execute upsert_tx('80', 'de', 'Trading');
execute upsert_tx('81', 'de', '81 (unknown CH)');
execute upsert_tx('90', 'de', 'Other Produc.&Serv.');
execute upsert_tx('99', 'de', 'Corporate');

prompt INSERTING into TLGNR_DIVISION_TX - FR
execute upsert_tx('10', 'fr', 'Clinker & Ciment');
execute upsert_tx('11', 'fr', 'AFR');
execute upsert_tx('12', 'fr', '12 (unknown CH)');
execute upsert_tx('20', 'fr', 'MIC');
execute upsert_tx('30', 'fr', 'Granulats');
execute upsert_tx('40', 'fr', 'Béton frais');
execute upsert_tx('50', 'fr', 'Produits de béton');
execute upsert_tx('60', 'fr', 'Asphalte');
execute upsert_tx('70', 'fr', 'Construction et pav');
execute upsert_tx('80', 'fr', 'Commerce');
execute upsert_tx('81', 'fr', '81 (unknown CH)');
execute upsert_tx('90', 'fr', 'Autres Prod.&Serv.');
execute upsert_tx('99', 'fr', 'Compagnie');

prompt INSERTING into TLGNR_DIVISION_TX - IT
execute upsert_tx('10', 'it', 'Clinker & Cemento');
execute upsert_tx('11', 'it', 'AFR');
execute upsert_tx('12', 'it', '12 (unknown CH)');
execute upsert_tx('20', 'it', 'MIC');
execute upsert_tx('30', 'it', 'Aggregati');
execute upsert_tx('40', 'it', 'Calcestruzzo');
execute upsert_tx('50', 'it', 'Prodotti per calcestruzzo');
execute upsert_tx('60', 'it', 'Asfalto');
execute upsert_tx('70', 'it', 'Costruzione e pav');
execute upsert_tx('80', 'it', 'Commercio');
execute upsert_tx('81', 'it', '81 (unknown CH)');
execute upsert_tx('90', 'it', 'Altri Prod.&Serv.');
execute upsert_tx('99', 'it', 'Corporativo');

prompt INSERTING into TLGNR_DIVISION_TX - ES
execute upsert_tx('10', 'es', 'Clínker & Cemento');
execute upsert_tx('11', 'es', 'AFR');
execute upsert_tx('12', 'es', '12 (unknown CH)');
execute upsert_tx('20', 'es', 'MIC');
execute upsert_tx('30', 'es', 'Áridos');
execute upsert_tx('40', 'es', 'RMX hormigón');
execute upsert_tx('50', 'es', 'Materiales de hormigón');
execute upsert_tx('60', 'es', 'Asfalto');
execute upsert_tx('70', 'es', 'Construcción y pav');
execute upsert_tx('80', 'es', 'Comercio');
execute upsert_tx('81', 'es', '81 (unknown CH)');
execute upsert_tx('90', 'es', 'Otros Produc.&Serv.');
execute upsert_tx('99', 'es', 'Corporativo');

prompt INSERTING into TLGNR_DIVISION_TX - CS (ENGLISH COPIED)
execute upsert_tx('10', 'cs', 'Slinek & Cement');
execute upsert_tx('11', 'cs', 'AFR');
execute upsert_tx('12', 'cs', '12 (unknown CH)');
execute upsert_tx('20', 'cs', 'MIC');
execute upsert_tx('30', 'cs', 'Štěrky');
execute upsert_tx('40', 'cs', 'Beton');
execute upsert_tx('50', 'cs', 'Betonové výrobky');
execute upsert_tx('60', 'cs', 'Asfalt');
execute upsert_tx('70', 'cs', 'Stavby a vozovky');
execute upsert_tx('80', 'cs', 'Obchod');
execute upsert_tx('81', 'cs', '81 (unknown CH)');
execute upsert_tx('90', 'cs', 'Ostatní zboží a služby');
execute upsert_tx('99', 'cs', 'Korporační');

prompt INSERTING into TLGNR_DIVISION_TX - SK (ENGLISH COPIED)
execute upsert_tx('10', 'sk', 'Slinok & Cement');
execute upsert_tx('11', 'sk', 'AFR');
execute upsert_tx('12', 'sk', '12 (unknown CH)');
execute upsert_tx('20', 'sk', 'MIC');
execute upsert_tx('30', 'sk', 'Kamenivo');
execute upsert_tx('40', 'sk', 'Betón');
execute upsert_tx('50', 'sk', 'Betónové výrobky');
execute upsert_tx('60', 'sk', 'Asfalt');
execute upsert_tx('70', 'sk', 'Stavby a dlažba');
execute upsert_tx('80', 'sk', 'Obchodovanie');
execute upsert_tx('81', 'sk', '81 (unknown CH)');
execute upsert_tx('90', 'sk', 'Iné výrobky a služby');
execute upsert_tx('99', 'sk', 'Korporačné');

prompt INSERTING into TLGNR_DIVISION_TX - HU (ENGLISH COPIED)
execute upsert_tx('10', 'hu', 'Klinker & cement');
execute upsert_tx('11', 'hu', 'AFR');
execute upsert_tx('12', 'hu', '12 (unknown CH)');
execute upsert_tx('20', 'hu', 'MIC');
execute upsert_tx('30', 'hu', 'Aggregátumok');
execute upsert_tx('40', 'hu', 'Beton');
execute upsert_tx('50', 'hu', 'Beton termékek');
execute upsert_tx('60', 'hu', 'Aszfalt');
execute upsert_tx('70', 'hu', 'Építés és kövezet (járda)');
execute upsert_tx('80', 'hu', 'Kereskedés');
execute upsert_tx('81', 'hu', '81 (unknown CH)');
execute upsert_tx('90', 'hu', 'Egyéb termék és szolgáltatás');
execute upsert_tx('99', 'hu', 'Vállalati');

prompt INSERTING into TLGNR_DIVISION_TX - RO (ENGLISH COPIED)
execute upsert_tx('10', 'ro', 'Clinker & Ciment');
execute upsert_tx('11', 'ro', 'AFR');
execute upsert_tx('12', 'ro', '12 (unknown CH)');
execute upsert_tx('20', 'ro', 'MIC');
execute upsert_tx('30', 'ro', 'Agregate');
execute upsert_tx('40', 'ro', 'Betoane');
execute upsert_tx('50', 'ro', 'Produse ciment');
execute upsert_tx('60', 'ro', 'Asfalt');
execute upsert_tx('70', 'ro', 'Constructii si pavaj');
execute upsert_tx('80', 'ro', 'Comert');
execute upsert_tx('81', 'ro', '81 (unknown CH)');
execute upsert_tx('90', 'ro', 'Alte produse si servicii');
execute upsert_tx('99', 'ro', 'Persoane juridice');

prompt INSERTING into TLGNR_DIVISION_TX - BG (ENGLISH COPIED)
execute upsert_tx('10', 'bg', 'Клинкер и цимент');
execute upsert_tx('11', 'bg', 'AFR');
execute upsert_tx('12', 'bg', '12 (unknown CH)');
execute upsert_tx('20', 'bg', 'MIC');
execute upsert_tx('30', 'bg', 'Кариерни материали');
execute upsert_tx('40', 'bg', 'Бетон');
execute upsert_tx('50', 'bg', 'Бетонни изделия');
execute upsert_tx('60', 'bg', 'Асфалт');
execute upsert_tx('70', 'bg', 'Строителство и настилка');
execute upsert_tx('80', 'bg', 'Търговия');
execute upsert_tx('81', 'bg', '81 (unknown CH)');
execute upsert_tx('90', 'bg', 'Други продукти и услуги');
execute upsert_tx('99', 'bg', 'Корпоративен');

prompt INSERTING into TLGNR_DIVISION_TX - RU (ENGLISH COPY)
execute upsert_tx('10', 'ru', 'Клинкер & Цемент');
execute upsert_tx('11', 'ru', 'AFR');
execute upsert_tx('12', 'ru', '12 (unknown CH)');
execute upsert_tx('20', 'ru', 'MIC');
execute upsert_tx('30', 'ru', 'Заполнители');
execute upsert_tx('40', 'ru', 'RMX бетон');
execute upsert_tx('50', 'ru', 'железобетонные изделия');
execute upsert_tx('60', 'ru', 'асфальт');
execute upsert_tx('70', 'ru', 'Строительство и тротуар');
execute upsert_tx('80', 'ru', 'торговый');
execute upsert_tx('81', 'ru', '81 (unknown CH)');
execute upsert_tx('90', 'ru', 'другие товары и услуги');
execute upsert_tx('99', 'ru', 'корпоративный');

prompt INSERTING into TLGNR_DIVISION_TX - PL (ENGLISH COPY)
execute upsert_tx('10', 'pl', 'Klinkier & cement');
execute upsert_tx('11', 'pl', 'AFR');
execute upsert_tx('12', 'pl', '12 (unknown CH)');
execute upsert_tx('20', 'pl', 'MIC');
execute upsert_tx('30', 'pl', 'Kruszywa');
execute upsert_tx('40', 'pl', 'RMX Beton');
execute upsert_tx('50', 'pl', 'Produkty betonowe');
execute upsert_tx('60', 'pl', 'Asfalt');
execute upsert_tx('70', 'pl', 'Konstrukcja i bruk');
execute upsert_tx('80', 'pl', 'Handel');
execute upsert_tx('81', 'pl', '81 (unknown CH)');
execute upsert_tx('90', 'pl', 'Inne produkty i usługi');
execute upsert_tx('99', 'pl', 'Zbiorowy');

prompt INSERTING into TLGNR_DIVISION_TX - AZ (ENGLISH COPY)
execute upsert_tx('10', 'az', 'Klinker və sement');
execute upsert_tx('11', 'az', 'AFR');
execute upsert_tx('12', 'az', '12 (unknown CH)');
execute upsert_tx('20', 'az', 'MIC');
execute upsert_tx('30', 'az', 'Aqreqatlar');
execute upsert_tx('40', 'az', 'RMX betonu');
execute upsert_tx('50', 'az', 'Beton məhsulları');
execute upsert_tx('60', 'az', 'Asfalt');
execute upsert_tx('70', 'az', 'Tikinti və pav');
execute upsert_tx('80', 'az', 'Əmək qüvvəsi');
execute upsert_tx('81', 'az', '81 (unknown CH)');
execute upsert_tx('90', 'az', 'Digər Məhs. və Xidm.');
execute upsert_tx('99', 'az', 'Korporativ');

prompt INSERTING into TLGNR_DIVISION_TX - HR (ENGLISH COPY)
execute upsert_tx('10', 'hr', 'Klinker & cement');
execute upsert_tx('11', 'hr', 'AFR');
execute upsert_tx('12', 'hr', '12 (unknown CH)');
execute upsert_tx('20', 'hr', 'MIC');
execute upsert_tx('30', 'hr', 'Agregati');
execute upsert_tx('40', 'hr', 'RMX Beton');
execute upsert_tx('50', 'hr', 'Betonski proizvodi');
execute upsert_tx('60', 'hr', 'Asfalt');
execute upsert_tx('70', 'hr', 'Izgradnja i asfaltiranje');
execute upsert_tx('80', 'hr', 'Trgovinski');
execute upsert_tx('81', 'hr', '81 (unknown CH)');
execute upsert_tx('90', 'hr', 'Ostali proizvodi i usluge');
execute upsert_tx('99', 'hr', 'Korporativni');

prompt INSERTING into TLGNR_DIVISION_TX - SR (ENGLISH COPY)
execute upsert_tx('10', 'sr', 'Klinker & Cement');
execute upsert_tx('11', 'sr', 'AFR');
execute upsert_tx('12', 'sr', '12 (unknown CH)');
execute upsert_tx('20', 'sr', 'MIC');
execute upsert_tx('30', 'sr', 'Agregati ');
execute upsert_tx('40', 'sr', 'RMX Beton');
execute upsert_tx('50', 'sr', 'Betonski proizvodi');
execute upsert_tx('60', 'sr', 'Asfalt');
execute upsert_tx('70', 'sr', 'Izgradnja i asfaltiranje');
execute upsert_tx('80', 'sr', 'Trejding');
execute upsert_tx('81', 'sr', '81 (unknown CH)');
execute upsert_tx('90', 'sr', 'Drugi proizvodi I usluge');
execute upsert_tx('99', 'sr', 'Korporativni');

prompt INSERTING into TLGNR_DIVISION_TX - TR
--these divisions (12,81) were present in a CH db, therefore we need to have them
execute upsert_tx('10', 'tr', 'Clinker & Cement');
execute upsert_tx('11', 'tr', 'AFR');
execute upsert_tx('12', 'tr', '12 (unknown CH)');
execute upsert_tx('20', 'tr', 'MIC');
execute upsert_tx('30', 'tr', 'Aggregates');
execute upsert_tx('40', 'tr', 'RMX Concrete');
execute upsert_tx('50', 'tr', 'Concrete products');
execute upsert_tx('60', 'tr', 'Asphalt');
execute upsert_tx('70', 'tr', 'Construction and pav');
execute upsert_tx('80', 'tr', 'Trading');
execute upsert_tx('81', 'tr', '81 (unknown CH)');
execute upsert_tx('90', 'tr', 'Other Produc.&Serv.');
execute upsert_tx('99', 'tr', 'Corporate');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;
