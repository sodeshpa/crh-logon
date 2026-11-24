-- $Revision$
-- $Date$
--
--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_uom.sql ----------'

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
-- tlgnr_uom_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_uom_bs (
  p_uom_cd                       in varchar2,
  p_ctl_act_cd                   in number,
  p_numerator_for_conversion     in number,
  p_denominator_for_conversion   in number,
  p_create_multi_order_flag      in number,
  p_available_in_web_order_flag  in number,
  p_fuzzy_quantity_flag          in number,
  p_uom_dimension_cd             in varchar2,
  p_send_to_sap                  in varchar2
)
is
  v_table_name    varchar2(48) := 'tlgnr_uom_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_uom_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.uom_cd)
    into v_exists_flag
    from tlgnr_uom_bs bs
    where bs.uom_cd = p_uom_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_uom_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_uom_bs set
/* uom_cd: cannot update because this is the key */
          numerator_for_conversion       = p_numerator_for_conversion,
          denominator_for_conversion     = p_denominator_for_conversion,
          create_multi_order_flag        = p_create_multi_order_flag,
          available_in_web_order_flag    = p_available_in_web_order_flag,
          fuzzy_quantity_flag            = p_fuzzy_quantity_flag,
          uom_dimension_cd               = p_uom_dimension_cd,
          send_to_sap                    = p_send_to_sap,
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          uom_cd = p_uom_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_uom_cd || ').');
      end if;
    else
      insert into tlgnr_uom_bs (
        uom_cd,
        numerator_for_conversion,
        denominator_for_conversion,
        create_multi_order_flag,
        available_in_web_order_flag,
        fuzzy_quantity_flag,
        uom_dimension_cd,
        send_to_sap,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_uom_cd,
        p_numerator_for_conversion,
        p_denominator_for_conversion,
        p_create_multi_order_flag,
        p_available_in_web_order_flag,
        p_fuzzy_quantity_flag,
        p_uom_dimension_cd,
        p_send_to_sap,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_uom_cd || ').');
    end if;

  end upsert_uom_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_uom_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_uom_tx (
  p_uom_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_uom_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_uom_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.uom_cd) into v_exists_bs_flag
    from tlgnr_uom_bs bs
    where bs.uom_cd = p_uom_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.uom_cd) into v_exists_flag
      from tlgnr_uom_tx tx
      where tx.uom_cd = p_uom_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_uom_tx set
