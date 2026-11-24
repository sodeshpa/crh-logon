-- $Revision$
-- $Date$
--
-- List based on clarification in https://aww.adnovum.ch/twiki/bin/view/CrhPegasus/ShiptoOrderSAPCleanup#Clarification_of_Pickup_Transpor

--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_sap_trsp_mode.sql ----------'

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

create or replace
procedure upsert_bs (
  p_sap_trsp_mode_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_sap_trsp_mode_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_sap_trsp_mode_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.sap_trsp_mode_cd)
    into v_exists_flag
    from tlgnr_sap_trsp_mode_bs bs
    where bs.sap_trsp_mode_cd = p_sap_trsp_mode_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_sap_trsp_mode_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_sap_trsp_mode_bs set
/* sap_trsp_mode_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          sap_trsp_mode_cd = p_sap_trsp_mode_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_sap_trsp_mode_cd || ').');
      end if;
    else
      insert into tlgnr_sap_trsp_mode_bs (
        sap_trsp_mode_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_sap_trsp_mode_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_sap_trsp_mode_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_sap_trsp_mode_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_sap_trsp_mode_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_sap_trsp_mode_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_sap_trsp_mode_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.sap_trsp_mode_cd) into v_exists_bs_flag
    from tlgnr_sap_trsp_mode_bs bs
    where bs.sap_trsp_mode_cd = p_sap_trsp_mode_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.sap_trsp_mode_cd) into v_exists_flag
      from tlgnr_sap_trsp_mode_tx tx
      where tx.sap_trsp_mode_cd = p_sap_trsp_mode_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_sap_trsp_mode_tx set
/* p_sap_trsp_mode_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          sap_trsp_mode_cd = p_sap_trsp_mode_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_sap_trsp_mode_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_sap_trsp_mode_tx (
          sap_trsp_mode_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_sap_trsp_mode_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_sap_trsp_mode_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_sap_trsp_mode_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_SAP_TRSP_MODE_BS
execute upsert_bs('10', 1);
execute upsert_bs('50', 1);
execute upsert_bs('00', 1);
execute upsert_bs('20', 1);
execute upsert_bs('30', 1);

prompt INSERTING into TLGNR_SAP_TRSP_MODE_TX - EN
execute upsert_tx('10', 'en', 'Truck');
execute upsert_tx('50', 'en', 'Single-wagon Train');
execute upsert_tx('00', 'en', 'Ex Plant (Pickup)');
execute upsert_tx('20', 'en', 'Block train');
execute upsert_tx('30', 'en', 'Ship');

prompt INSERTING into TLGNR_SAP_TRSP_MODE_TX - DE
execute upsert_tx('10', 'de', 'LKW');
execute upsert_tx('50', 'de', 'Bahnwagen');
execute upsert_tx('00', 'de', 'Ex Plant (Pickup)');
execute upsert_tx('20', 'de', 'Blockzug');
execute upsert_tx('30', 'de', 'Schiff');

prompt INSERTING into TLGNR_SAP_TRSP_MODE_TX - FR
execute upsert_tx('10', 'fr', 'Camion');
execute upsert_tx('50', 'fr', 'Train');
execute upsert_tx('00', 'fr', 'Ex Usine (Pickup)');
execute upsert_tx('20', 'fr', 'Train entier');
execute upsert_tx('30', 'fr', 'Bateau');

prompt INSERTING into TLGNR_SAP_TRSP_MODE_TX - IT
execute upsert_tx('10', 'it', 'Camion');
execute upsert_tx('50', 'it', 'Ferrovia');
execute upsert_tx('00', 'it', 'Impianto Est (Pickup)');
execute upsert_tx('20', 'it', 'Treno');
execute upsert_tx('30', 'it', 'Nave');

prompt INSERTING into TLGNR_SAP_TRSP_MODE_TX - ES
execute upsert_tx('10', 'es', 'Camión');
execute upsert_tx('50', 'es', 'Tren');
execute upsert_tx('00', 'es', 'Planta Ext (Pickup)');
execute upsert_tx('20', 'es', 'Tren completo');
execute upsert_tx('30', 'es', 'Barco');

prompt INSERTING into TLGNR_SAP_TRSP_MODE_TX - CS
execute upsert_tx('10', 'cs', 'Tahač');
execute upsert_tx('50', 'cs', 'Jednotlivé vagóny');
execute upsert_tx('00', 'cs', 'Expediční závod (EXW)');
execute upsert_tx('20', 'cs', 'Ucelený vlak');
execute upsert_tx('30', 'cs', 'Loď');

prompt INSERTING into TLGNR_SAP_TRSP_MODE_TX - SK
execute upsert_tx('10', 'sk', 'Ťahač');
execute upsert_tx('50', 'sk', 'Vlak s jedným vagónom');
execute upsert_tx('00', 'sk', 'Externá Prevádzka (Pickup)');
execute upsert_tx('20', 'sk', 'Ucelený vlak');
execute upsert_tx('30', 'sk', 'Loď');

prompt INSERTING into TLGNR_SAP_TRSP_MODE_TX - HU
execute upsert_tx('10', 'hu', 'Kamion');
execute upsert_tx('50', 'hu', 'Egy vagonos vonat');
execute upsert_tx('00', 'hu', 'Külső gyár (Felvenni)');
execute upsert_tx('20', 'hu', 'Szervény');
execute upsert_tx('30', 'hu', 'Szállítás');

prompt INSERTING into TLGNR_SAP_TRSP_MODE_TX - RO
execute upsert_tx('10', 'ro', 'Camion');
execute upsert_tx('50', 'ro', 'Tren cu 1 vagon');
execute upsert_tx('00', 'ro', 'Fabrica Ex (Ridicare)');
execute upsert_tx('20', 'ro', 'Tren');
execute upsert_tx('30', 'ro', 'Vapor');

prompt INSERTING into TLGNR_SAP_TRSP_MODE_TX - BG
execute upsert_tx('10', 'bg', 'Камион');
execute upsert_tx('50', 'bg', 'Един-вагон Влак');
execute upsert_tx('00', 'bg', 'Предишен завод (Pickup)');
execute upsert_tx('20', 'bg', 'Блок влак');
execute upsert_tx('30', 'bg', 'Кораб');

prompt INSERTING into TLGNR_SAP_TRSP_MODE_TX - RU (ENGLISH COPY)
execute upsert_tx('10', 'ru', 'автомобиль');
execute upsert_tx('50', 'ru', 'вагон');
execute upsert_tx('00', 'ru', 'Ex Plant (Pickup)');
execute upsert_tx('20', 'ru', 'маршрутный поезд');
execute upsert_tx('30', 'ru', 'баржа');

prompt INSERTING into TLGNR_SAP_TRSP_MODE_TX - PL
execute upsert_tx('10', 'pl', 'Pojazd ciężarowy');
execute upsert_tx('50', 'pl', 'Pojedynczy wagon kolejowy');
execute upsert_tx('00', 'pl', 'Ex Plant (Odbiór)');
execute upsert_tx('20', 'pl', 'Zablokowanie pociągu');
execute upsert_tx('30', 'pl', 'Statek');

prompt INSERTING into TLGNR_SAP_TRSP_MODE_TX - AZ (ENGLISH COPY)
execute upsert_tx('10', 'az', 'Truck');
execute upsert_tx('50', 'az', 'Single-wagon Train');
execute upsert_tx('00', 'az', 'Ex Plant (Pickup)');
execute upsert_tx('20', 'az', 'Block train');
execute upsert_tx('30', 'az', 'Ship');

prompt INSERTING into TLGNR_SAP_TRSP_MODE_TX - HR
execute upsert_tx('10', 'hr', 'Tegljač');
execute upsert_tx('50', 'hr', 'Jedan vagon vlaka');
execute upsert_tx('00', 'hr', 'Bivše postrojenje (Pickup)');
execute upsert_tx('20', 'hr', 'Blokiraj vlak');
execute upsert_tx('30', 'hr', 'Brod');

prompt INSERTING into TLGNR_SAP_TRSP_MODE_TX - SR
execute upsert_tx('10', 'sr', 'Kamion');
execute upsert_tx('50', 'sr', 'Voz sa jednim vagonom');
execute upsert_tx('00', 'sr', 'EXW');
execute upsert_tx('20', 'sr', 'Blok vagona');
execute upsert_tx('30', 'sr', 'Brod');

prompt INSERTING into TLGNR_SAP_TRSP_MODE_TX - TR
execute upsert_tx('10', 'tr', 'Truck');
execute upsert_tx('50', 'tr', 'Single-wagon Train');
execute upsert_tx('00', 'tr', 'Ex Plant (Pickup)');
execute upsert_tx('20', 'tr', 'Block train');
execute upsert_tx('30', 'tr', 'Ship');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

