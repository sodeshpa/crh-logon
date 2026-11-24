-- $Revision$
-- $Date$
--
-- Values based on LOGON_Interface_Price_conditions_V2.1_CDU_20090514.xls,
-- and in sync with workshop 22.6.2009 about transport conditions:
-- https://aww.adnovum.ch/twiki/bin/view/CrhPegasus/WorkshopCrh20090622

--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_cond_logon_type.sql ----------'

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
-- tlgnr_cond_logon_type_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_cond_logon_type_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_cond_logon_type_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_cond_logon_type_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.cond_logon_type_cd)
    into v_exists_flag
    from tlgnr_cond_logon_type_bs bs
    where bs.cond_logon_type_cd = p_cond_logon_type_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_cond_logon_type_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_cond_logon_type_bs set
/* cond_logon_type_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          cond_logon_type_cd = p_cond_logon_type_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_cond_logon_type_cd || ').');
      end if;
    else
      insert into tlgnr_cond_logon_type_bs (
        cond_logon_type_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_cond_logon_type_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_cond_logon_type_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_cond_logon_type_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_cond_logon_type_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_cond_logon_type_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_cond_logon_type_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.cond_logon_type_cd) into v_exists_bs_flag
    from tlgnr_cond_logon_type_bs bs
    where bs.cond_logon_type_cd = p_cond_logon_type_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.cond_logon_type_cd) into v_exists_flag
      from tlgnr_cond_logon_type_tx tx
      where tx.cond_logon_type_cd = p_cond_logon_type_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_cond_logon_type_tx set
/* p_cond_logon_type_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          cond_logon_type_cd = p_cond_logon_type_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_cond_logon_type_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_cond_logon_type_tx (
          cond_logon_type_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_cond_logon_type_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_cond_logon_type_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_cond_logon_type_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_COND_LOGON_TYPE_BS
execute upsert_bs('MP',  1);
execute upsert_bs('TC',  1);
execute upsert_bs('TRf', 1);
execute upsert_bs('TRv', 1);

prompt INSERTING into TLGNR_COND_LOGON_TYPE_TX - EN
execute upsert_tx('MP',  'en', 'Material Price Condition');
execute upsert_tx('TC',  'en', 'Transport Cost Condition');
execute upsert_tx('TRf', 'en', 'Transport Rate fixed (for Bag, tonkilometer)');
execute upsert_tx('TRv', 'en', 'Transport Rate variable (for Bag, tonkilometer)');

prompt INSERTING into TLGNR_COND_LOGON_TYPE_TX - DE
execute upsert_tx('MP',  'de', 'Materialpreis-Kondition');
execute upsert_tx('TC',  'de', 'Transportkosten-Kondition');
execute upsert_tx('TRf', 'de', 'Transport-Rate fix (für Sack, Tonnenkilometer)');
execute upsert_tx('TRv', 'de', 'Transport-Rate variabel (für Sack, Tonnenkilometer)');

prompt INSERTING into TLGNR_COND_LOGON_TYPE_TX - FR
execute upsert_tx('MP',  'fr', 'Condition Prix Matériel');
execute upsert_tx('TC',  'fr', 'Condition Coûts Transport');
execute upsert_tx('TRf', 'fr', 'Taux de transport fixé (pour Sac, tonnekilomètre)');
execute upsert_tx('TRv', 'fr', 'Taux de transport variable (pour Sac, tonnekilomètre)');

prompt INSERTING into TLGNR_COND_LOGON_TYPE_TX - IT
execute upsert_tx('MP',  'it', 'Condizione Prezzo Materiale');
execute upsert_tx('TC',  'it', 'Condizione Costi Trasporto');
execute upsert_tx('TRf', 'it', 'Tasso di Trasporto fissato (per Sacco, tonnellatachilometro)');
execute upsert_tx('TRv', 'it', 'Tasso di Trasporto variabile (per Sacco, tonnellatachilometro)');

prompt INSERTING into TLGNR_COND_LOGON_TYPE_TX - ES
execute upsert_tx('MP',  'es', 'Condición Precios Material');
execute upsert_tx('TC',  'es', 'Condición Costes Transporte');
execute upsert_tx('TRf', 'es', 'Ratio fijo de Transporte (para Bolsas, toneladakilómetro)');
execute upsert_tx('TRv', 'es', 'Ratio variable de Transporte (para Bolsas, toneladakilómetro)');

prompt INSERTING into TLGNR_COND_LOGON_TYPE_TX - CS
execute upsert_tx('MP',  'cs', 'Materiálové cenové podmínky');
execute upsert_tx('TC',  'cs', 'Dopravní cenové podmínky');
execute upsert_tx('TRf', 'cs', 'Transportní sazba fixní (pro balené zboží, tkm)');
execute upsert_tx('TRv', 'cs', 'Transportní sazba variabilní (pro balené zboží, tkm)');

prompt INSERTING into TLGNR_COND_LOGON_TYPE_TX - SK
execute upsert_tx('MP',  'sk', 'Cenové podmienky materiálu');
execute upsert_tx('TC',  'sk', 'Cenové podmienky dopravy');
execute upsert_tx('TRf', 'sk', 'Sadzba za prepravu fixná (balený, tonakilometer)');
execute upsert_tx('TRv', 'sk', 'Sadzba za prepravu variabilná (balený, tonakilometer)');

prompt INSERTING into TLGNR_COND_LOGON_TYPE_TX - HU
execute upsert_tx('MP',  'hu', 'Anyag ár kondíció');
execute upsert_tx('TC',  'hu', 'Kiszállítási ár kondíció');
execute upsert_tx('TRf', 'hu', 'Rögzített kiszállítási ár (zsák, tonna/kilométer)');
execute upsert_tx('TRv', 'hu', 'Változó kiszállítási ár (Zsák, tonna/kilométer)');

prompt INSERTING into TLGNR_COND_LOGON_TYPE_TX - RO
execute upsert_tx('MP',  'ro', 'Conditie pret material');
execute upsert_tx('TC',  'ro', 'Conditie cost transport');
execute upsert_tx('TRf', 'ro', 'Cantitate de transport fixata (pentru sac, tona/km)');
execute upsert_tx('TRv', 'ro', 'Cantitate de transport variabila (pentru sac, tona/km)');

prompt INSERTING into TLGNR_COND_LOGON_TYPE_TX - BG
execute upsert_tx('MP',  'bg', 'Състояние на цена на материал');
execute upsert_tx('TC',  'bg', 'Състояние на транспортните разходи');
execute upsert_tx('TRf', 'bg', 'Транспортният процент е фиксиран (за торба, тонкилометър)');
execute upsert_tx('TRv', 'bg', 'Транспортният процент е променлив (за торба, тонкилометър)');

prompt INSERTING into TLGNR_COND_LOGON_TYPE_TX - RU (ENGLISH COPY)
execute upsert_tx('MP',  'ru', 'условие цены материала');
execute upsert_tx('TC',  'ru', 'условие транспортных затрат');
execute upsert_tx('TRf', 'ru', 'фиксированный транспортый тариф (для тары, тонна на километр)');
execute upsert_tx('TRv', 'ru', 'переменный транспортый тариф (для тары, тонна на километр)');

prompt INSERTING into TLGNR_COND_LOGON_TYPE_TX - PL (ENGLISH COPY)
execute upsert_tx('MP',  'pl', 'Stan Ceny Material');
execute upsert_tx('TC',  'pl', 'Stan kosztu transportu');
execute upsert_tx('TRf', 'pl', 'Stawka transportu ustalona (dla worków, tona kilometr)');
execute upsert_tx('TRv', 'pl', 'Zmienna stawka transportu (dla worków ,tona kilometr)');

prompt INSERTING into TLGNR_COND_LOGON_TYPE_TX - AZ (ENGLISH COPY)
execute upsert_tx('MP',  'az', 'Material Price Condition');
execute upsert_tx('TC',  'az', 'Transport Cost Condition');
execute upsert_tx('TRf', 'az', 'Transport Rate fixed (for Bag, tonkilometer)');
execute upsert_tx('TRv', 'az', 'Transport Rate variable (for Bag, tonkilometer)');

prompt INSERTING into TLGNR_COND_LOGON_TYPE_TX - HR (ENGLISH COPY)
execute upsert_tx('MP',  'hr', 'Trošak materijala');
execute upsert_tx('TC',  'hr', 'Trošak prijevoza');
execute upsert_tx('TRf', 'hr', 'Prijevoz fiksni (za vreće, tona/kilometar)');
execute upsert_tx('TRv', 'hr', 'Prijevoz varijabilni (za vreće, tona/kilometar)');

prompt INSERTING into TLGNR_COND_LOGON_TYPE_TX - SR (ENGLISH COPY)
execute upsert_tx('MP',  'sr', 'Trošak materijala');
execute upsert_tx('TC',  'sr', 'Trošak prevoza');
execute upsert_tx('TRf', 'sr', 'Fiksni trošak prevoza (za vreće, tonakilomentara)');
execute upsert_tx('TRv', 'sr', 'Varijabilni trošak transporta (za džak, tonakilometar)');

prompt INSERTING into TLGNR_COND_LOGON_TYPE_TX - TR
execute upsert_tx('MP', 'tr', 'Material Price Condition');
execute upsert_tx('TC', 'tr', 'Transport Cost Condition');
execute upsert_tx('TRf', 'tr', 'Transport Rate fixed (for Bag, tonkilometer)');
execute upsert_tx('TRv', 'tr', 'Transport Rate variable (for Bag, tonkilometer)');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