/* p_uom_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          uom_cd = p_uom_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_uom_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_uom_tx (
          uom_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_uom_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_uom_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_uom_cd || ').');
    end if;

  end upsert_uom_tx;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_uom_dimension_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_uom_dimension_bs (
  p_uom_dimension_cd          in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_uom_dimension_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_uom_dimension_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.uom_dimension_cd)
    into v_exists_flag
    from tlgnr_uom_dimension_bs bs
    where bs.uom_dimension_cd = p_uom_dimension_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_uom_dimension_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_uom_dimension_bs set
/* uom_dimension_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          uom_dimension_cd = p_uom_dimension_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_uom_dimension_cd || ').');
      end if;
    else
      insert into tlgnr_uom_dimension_bs (
        uom_dimension_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_uom_dimension_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_uom_dimension_cd || ').');
    end if;

  end upsert_uom_dimension_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_uom_dimension_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_uom_dimension_tx (
  p_uom_dimension_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_uom_dimension_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_uom_dimension_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.uom_dimension_cd) into v_exists_bs_flag
    from tlgnr_uom_dimension_bs bs
    where bs.uom_dimension_cd = p_uom_dimension_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.uom_dimension_cd) into v_exists_flag
      from tlgnr_uom_dimension_tx tx
      where tx.uom_dimension_cd = p_uom_dimension_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_uom_dimension_tx set
/* p_uom_dimension_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          uom_dimension_cd = p_uom_dimension_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_uom_dimension_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_uom_dimension_tx (
          uom_dimension_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_uom_dimension_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_uom_dimension_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_uom_dimension_cd || ').');
    end if;

  end upsert_uom_dimension_tx;
/
show errors;

prompt INSERTING into TLGNR_UOM_DIMENSION_BS
-- Kept on purpose in same file as TLGNR_UOM, because it's so closely related, and UOM is based directly on UOM_DIMENSION

-- No longer use '-' for vague units. Is now as a flag on uom_bs: fuzzy_quantity_flag.
-- Reason: When looking at new orders, which have not yet been dispatched, we still want to have a vague idea what '1 LKW' is.
-- Therefore, numberator/denominator should be set also for those, and they are convertible, just not precisely but as a first guess.
execute upsert_uom_dimension_bs('-',		0);
execute upsert_uom_dimension_bs('TO',		1);
execute upsert_uom_dimension_bs('HRE',	1);
execute upsert_uom_dimension_bs('PCE',	1);
execute upsert_uom_dimension_bs('L',		0);
execute upsert_uom_dimension_bs('M',		0);

prompt INSERTING into TLGNR_UOM_BS

execute upsert_uom_bs('TO' , 1,   1,   1, 0, 1, 0, 'TO',  'TNE');
execute upsert_uom_bs('15T', 0,  15,   1, 1, 0, 1, 'TO',  null);
execute upsert_uom_bs('27T', 0,  27,   1, 1, 0, 1, 'TO',  null);
execute upsert_uom_bs('36T', 0,  36,   1, 1, 0, 1, 'TO',  null);
execute upsert_uom_bs('54T', 0,  54,   1, 1, 0, 1, 'TO',  null);
execute upsert_uom_bs('CKG', 1,   1,  10, 0, 0, 0, 'TO',  null);
execute upsert_uom_bs('HRE', 1,   1,   1, 0, 0, 0, 'HRE', 'HUR');
--added because of deliveryTimeToleranceUom on tlgnc_custorder
execute upsert_uom_bs('MIN', 1,  60,   1, 0, 0, 0, 'HRE', null);
execute upsert_uom_bs('PCE', 1,   1,   1, 0, 1, 0, 'PCE', 'PCE');
-- lajos/20120626: LOGON-4948: specific bags (25kg, 40 kg, 50kg) are removed. SAP BG (bag) is mapped (in TLGNR_MAP_SAP_UOM_BS) to BAG (general). See TLGNS_SAP_MATERIAL_ALT_UOM table for material specific conversion (numerator, denominator and base UoM).
-- TNE was once in the interface from SAP, because they used the wrong standards (should have sent 'TO'). Include, as inactive. Same with 'ST'
execute upsert_uom_bs('TNE', 0,   1,   1, 0, 0, 0, 'TO',  null);
execute upsert_uom_bs('ST' , 0,   1,   1, 0, 0, 0, 'PCE', null);

-- Vague units and logon specific units
execute upsert_uom_bs('LKW', 1,  28,   1, 1, 1, 1, 'TO',  null);
execute upsert_uom_bs('BW',  1,  60,   1, 1, 1, 1, 'TO',  null);
-- uma/20091009: create_multi_order_flag could be renamed to "create-multi-order-per-number-of-items", and for containers: per 2 containers a new order.
-- But then, rail+truck orders would have to be distinguished. Don't do this...
-- This should rather be some additional ruleset and depend on the vehicle/type as well.
execute upsert_uom_bs('CON', 1,  28,   1, 1, 1, 1, 'TO',  null);
-- lajos/20120626: LOGON-4948: SAP PF (pallet) is mapped (in TLGNR_MAP_SAP_UOM_BS) to PAL. It is a material specific alternative UoM so global conversion to its dimension is not possible (numerator, denominator and dimension is null), but fuzzy flag is removed. See TLGNS_SAP_MATERIAL_ALT_UOM table for material specific conversion (numerator, denominator and base UoM).
execute upsert_uom_bs('PAL', 1,  null, null, 0, 1, 0, null, 'PF');
-- lajos/20120626: LOGON-4948: specific pallets (with 40 and 48 bags) are removed. SAP PF (pallet) is mapped to PAL (general). See TLGNS_SAP_MATERIAL_ALT_UOM table for material specific conversion (numerator, denominator and base UoM).

execute upsert_uom_bs('L'  , 0,   1,   1, 0, 0, 0, 'L', null);
execute upsert_uom_bs('M'  , 0,   1,   1, 0, 0, 0, 'M', null);
execute upsert_uom_bs('KM' , 0,1000,   1, 0, 0, 0, 'M', null);
-- KG may be used, and is also supported, as it is a weight. Similar to CKG, add also as active!
execute upsert_uom_bs('KG' , 1,   1,1000, 0, 0, 0, 'TO', null);
-- STD (Stunde, =hour) may or may not be used, howevever, Andreas asked to stick with the already existing UOM 'HRE', therefore, adding STD as inactive, and synonym to HRE (Mails 29.7.2010 to pegasus)
execute upsert_uom_bs('STD', 0,   1,   1, 0, 0, 0, 'HRE', null);

-- lajos/20120616/LOGON-4849: base UOMs from SAP (also used as alternative UOM).
execute upsert_uom_bs('P1',  1,   null,   null, 0, 0, 0, null, null);

-- lajos/20120616/LOGON-4849: alternative UOMs from SAP.
execute upsert_uom_bs('MTQ', 1,   null,   null, 0, 0, 0, null, null);
-- uma/20120913 - temporary fix for LOGON-5350, issue in scheduling screen: Set numerator & denominator to 1. This should NOT be necessary by design of "Alternative UOM", but it fixes that problem temporarily.
update tlgnr_uom_bs set numerator_for_conversion=1, denominator_for_conversion=1 where uom_cd = 'MTQ' /* TODO uma/20120913 - remove after proper fix of LOGON-5350 */ ;

-- lajos/20120626: LOGON-4948: SAP BG (bag) is mapped (in TLGNR_MAP_SAP_UOM_BS) to BAG. It is a general bag (in contrast with S25 (bag 25kg), S40, S50) so global conversion to its dimension is not possible (numerator, denominator and dimension is null). See TLGNS_SAP_MATERIAL_ALT_UOM table for material specific conversion (numerator, denominator and base UoM).
execute upsert_uom_bs('BAG', 1,   null,   null, 0, 0, 0, null, 'BG');

execute upsert_uom_bs('EA', 1,   1,   1, 0, 1, 0, 'PCE', 'EA');

-- Kept on purpose in same file as TLGNR_UOM, because it's so closely related, and UOM is based directly on UOM_DIMENSION

-- For NGE, no extensions to the UOM / DIMENSION were requested.
-- The vision/scope document and all business cases mainly talk about CEMENT, and WEIGHING.
-- Therefore I do not think that we have to support LENGHTS and VOLUMES in the application, that would be
-- problematic because it is unclear how it would be measured, and what the logic in terminal screen and
-- order detail etc would be
-- => Add them CORRECTLY as dimension + UOM, but inactive, because not supported.

-- See email from uma, sent to NGE + SAP + cc pegasus@adnovum.ch:
-- "Unknown UOM in material export: added some as active, some as inactive + questions", 29.7.2010
prompt INSERTING into TLGNR_UOM(_DIMENSION)_TX - EN
execute upsert_uom_dimension_tx('-', 'en', 'OBSOLET: Not convertible');
execute upsert_uom_dimension_tx('HRE', 'en', 'Time, 1 hour');
execute upsert_uom_dimension_tx('L', 'en', 'Volume, 1 liter');
execute upsert_uom_dimension_tx('M', 'en', 'Length, 1 meter');
execute upsert_uom_dimension_tx('PCE', 'en', 'Pieces, 1 piece');
execute upsert_uom_dimension_tx('TO', 'en', 'Weight, 1 ton');

execute upsert_uom_tx('15T', 'en', '15 t');
execute upsert_uom_tx('27T', 'en', '27 t');
execute upsert_uom_tx('36T', 'en', '36 t');
execute upsert_uom_tx('54T', 'en', '54 t');
execute upsert_uom_tx('BAG', 'en', 'Bag');
execute upsert_uom_tx('BW', 'en', 'Railcar');
execute upsert_uom_tx('CKG', 'en', 'centi kg');
execute upsert_uom_tx('CON', 'en', 'Container');
execute upsert_uom_tx('EA', 'en', 'Each');
execute upsert_uom_tx('HRE', 'en', 'hre');
execute upsert_uom_tx('KG', 'en', 'Kilogramm');
execute upsert_uom_tx('KM', 'en', 'Kilometer');
execute upsert_uom_tx('L', 'en', 'Liter');
execute upsert_uom_tx('LKW', 'en', 'Truck');
execute upsert_uom_tx('M', 'en', 'Meter');
execute upsert_uom_tx('MIN', 'en', 'min');
execute upsert_uom_tx('MTQ', 'en', 'Cubic metre');
execute upsert_uom_tx('P1', 'en', 'Percent');
execute upsert_uom_tx('PAL', 'en', 'Pallet');
execute upsert_uom_tx('PCE', 'en', 'pce');
execute upsert_uom_tx('ST', 'en', 'unknown');
execute upsert_uom_tx('STD', 'en', 'Hour');
execute upsert_uom_tx('TNE', 'en', 'tons (TNE)');
execute upsert_uom_tx('TO', 'en', 'tons');

prompt INSERTING into TLGNR_UOM(_DIMENSION)_TX - DE
execute upsert_uom_dimension_tx('-', 'de', 'OBSOLET: Vage Einheit');
execute upsert_uom_dimension_tx('HRE', 'de', 'Zeit, 1 Stunde');
execute upsert_uom_dimension_tx('L', 'de', 'Volumen, 1 Liter');
execute upsert_uom_dimension_tx('M', 'de', 'Länge, 1 Meter');
execute upsert_uom_dimension_tx('PCE', 'de', 'Stücke/Anzahl, 1 Stück');
execute upsert_uom_dimension_tx('TO', 'de', 'Gewicht, 1 Tonne');

execute upsert_uom_tx('15T', 'de', '15 t');
execute upsert_uom_tx('27T', 'de', '27 t');
execute upsert_uom_tx('36T', 'de', '36 t');
execute upsert_uom_tx('54T', 'de', '54 t');
execute upsert_uom_tx('BAG', 'de', 'Sack');
execute upsert_uom_tx('BW', 'de', 'Bahnwagen');
execute upsert_uom_tx('CKG', 'de', 'ckg');
execute upsert_uom_tx('CON', 'de', 'Container');
execute upsert_uom_tx('EA', 'de', 'Jede');
execute upsert_uom_tx('HRE', 'de', 'h');
execute upsert_uom_tx('KG', 'de', 'Kilogramm');
execute upsert_uom_tx('KM', 'de', 'Kilometer');
execute upsert_uom_tx('L', 'de', 'Liter');
execute upsert_uom_tx('LKW', 'de', 'LKW');
execute upsert_uom_tx('M', 'de', 'Meter');
execute upsert_uom_tx('MIN', 'de', 'min');
execute upsert_uom_tx('MTQ', 'de', 'Kubikmeter');
execute upsert_uom_tx('P1', 'de', 'Prozent');
execute upsert_uom_tx('PAL', 'de', 'Palett');
execute upsert_uom_tx('PCE', 'de', 'pce');
execute upsert_uom_tx('ST', 'de', 'unbekannt');
execute upsert_uom_tx('STD', 'de', 'Stunde');
execute upsert_uom_tx('TNE', 'de', 'Tonnen (TNE)');
execute upsert_uom_tx('TO', 'de', 'Tonnen');

prompt INSERTING into TLGNR_UOM(_DIMENSION)_TX - FR
execute upsert_uom_dimension_tx('-', 'fr', 'OBSOLÈTE: Non convertible');
execute upsert_uom_dimension_tx('HRE', 'fr', 'Temps, 1 heure');
execute upsert_uom_dimension_tx('L', 'fr', 'Volume, 1 litre');
execute upsert_uom_dimension_tx('M', 'fr', 'Longueur, 1 mètre');
execute upsert_uom_dimension_tx('PCE', 'fr', 'Pièces, 1 pièce');
execute upsert_uom_dimension_tx('TO', 'fr', 'Poids, 1 tonne');

execute upsert_uom_tx('15T', 'fr', '15 t');
execute upsert_uom_tx('27T', 'fr', '27 t');
execute upsert_uom_tx('36T', 'fr', '36 t');
execute upsert_uom_tx('54T', 'fr', '54 t');
execute upsert_uom_tx('BAG', 'fr', 'Sac');
execute upsert_uom_tx('BW', 'fr', 'Wagon');
execute upsert_uom_tx('CKG', 'fr', 'centi Kg');
execute upsert_uom_tx('CON', 'fr', 'Container');
execute upsert_uom_tx('EA', 'fr', 'Chaque');
execute upsert_uom_tx('HRE', 'fr', 'h');
execute upsert_uom_tx('KG', 'fr', 'Kilogramme');
execute upsert_uom_tx('KM', 'fr', 'Kilomètre');
execute upsert_uom_tx('L', 'fr', 'Litre');
execute upsert_uom_tx('LKW', 'fr', 'Camion');
execute upsert_uom_tx('M', 'fr', 'Mètre');
execute upsert_uom_tx('MIN', 'fr', 'min');
execute upsert_uom_tx('MTQ', 'fr', 'Mètre cube');
execute upsert_uom_tx('P1', 'fr', 'Pour cent');
execute upsert_uom_tx('PAL', 'fr', 'Palette');
execute upsert_uom_tx('PCE', 'fr', 'Unité');
execute upsert_uom_tx('ST', 'fr', 'inconnue');
execute upsert_uom_tx('STD', 'fr', 'Heure');
execute upsert_uom_tx('TNE', 'fr', 'Tonnes');
execute upsert_uom_tx('TO', 'fr', 'Tonnes');

prompt INSERTING into TLGNR_UOM(_DIMENSION)_TX - IT
execute upsert_uom_dimension_tx('-', 'it', 'OBSOLET: Non convertibile');
execute upsert_uom_dimension_tx('HRE', 'it', 'Tempo, 1 ora');
execute upsert_uom_dimension_tx('L', 'it', 'Volume, 1 litro');
execute upsert_uom_dimension_tx('M', 'it', 'Lunghezza, 1 metro');
execute upsert_uom_dimension_tx('PCE', 'it', 'Pezzi, 1 pezzo');
execute upsert_uom_dimension_tx('TO', 'it', 'Peso, 1 tonnellata');

execute upsert_uom_tx('15T', 'it', '15 t');
execute upsert_uom_tx('27T', 'it', '27 t');
execute upsert_uom_tx('36T', 'it', '36 t');
execute upsert_uom_tx('54T', 'it', '54 t');
execute upsert_uom_tx('BAG', 'it', 'Sacco');
execute upsert_uom_tx('BW', 'it', 'Vagone');
execute upsert_uom_tx('CKG', 'it', 'cent Kg');
execute upsert_uom_tx('CON', 'it', 'Container');
execute upsert_uom_tx('EA', 'it', 'Ognuno');
execute upsert_uom_tx('HRE', 'it', 'h');
execute upsert_uom_tx('KG', 'it', 'Chilogrammo');
execute upsert_uom_tx('KM', 'it', 'Chilometro');
execute upsert_uom_tx('L', 'it', 'Litro');
execute upsert_uom_tx('LKW', 'it', 'Camion');
execute upsert_uom_tx('M', 'it', 'Metro');
execute upsert_uom_tx('MIN', 'it', 'min');
execute upsert_uom_tx('MTQ', 'it', 'Metro cubo');
execute upsert_uom_tx('P1', 'it', 'Per cento');
execute upsert_uom_tx('PAL', 'it', 'Bancale');
execute upsert_uom_tx('PCE', 'it', 'Unità');
execute upsert_uom_tx('ST', 'it', 'sconosciuto');
execute upsert_uom_tx('STD', 'it', 'Ora');
execute upsert_uom_tx('TNE', 'it', 'Tonnellate');
execute upsert_uom_tx('TO', 'it', 'Tonnellate');

prompt INSERTING into TLGNR_UOM(_DIMENSION)_TX - ES
execute upsert_uom_dimension_tx('-', 'es', 'OBSOLETO: No convertible');
execute upsert_uom_dimension_tx('HRE', 'es', 'Hora, 1 hora');
execute upsert_uom_dimension_tx('L', 'es', 'Volumen, 1 litro');
execute upsert_uom_dimension_tx('M', 'es', 'Longitud, 1 metro');
execute upsert_uom_dimension_tx('PCE', 'es', 'Piezas, 1 pieza');
execute upsert_uom_dimension_tx('TO', 'es', 'Peso, 1 tonelada');

execute upsert_uom_tx('15T', 'es', '15 t');
execute upsert_uom_tx('27T', 'es', '27 t');
execute upsert_uom_tx('36T', 'es', '36 t');
execute upsert_uom_tx('54T', 'es', '54 t');
execute upsert_uom_tx('BAG', 'es', 'Saco');
execute upsert_uom_tx('BW', 'es', 'Vagón');
execute upsert_uom_tx('CKG', 'es', 'centikg');
execute upsert_uom_tx('CON', 'es', 'Contenedor');
execute upsert_uom_tx('EA', 'es', 'Pieza');
execute upsert_uom_tx('HRE', 'es', 'hr');
execute upsert_uom_tx('KG', 'es', 'Kilogramo');
execute upsert_uom_tx('KM', 'es', 'Kilómetro');
execute upsert_uom_tx('L', 'es', 'Litro');
execute upsert_uom_tx('LKW', 'es', 'Camión');
execute upsert_uom_tx('M', 'es', 'Metro');
execute upsert_uom_tx('MIN', 'es', 'min');
execute upsert_uom_tx('MTQ', 'es', 'Metro cúbico');
execute upsert_uom_tx('P1', 'es', 'Por ciento');
execute upsert_uom_tx('PAL', 'es', 'Palé');
execute upsert_uom_tx('PCE', 'es', 'Pieza');
execute upsert_uom_tx('ST', 'es', 'desconocido');
execute upsert_uom_tx('STD', 'es', 'Hora');
execute upsert_uom_tx('TNE', 'es', 'Toneladas (TNE)');
execute upsert_uom_tx('TO', 'es', 'Toneladas');

execute upsert_uom_dimension_tx('-', 'cs', 'Neplatná jednotka: nelze převést');
execute upsert_uom_dimension_tx('HRE', 'cs', 'Čas, 1 hod');
execute upsert_uom_dimension_tx('L', 'cs', 'Objem, 1litr');
execute upsert_uom_dimension_tx('M', 'cs', 'Délka, 1 m');
execute upsert_uom_dimension_tx('PCE', 'cs', 'Kusy, 1 ks');
execute upsert_uom_dimension_tx('TO', 'cs', 'Váha, 1 tuna');

execute upsert_uom_tx('15T', 'cs', '15 t');
execute upsert_uom_tx('27T', 'cs', '27 t');
execute upsert_uom_tx('36T', 'cs', '36 t');
execute upsert_uom_tx('54T', 'cs', '54 t');
execute upsert_uom_tx('BAG', 'cs', 'Balené zboží');
execute upsert_uom_tx('BW', 'cs', 'Železniční vozidlo');
execute upsert_uom_tx('CKG', 'cs', 'centi kg');
execute upsert_uom_tx('CON', 'cs', 'Kontejner');
execute upsert_uom_tx('EA', 'cs', 'Each');
execute upsert_uom_tx('HRE', 'cs', 'hre');
execute upsert_uom_tx('KG', 'cs', 'Kilogram');
execute upsert_uom_tx('KM', 'cs', 'Kilometr');
execute upsert_uom_tx('L', 'cs', 'Litr');
execute upsert_uom_tx('LKW', 'cs', 'Vozidlo');
execute upsert_uom_tx('M', 'cs', 'Metr');
execute upsert_uom_tx('MIN', 'cs', 'min');
execute upsert_uom_tx('MTQ', 'cs', 'Krychlový metr');
execute upsert_uom_tx('P1', 'cs', 'Procento');
execute upsert_uom_tx('PAL', 'cs', 'Paleta');
execute upsert_uom_tx('PCE', 'cs', 'kusy');
execute upsert_uom_tx('ST', 'cs', 'neznámý');
execute upsert_uom_tx('STD', 'cs', 'Hodina');
execute upsert_uom_tx('TNE', 'cs', 'tuny (TNE)');
execute upsert_uom_tx('TO', 'cs', 'tuny');

prompt INSERTING into TLGNR_UOM(_DIMENSION)_TX - SK
execute upsert_uom_dimension_tx('-', 'sk', 'Zastaralé: nekonvertibilné');
execute upsert_uom_dimension_tx('HRE', 'sk', 'Čas, 1 hodina');
execute upsert_uom_dimension_tx('L', 'sk', 'Objem, 1 liter');
execute upsert_uom_dimension_tx('M', 'sk', 'Dĺžka, 1 meter');
execute upsert_uom_dimension_tx('PCE', 'sk', 'Kusy, 1 kus');
execute upsert_uom_dimension_tx('TO', 'sk', 'Váha, 1 tona');

execute upsert_uom_tx('15T', 'sk', '15 t');
execute upsert_uom_tx('27T', 'sk', '27 t');
execute upsert_uom_tx('36T', 'sk', '36 t');
execute upsert_uom_tx('54T', 'sk', '54 t');
execute upsert_uom_tx('BAG', 'sk', 'Balený');
execute upsert_uom_tx('BW', 'sk', 'Vozeň');
execute upsert_uom_tx('CKG', 'sk', 'centi kg');
execute upsert_uom_tx('CON', 'sk', 'Kontajner');
execute upsert_uom_tx('EA', 'sk', 'Spolu');
execute upsert_uom_tx('HRE', 'sk', 'hod');
execute upsert_uom_tx('KG', 'sk', 'Kilogram');
execute upsert_uom_tx('KM', 'sk', 'Kilometer');
execute upsert_uom_tx('L', 'sk', 'Liter');
execute upsert_uom_tx('LKW', 'sk', 'Ťahač');
execute upsert_uom_tx('M', 'sk', 'Meter');
execute upsert_uom_tx('MIN', 'sk', 'min');
execute upsert_uom_tx('MTQ', 'sk', 'Kubický meter');
execute upsert_uom_tx('P1', 'sk', 'Percento');
execute upsert_uom_tx('PAL', 'sk', 'Paleta');
execute upsert_uom_tx('PCE', 'sk', 'ks');
execute upsert_uom_tx('ST', 'sk', 'neznámy');
execute upsert_uom_tx('STD', 'sk', 'Hodina');
execute upsert_uom_tx('TNE', 'sk', 'tony (TNE)');
execute upsert_uom_tx('TO', 'sk', 'tony');

prompt INSERTING into TLGNR_UOM(_DIMENSION)_TX - HU
execute upsert_uom_dimension_tx('-', 'hu', 'Nem váltható át');
execute upsert_uom_dimension_tx('HRE', 'hu', 'Idő, 1 óra');
execute upsert_uom_dimension_tx('L', 'hu', 'Térfogat, 1 liter');
execute upsert_uom_dimension_tx('M', 'hu', 'Hossz, 1 méter');
execute upsert_uom_dimension_tx('PCE', 'hu', 'Darab, 1 darab');
execute upsert_uom_dimension_tx('TO', 'hu', 'Súly, 1 tonna');

execute upsert_uom_tx('15T', 'hu', '15 t');
execute upsert_uom_tx('27T', 'hu', '27 t');
execute upsert_uom_tx('36T', 'hu', '36 t');
execute upsert_uom_tx('54T', 'hu', '54 t');
execute upsert_uom_tx('BAG', 'hu', 'Zsák');
execute upsert_uom_tx('BW', 'hu', 'Motorkocsi (vasúti)');
execute upsert_uom_tx('CKG', 'hu', 'centikg');
execute upsert_uom_tx('CON', 'hu', 'Konténer');
execute upsert_uom_tx('EA', 'hu', 'Összes');
execute upsert_uom_tx('HRE', 'hu', 'óra');
execute upsert_uom_tx('KG', 'hu', 'Kilogramm');
execute upsert_uom_tx('KM', 'hu', 'Kilométer');
execute upsert_uom_tx('L', 'hu', 'Liter');
execute upsert_uom_tx('LKW', 'hu', 'Kamion');
execute upsert_uom_tx('M', 'hu', 'Méter');
execute upsert_uom_tx('MIN', 'hu', 'min');
execute upsert_uom_tx('MTQ', 'hu', 'Köbméter');
execute upsert_uom_tx('P1', 'hu', 'Százalék');
execute upsert_uom_tx('PAL', 'hu', 'Raklap');
execute upsert_uom_tx('PCE', 'hu', 'Darab');
execute upsert_uom_tx('ST', 'hu', 'Ismeretlen');
execute upsert_uom_tx('STD', 'hu', 'Óra');
execute upsert_uom_tx('TNE', 'hu', 'Tonna (ME)');
execute upsert_uom_tx('TO', 'hu', 'Tonna');

prompt INSERTING into TLGNR_UOM(_DIMENSION)_TX - RO
execute upsert_uom_dimension_tx('-', 'ro', 'Invechit: nu poate fi convertit');
execute upsert_uom_dimension_tx('HRE', 'ro', 'Timp, 1 ora');
execute upsert_uom_dimension_tx('L', 'ro', 'Volum, 1 litru');
execute upsert_uom_dimension_tx('M', 'ro', 'Lungime, 1 metru');
execute upsert_uom_dimension_tx('PCE', 'ro', 'Piese, 1 piesa');
execute upsert_uom_dimension_tx('TO', 'ro', 'Greutate, 1 tona');

execute upsert_uom_tx('15T', 'ro', '15 t');
execute upsert_uom_tx('27T', 'ro', '27 t');
execute upsert_uom_tx('36T', 'ro', '36 t');
execute upsert_uom_tx('54T', 'ro', '54 t');
execute upsert_uom_tx('BAG', 'ro', 'Sac');
execute upsert_uom_tx('BW', 'ro', 'Tren');
execute upsert_uom_tx('CKG', 'ro', 'cg');
execute upsert_uom_tx('CON', 'ro', 'Container');
execute upsert_uom_tx('EA', 'ro', 'Buc');
execute upsert_uom_tx('HRE', 'ro', 'ora');
execute upsert_uom_tx('KG', 'ro', 'Kilogram');
execute upsert_uom_tx('KM', 'ro', 'Kilometru');
execute upsert_uom_tx('L', 'ro', 'Litru');
execute upsert_uom_tx('LKW', 'ro', 'Camion');
execute upsert_uom_tx('M', 'ro', 'Metru');
execute upsert_uom_tx('MIN', 'ro', 'min');
execute upsert_uom_tx('MTQ', 'ro', 'Metru cub');
execute upsert_uom_tx('P1', 'ro', 'Procent');
execute upsert_uom_tx('PAL', 'ro', 'Palet');
execute upsert_uom_tx('PCE', 'ro', 'bucata');
execute upsert_uom_tx('ST', 'ro', 'necunoscut');
execute upsert_uom_tx('STD', 'ro', 'Ora');
execute upsert_uom_tx('TNE', 'ro', 'tone (TNE)');
execute upsert_uom_tx('TO', 'ro', 'tone');

prompt INSERTING into TLGNR_UOM(_DIMENSION)_TX - BG
execute upsert_uom_dimension_tx('-', 'bg', 'Не сгъваем');
execute upsert_uom_dimension_tx('HRE', 'bg', 'Време, 1 час');
execute upsert_uom_dimension_tx('L', 'bg', 'Обем, 1 литър');
execute upsert_uom_dimension_tx('M', 'bg', 'Дължина, 1 метър');
execute upsert_uom_dimension_tx('PCE', 'bg', 'Части, 1 парче');
execute upsert_uom_dimension_tx('TO', 'bg', 'Тегло, 1 тон');

execute upsert_uom_tx('15T', 'bg', '15 t');
execute upsert_uom_tx('27T', 'bg', '27 t');
execute upsert_uom_tx('36T', 'bg', '36 t');
execute upsert_uom_tx('54T', 'bg', '54 t');
execute upsert_uom_tx('BAG', 'bg', 'Торба');
execute upsert_uom_tx('BW', 'bg', 'Мотриса');
execute upsert_uom_tx('CKG', 'bg', 'centi kg');
execute upsert_uom_tx('CON', 'bg', 'Контейнер');
execute upsert_uom_tx('EA', 'bg', 'всеки');
execute upsert_uom_tx('HRE', 'bg', 'час');
execute upsert_uom_tx('KG', 'bg', 'Килограм');
execute upsert_uom_tx('KM', 'bg', 'Километър');
execute upsert_uom_tx('L', 'bg', 'Литър');
execute upsert_uom_tx('LKW', 'bg', 'Камион');
execute upsert_uom_tx('M', 'bg', 'Метър');
execute upsert_uom_tx('MIN', 'bg', 'мин');
execute upsert_uom_tx('MTQ', 'bg', 'Кубичен метър');
execute upsert_uom_tx('P1', 'bg', 'Процент');
execute upsert_uom_tx('PAL', 'bg', 'Палет');
execute upsert_uom_tx('PCE', 'bg', 'Парче');
execute upsert_uom_tx('ST', 'bg', 'непознат');
execute upsert_uom_tx('STD', 'bg', 'Час');
execute upsert_uom_tx('TNE', 'bg', 'Тонове (TNE)');
execute upsert_uom_tx('TO', 'bg', 'Тонове');

prompt INSERTING into TLGNR_UOM(_DIMENSION)_TX - RU
execute upsert_uom_dimension_tx('-', 'ru', 'OBSOLET: Не конвертирован');
execute upsert_uom_dimension_tx('HRE', 'ru', 'Время, 1 час');
execute upsert_uom_dimension_tx('L', 'ru', 'Объем, 1 литр');
execute upsert_uom_dimension_tx('M', 'ru', 'Длина, 1 метр');
execute upsert_uom_dimension_tx('PCE', 'ru', 'Штуки, 1 шт');
execute upsert_uom_dimension_tx('TO', 'ru', 'Вес, 1 тонна');

execute upsert_uom_tx('15T', 'ru', '15 t');
execute upsert_uom_tx('27T', 'ru', '27 t');
execute upsert_uom_tx('36T', 'ru', '36 t');
execute upsert_uom_tx('54T', 'ru', '54 t');
execute upsert_uom_tx('BAG', 'ru', 'мешок');
execute upsert_uom_tx('BW', 'ru', 'вагон');
execute upsert_uom_tx('CKG', 'ru', 'centi кг');
execute upsert_uom_tx('CON', 'ru', 'контейнер');
execute upsert_uom_tx('EA', 'ru', 'каждый');
execute upsert_uom_tx('HRE', 'ru', 'час');
execute upsert_uom_tx('KG', 'ru', 'килограмм');
execute upsert_uom_tx('KM', 'ru', 'километр');
execute upsert_uom_tx('L', 'ru', 'литр');
execute upsert_uom_tx('LKW', 'ru', 'автомобиль');
execute upsert_uom_tx('M', 'ru', 'метр');
execute upsert_uom_tx('MIN', 'ru', 'мин');
execute upsert_uom_tx('MTQ', 'ru', 'кубический метр');
execute upsert_uom_tx('P1', 'ru', 'процент');
execute upsert_uom_tx('PAL', 'ru', 'паллета');
execute upsert_uom_tx('PCE', 'ru', 'шт');
execute upsert_uom_tx('ST', 'ru', 'неизвестно');
execute upsert_uom_tx('STD', 'ru', 'час');
execute upsert_uom_tx('TNE', 'ru', 'тонны (т)');
execute upsert_uom_tx('TO', 'ru', 'тонны');

prompt INSERTING into TLGNR_UOM(_DIMENSION)_TX - PL
execute upsert_uom_dimension_tx('-', 'pl', 'OBSOLET : Nie odwracalny');
execute upsert_uom_dimension_tx('HRE', 'pl', 'Czas, 1 godzina');
execute upsert_uom_dimension_tx('L', 'pl', 'Objętość, 1 litr');
execute upsert_uom_dimension_tx('M', 'pl', 'Długość, 1 metr');
execute upsert_uom_dimension_tx('PCE', 'pl', 'Szt., 1 szt.');
execute upsert_uom_dimension_tx('TO', 'pl', 'Waga, 1 tona');

execute upsert_uom_tx('15T', 'pl', '15 t');
execute upsert_uom_tx('27T', 'pl', '27 t');
execute upsert_uom_tx('36T', 'pl', '36 t');
execute upsert_uom_tx('54T', 'pl', '54 t');
execute upsert_uom_tx('BAG', 'pl', 'Torba');
execute upsert_uom_tx('BW', 'pl', 'Lokomotywa');
execute upsert_uom_tx('CKG', 'pl', 'centi kg');
execute upsert_uom_tx('CON', 'pl', 'Pojemnik');
execute upsert_uom_tx('EA', 'pl', 'Each');
execute upsert_uom_tx('HRE', 'pl', 'hre');
execute upsert_uom_tx('KG', 'pl', 'Kilogram');
execute upsert_uom_tx('KM', 'pl', 'Kilometr');
execute upsert_uom_tx('L', 'pl', 'Litr');
execute upsert_uom_tx('LKW', 'pl', 'Ciężarówka');
execute upsert_uom_tx('M', 'pl', 'Metr');
execute upsert_uom_tx('MIN', 'pl', 'min');
execute upsert_uom_tx('MTQ', 'pl', 'Metr sześcienny');
execute upsert_uom_tx('P1', 'pl', 'Procent');
execute upsert_uom_tx('PAL', 'pl', 'Paleta');
execute upsert_uom_tx('PCE', 'pl', 'szt.');
execute upsert_uom_tx('ST', 'pl', 'nieznany');
execute upsert_uom_tx('STD', 'pl', 'Godzina');
execute upsert_uom_tx('TNE', 'pl', 'ton (TNE)');
execute upsert_uom_tx('TO', 'pl', 'tony');

prompt INSERTING into TLGNR_UOM(_DIMENSION)_TX - AZ
execute upsert_uom_dimension_tx('-', 'az', 'OBSOLET: Dönərsiz');
execute upsert_uom_dimension_tx('HRE', 'az', 'Vaxt, 1 saat');
execute upsert_uom_dimension_tx('L', 'az', 'Həcm, 1 liter');
execute upsert_uom_dimension_tx('M', 'az', 'Uzunluq, 1 metr');
execute upsert_uom_dimension_tx('PCE', 'az', 'Ədədlə, 1 ədəd');
execute upsert_uom_dimension_tx('TO', 'az', 'Çəki, 1 ton');

execute upsert_uom_tx('15T', 'az', '15 t');
execute upsert_uom_tx('27T', 'az', '27 t');
execute upsert_uom_tx('36T', 'az', '36 t');
execute upsert_uom_tx('54T', 'az', '54 t');
execute upsert_uom_tx('BAG', 'az', 'Kisə');
execute upsert_uom_tx('BW', 'az', 'Dəmiryol vaqonu');
execute upsert_uom_tx('CKG', 'az', 'santi kq');
execute upsert_uom_tx('CON', 'az', 'Konteyner');
execute upsert_uom_tx('EA', 'az', 'Hər');
execute upsert_uom_tx('HRE', 'az', 'hre');
execute upsert_uom_tx('KG', 'az', 'Kiloqram');
execute upsert_uom_tx('KM', 'az', 'Kilometr');
execute upsert_uom_tx('L', 'az', 'Liter');
execute upsert_uom_tx('LKW', 'az', 'Yük maşını');
execute upsert_uom_tx('M', 'az', 'Metr');
execute upsert_uom_tx('MIN', 'az', 'min');
execute upsert_uom_tx('MTQ', 'az', 'Kubmetr');
execute upsert_uom_tx('P1', 'az', 'Faiz');
execute upsert_uom_tx('PAL', 'az', 'Altlıq');
execute upsert_uom_tx('PCE', 'az', 'pce');
execute upsert_uom_tx('ST', 'az', 'məlum deyil');
execute upsert_uom_tx('STD', 'az', 'Saat');
execute upsert_uom_tx('TNE', 'az', 'ton (TNE)');
execute upsert_uom_tx('TO', 'az', 'ton');

prompt INSERTING into TLGNR_UOM(_DIMENSION)_TX - HR
execute upsert_uom_dimension_tx('-', 'hr', 'OBSOLET: Nije konvertibiln');
execute upsert_uom_dimension_tx('HRE', 'hr', 'Vrijeme, 1 sat');
execute upsert_uom_dimension_tx('L', 'hr', 'Volumen, 1 litra');
execute upsert_uom_dimension_tx('M', 'hr', 'Dužina, 1 metar');
execute upsert_uom_dimension_tx('PCE', 'hr', 'Komadi, 1 komad');
execute upsert_uom_dimension_tx('TO', 'hr', 'Težina, 1 tona');

execute upsert_uom_tx('15T', 'hr', '15 t');
execute upsert_uom_tx('27T', 'hr', '27 t');
execute upsert_uom_tx('36T', 'hr', '36 t');
execute upsert_uom_tx('54T', 'hr', '54 t');
execute upsert_uom_tx('BAG', 'hr', 'Vreće');
execute upsert_uom_tx('BW', 'hr', 'Motorni vlak');
execute upsert_uom_tx('CKG', 'hr', 'centi kg');
execute upsert_uom_tx('CON', 'hr', 'Kontejner');
execute upsert_uom_tx('EA', 'hr', 'Svaki');
execute upsert_uom_tx('HRE', 'hr', 'sat');
execute upsert_uom_tx('KG', 'hr', 'Kilogramm');
execute upsert_uom_tx('KM', 'hr', 'Kilometar');
execute upsert_uom_tx('L', 'hr', 'Litra');
execute upsert_uom_tx('LKW', 'hr', 'Tegljač');
execute upsert_uom_tx('M', 'hr', 'Metar');
execute upsert_uom_tx('MIN', 'hr', 'min');
execute upsert_uom_tx('MTQ', 'hr', 'Kubni metar');
execute upsert_uom_tx('P1', 'hr', 'Postotak');
execute upsert_uom_tx('PAL', 'hr', 'Paleta');
execute upsert_uom_tx('PCE', 'hr', 'komad');
execute upsert_uom_tx('ST', 'hr', 'nepoznat');
execute upsert_uom_tx('STD', 'hr', 'Sat');
execute upsert_uom_tx('TNE', 'hr', 'tona (TNE)');
execute upsert_uom_tx('TO', 'hr', 'tona');

prompt INSERTING into TLGNR_UOM(_DIMENSION)_TX - SR
execute upsert_uom_dimension_tx('-', 'sr', 'OBSOLET: Nije konvertibilan');
execute upsert_uom_dimension_tx('HRE', 'sr', 'Vreme, 1 sat');
execute upsert_uom_dimension_tx('L', 'sr', 'Zapremina, 1 litar');
execute upsert_uom_dimension_tx('M', 'sr', 'Dužina, 1 metar');
execute upsert_uom_dimension_tx('PCE', 'sr', 'Komada, 1 komad');
execute upsert_uom_dimension_tx('TO', 'sr', 'Težina, 1 tona');

execute upsert_uom_tx('15T', 'sr', '15 t');
execute upsert_uom_tx('27T', 'sr', '27 t');
execute upsert_uom_tx('36T', 'sr', '36 t');
execute upsert_uom_tx('54T', 'sr', '54 t');
execute upsert_uom_tx('BAG', 'sr', 'Vreće');
execute upsert_uom_tx('BW', 'sr', 'Railcar');
execute upsert_uom_tx('CKG', 'sr', 'centi kg');
execute upsert_uom_tx('CON', 'sr', 'Kontejner');
execute upsert_uom_tx('EA', 'sr', 'Each');
execute upsert_uom_tx('HRE', 'sr', 'hre');
execute upsert_uom_tx('KG', 'sr', 'Kilogram');
execute upsert_uom_tx('KM', 'sr', 'Kilometar');
execute upsert_uom_tx('L', 'sr', 'Litra');
execute upsert_uom_tx('LKW', 'sr', 'Kamion');
execute upsert_uom_tx('M', 'sr', 'Metar');
execute upsert_uom_tx('MIN', 'sr', 'min');
execute upsert_uom_tx('MTQ', 'sr', 'Kubni metar');
execute upsert_uom_tx('P1', 'sr', 'Procenat');
execute upsert_uom_tx('PAL', 'sr', 'Paleta');
execute upsert_uom_tx('PCE', 'sr', 'komad ');
execute upsert_uom_tx('ST', 'sr', 'nepoznat');
execute upsert_uom_tx('STD', 'sr', 'Sat');
execute upsert_uom_tx('TNE', 'sr', 'tona (TNE)');
execute upsert_uom_tx('TO', 'sr', 'tona');

prompt INSERTING into TLGNR_UOM(_DIMENSION)_TX - TR
execute upsert_uom_dimension_tx('-', 'tr', 'OBSOLET: Not convertible');
execute upsert_uom_dimension_tx('HRE', 'tr', 'Time, 1 hour');
execute upsert_uom_dimension_tx('L', 'tr', 'Volume, 1 liter');
execute upsert_uom_dimension_tx('M', 'tr', 'Length, 1 meter');
execute upsert_uom_dimension_tx('PCE', 'tr', 'Pieces, 1 piece');
execute upsert_uom_dimension_tx('TO', 'tr', 'Weight, 1 ton');

execute upsert_uom_tx('15T', 'tr', '15 t');
execute upsert_uom_tx('27T', 'tr', '27 t');
execute upsert_uom_tx('36T', 'tr', '36 t');
execute upsert_uom_tx('54T', 'tr', '54 t');
execute upsert_uom_tx('BAG', 'tr', 'Bag');
execute upsert_uom_tx('BW', 'tr', 'Railcar');
execute upsert_uom_tx('CKG', 'tr', 'centi kg');
execute upsert_uom_tx('CON', 'tr', 'Container');
execute upsert_uom_tx('EA', 'tr', 'Each');
execute upsert_uom_tx('HRE', 'tr', 'hre');
execute upsert_uom_tx('KG', 'tr', 'Kilogramm');
execute upsert_uom_tx('KM', 'tr', 'Kilometer');
execute upsert_uom_tx('L', 'tr', 'Liter');
execute upsert_uom_tx('LKW', 'tr', 'Truck');
execute upsert_uom_tx('M', 'tr', 'Meter');
execute upsert_uom_tx('MIN', 'tr', 'min');
execute upsert_uom_tx('MTQ', 'tr', 'Cubic metre');
execute upsert_uom_tx('P1', 'tr', 'Percent');
execute upsert_uom_tx('PAL', 'tr', 'Pallet');
execute upsert_uom_tx('PCE', 'tr', 'pce');
execute upsert_uom_tx('ST', 'tr', 'unknown');
execute upsert_uom_tx('STD', 'tr', 'Hour');
execute upsert_uom_tx('TNE', 'tr', 'tons (TNE)');
execute upsert_uom_tx('TO', 'tr', 'tons');

commit;

drop procedure upsert_uom_bs;
drop procedure upsert_uom_tx;
drop procedure upsert_uom_dimension_bs;
drop procedure upsert_uom_dimension_tx;
drop function is_different_number;
drop function is_different_varchar2;

